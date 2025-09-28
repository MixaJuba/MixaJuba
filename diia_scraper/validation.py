"""Validation utilities for structured success stories."""

from __future__ import annotations

from dataclasses import dataclass
from typing import Dict, Iterable, List


@dataclass(frozen=True)
class ValidationResult:
    """Captures validation metadata for a structured story."""

    missing_blocks: List[str]
    needs_review: bool
    notes: Dict[str, str]


def assess_story_blocks(blocks: Dict[str, str], required_blocks: Iterable[str]) -> ValidationResult:
    """Check whether the story contains data in all required blocks.

    Args:
        blocks: Mapping of analytic blocks to extracted content.
        required_blocks: Iterable of blocks considered mandatory.

    Returns:
        ``ValidationResult`` listing missing blocks and review notes.
    """

    missing: List[str] = []
    notes: Dict[str, str] = {}

    for block in required_blocks:
        value = blocks.get(block, "")
        if not value or not value.strip():
            missing.append(block)
            notes[block] = "Empty block – requires manual fact-checking."

    return ValidationResult(missing_blocks=missing, needs_review=bool(missing), notes=notes)


__all__ = ["ValidationResult", "assess_story_blocks"]
