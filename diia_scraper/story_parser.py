"""Story parsing helpers for Diia.Business success cases.

The parser is intentionally heuristic: it searches for section-like headings in
Ukrainian/English and gracefully falls back to positional assignment when
headings are absent. The goal is to standardise content for downstream
analytics without relying on heavy NLP dependencies.
"""

from __future__ import annotations

import json
import re
from dataclasses import dataclass
from typing import Dict, Iterable, List, Optional, Tuple

# Canonical order of analytic blocks requested by the user.
BLOCK_ORDER: List[str] = [
    "Introduction",
    "Case Description",
    "Challenges",
    "Solutions Found",
    "Effectiveness Analysis",
    "Resources and Tools",
    "Self-analysis",
    "General Conclusions",
    "Audit",
]

# Default keyword map mixes Ukrainian and English markers frequently found in
# Diia.Business publications. The structure is customisable at runtime.
DEFAULT_KEYWORD_MAP: Dict[str, List[str]] = {
    "Introduction": [
        "вступ",
        "коротка суть",
        "мета",
        "навіщо",
        "огляд",
        "introduction",
        "summary",
        "purpose",
    ],
    "Case Description": [
        "опис кейсу",
        "стартові умови",
        "передумови",
        "ринок",
        "інвестиції",
        "команда",
        "case description",
        "background",
        "initial conditions",
    ],
    "Challenges": [
        "труднощі",
        "виклики",
        "бар'єри",
        "obstacles",
        "challenges",
        "pain point",
    ],
    "Solutions Found": [
        "рішення",
        "стратегія",
        "кроки",
        "дії",
        "implementation",
        "solutions",
    ],
    "Effectiveness Analysis": [
        "аналіз ефективності",
        "результати",
        "success",
        "невдачі",
        "lessons",
        "impact",
    ],
    "Resources and Tools": [
        "ресурси",
        "інструменти",
        "технології",
        "платформи",
        "toolkit",
        "partners",
    ],
    "Self-analysis": [
        "самоаналіз",
        "цитати",
        "висновки підприємця",
        "reflection",
        "quote",
        "entrepreneur",
    ],
    "General Conclusions": [
        "узагальнені висновки",
        "висновки",
        "рекомендації",
        "тенденції",
        "conclusion",
        "advice",
        "recommendations",
    ],
    "Audit": [
        "аудит",
        "контрольні питання",
        "сліпі плями",
        "прогалини",
        "audit",
        "risks",
    ],
}

# Regex helpers reused by the parser.
_MULTISPACE = re.compile(r"\s+", re.UNICODE)
_WORD_NORMALISER = re.compile(r"[^\w\s]", re.UNICODE)


@dataclass(frozen=True)
class ParserConfig:
    """Configuration for keyword-driven block detection."""

    block_order: Iterable[str]
    keyword_map: Dict[str, Iterable[str]]
    heading_max_words: int = 12

    def as_json(self) -> str:
        """Serialise the config to JSON (useful for debugging/export)."""

        payload = {
            "block_order": list(self.block_order),
            "keyword_map": {k: list(v) for k, v in self.keyword_map.items()},
            "heading_max_words": self.heading_max_words,
        }
        return json.dumps(payload, ensure_ascii=False, indent=2)


DEFAULT_CONFIG = ParserConfig(block_order=BLOCK_ORDER, keyword_map=DEFAULT_KEYWORD_MAP)


def parse_story(text: str, config: ParserConfig | None = None) -> Dict[str, str]:
    """Split a success story into analytical blocks.

    Args:
        text: Full story body (with optional headings).
        config: Optional override for the parser configuration.

    Returns:
        Ordered dictionary-like mapping (Python ``dict`` preserves insertion
        order) where keys match ``config.block_order`` and values contain block
        content. Missing sections are represented by empty strings.
    """

    cfg = config or DEFAULT_CONFIG
    ordered_blocks = list(cfg.block_order)
    keyword_map = {k: list(v) for k, v in cfg.keyword_map.items()}
    normalised_text = re.sub(r"\r\n?", "\n", text or "").strip()
    if not normalised_text:
        return {block: "" for block in ordered_blocks}

    sections = _extract_sections(normalised_text, cfg)
    result: Dict[str, str] = {block: "" for block in ordered_blocks}
    fallback_index = 0

    for block_hint, content in sections:
        if not content:
            continue

        if block_hint in result:
            target_block = block_hint
            fallback_index = max(fallback_index, ordered_blocks.index(target_block))
        else:
            target_block, fallback_index = _assign_fallback(result, ordered_blocks, fallback_index)

        existing = result[target_block]
        result[target_block] = f"{existing}\n\n{content}".strip() if existing else content

    return result


def _assign_fallback(current: Dict[str, str], order: List[str], start: int) -> Tuple[str, int]:
    """Pick the next empty block in canonical order."""

    for idx in range(start, len(order)):
        block = order[idx]
        if not current[block]:
            return block, idx
    return order[-1], len(order) - 1


def _extract_sections(text: str, config: ParserConfig) -> List[Tuple[Optional[str], str]]:
    """Extract candidate sections with potential block names."""

    paragraphs = [p.strip() for p in re.split(r"\n{2,}", text) if p.strip()]
    sections: List[Tuple[Optional[str], str]] = []
    active_block: Optional[str] = None

    for paragraph in paragraphs:
        lines = [ln.strip() for ln in paragraph.splitlines() if ln.strip()]
        if not lines:
            continue

        block, inline_content = _detect_heading(lines[0], config)
        if block:
            # Start a new block; reuse inline content if heading contains text.
            body_lines = []
            if inline_content:
                body_lines.append(inline_content)
            if len(lines) > 1:
                body_lines.extend(lines[1:])
            sections.append((block, "\n".join(body_lines).strip()))
            active_block = block
        else:
            sections.append((active_block, "\n".join(lines)))

    # Merge consecutive sections that belong to the same block to avoid
    # splitting paragraphs unnecessarily.
    merged: List[Tuple[Optional[str], str]] = []
    for block, content in sections:
        if merged and merged[-1][0] == block and block is not None:
            prev_block, prev_content = merged[-1]
            merged[-1] = (prev_block, f"{prev_content}\n\n{content}".strip())
        else:
            merged.append((block, content))
    return merged


def _detect_heading(line: str, config: ParserConfig) -> Tuple[Optional[str], str]:
    """Attempt to interpret a line as a heading and return the block match."""

    stripped = line.strip()
    if not stripped:
        return None, ""

    for separator in (":", "—", "–", "-", "."):
        if separator in stripped:
            potential, tail = stripped.split(separator, 1)
            block = _evaluate_heading_candidate(potential, config)
            if block:
                return block, tail.strip()

    block = _evaluate_heading_candidate(stripped, config)
    return block, ""


def _evaluate_heading_candidate(candidate: str, config: ParserConfig) -> Optional[str]:
    """Check if candidate string matches any configured keyword."""

    candidate = candidate.strip()
    if not candidate:
        return None

    if not _looks_like_heading(candidate, config.heading_max_words):
        return None

    normalised = _MULTISPACE.sub(" ", _WORD_NORMALISER.sub(" ", candidate.lower())).strip()
    tokens = set(normalised.split())

    for block, keywords in config.keyword_map.items():
        for keyword in keywords:
            keyword = keyword.lower()
            if " " in keyword:
                if keyword in normalised:
                    return block
            elif keyword in tokens:
                return block
    return None


def _looks_like_heading(text: str, max_words: int) -> bool:
    """Heuristic: treat short lines as potential headings."""

    words = text.split()
    return 0 < len(words) <= max_words


__all__ = [
    "BLOCK_ORDER",
    "DEFAULT_KEYWORD_MAP",
    "ParserConfig",
    "DEFAULT_CONFIG",
    "parse_story",
]
