"""Scraper orchestrating Diia.Business success story extraction."""

from __future__ import annotations

import json
import logging
import time
from dataclasses import dataclass, field, asdict
from pathlib import Path
from typing import Dict, Iterable, List, Optional
from urllib.parse import urljoin
import urllib.robotparser

import pandas as pd
import requests
from bs4 import BeautifulSoup

from .story_parser import ParserConfig, DEFAULT_CONFIG, parse_story
from .validation import ValidationResult, assess_story_blocks

LOGGER = logging.getLogger(__name__)


@dataclass
class StructuredStory:
    """Normalised representation of a success story."""

    title: str
    url: str
    blocks: Dict[str, str]
    validation: ValidationResult
    raw_text: str = ""
    metadata: Dict[str, str] = field(default_factory=dict)

    def to_serialisable_dict(self) -> Dict[str, object]:
        """Return a JSON-serialisable representation of the story."""

        payload = asdict(self)
        payload["validation"] = {
            "missing_blocks": self.validation.missing_blocks,
            "needs_review": self.validation.needs_review,
            "notes": self.validation.notes,
        }
        return payload

    def to_flat_record(self) -> Dict[str, str]:
        """Flatten nested structure for CSV export."""

        record = {
            "title": self.title,
            "url": self.url,
            "needs_review": str(self.validation.needs_review),
            "missing_blocks": ", ".join(self.validation.missing_blocks),
        }
        record.update({block: text for block, text in self.blocks.items()})
        return record


class DiiaBusinessScraper:
    """Scrape and structure success stories from business.diia.gov.ua."""

    def __init__(
        self,
        base_url: str = "https://business.diia.gov.ua",
        listing_path: str = "/history-of-success",
        *,
        session: Optional[requests.Session] = None,
        parser_config: ParserConfig = DEFAULT_CONFIG,
        request_delay: float = 1.0,
        request_timeout: int = 30,
    ) -> None:
        self.base_url = base_url.rstrip("/")
        self.listing_path = listing_path
        self.session = session or requests.Session()
        self.session.headers.setdefault(
            "User-Agent",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 "
            "(KHTML, like Gecko) Chrome/122.0 Safari/537.36",
        )
        self.parser_config = parser_config
        self.request_delay = request_delay
        self.request_timeout = request_timeout

    # ------------------------------------------------------------------
    # Public API
    # ------------------------------------------------------------------
    def scrape(self, limit: int = 3) -> List[StructuredStory]:
        """Scrape and structure a limited number of success stories."""

        links = self._fetch_story_links(limit)
        stories: List[StructuredStory] = []

        for index, url in enumerate(links, start=1):
            try:
                LOGGER.info("Fetching story %s/%s: %s", index, len(links), url)
                html = self._get(url)
                soup = BeautifulSoup(html, "html.parser")
                title = self._extract_title(soup) or "Untitled story"
                raw_text = self._extract_story_text(soup)
                blocks = parse_story(raw_text, self.parser_config)
                validation = assess_story_blocks(blocks, self.parser_config.block_order)
                stories.append(
                    StructuredStory(
                        title=title,
                        url=url,
                        blocks=blocks,
                        validation=validation,
                        raw_text=raw_text,
                        metadata={"fetched_at": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime())},
                    )
                )
            except requests.RequestException as exc:
                LOGGER.error("Failed to download %s: %s", url, exc)
            except Exception as exc:  # pragma: no cover - defensive logging
                LOGGER.exception("Unexpected error while processing %s: %s", url, exc)

            time.sleep(self.request_delay)

        return stories

    def check_robots_allowance(self) -> bool:
        """Return True if scraping the listing path is allowed by robots.txt."""

        robots_url = urljoin(f"{self.base_url}/", "robots.txt")
        parser = urllib.robotparser.RobotFileParser()
        try:
            parser.set_url(robots_url)
            parser.read()
        except Exception as exc:  # pragma: no cover - network failure resilience
            LOGGER.warning("Could not read robots.txt (%s): %s", robots_url, exc)
            return False
        return parser.can_fetch(self.session.headers.get("User-Agent", "*"), urljoin(self.base_url, self.listing_path))

    # ------------------------------------------------------------------
    # Export helpers
    # ------------------------------------------------------------------
    def export(self, stories: Iterable[StructuredStory], output_dir: Path) -> Dict[str, Path]:
        """Export structured stories to JSON and CSV."""

        output_dir.mkdir(parents=True, exist_ok=True)
        stories_list = list(stories)
        json_path = output_dir / "stories.json"
        csv_path = output_dir / "stories.csv"
        export_to_json(stories_list, json_path)
        export_to_csv(stories_list, csv_path)
        return {"json": json_path, "csv": csv_path}

    # ------------------------------------------------------------------
    # Internal helpers
    # ------------------------------------------------------------------
    def _fetch_story_links(self, limit: int) -> List[str]:
        """Return absolute URLs for the latest success stories."""

        listing_url = urljoin(self.base_url, self.listing_path)
        html = self._get(listing_url)
        soup = BeautifulSoup(html, "html.parser")
        links = self._extract_links_from_listing(soup)
        absolute_links = [urljoin(self.base_url, link) for link in links]
        return absolute_links[:limit]

    def _extract_links_from_listing(self, soup: BeautifulSoup) -> List[str]:
        """Extract story hyperlinks from the listing page."""

        candidates = []
        for selector in [
            "a.article-card",
            "a.card",
            "a.story-card",
            "article a",
        ]:
            for anchor in soup.select(selector):
                href = anchor.get("href")
                if href and href not in candidates:
                    candidates.append(href)
            if candidates:
                break

        # Fallback: look for anchors containing the listing path slug.
        if not candidates:
            for anchor in soup.find_all("a"):
                href = anchor.get("href") or ""
                if "history" in href and href not in candidates:
                    candidates.append(href)
        return candidates

    def _extract_title(self, soup: BeautifulSoup) -> Optional[str]:
        """Extract the title (h1) from a story page."""

        title_tag = soup.find("h1")
        if title_tag:
            return title_tag.get_text(strip=True)
        return None

    def _extract_story_text(self, soup: BeautifulSoup) -> str:
        """Extract the main textual content from a story page."""

        containers = [
            "article",
            "div.article",
            "div.post-content",
            "div.article-content",
            "div.blog-single",
            "main",
        ]
        for selector in containers:
            element = soup.select_one(selector)
            if element:
                paragraphs = [
                    p.get_text(" ", strip=True)
                    for p in element.find_all(["p", "li", "blockquote", "h2", "h3"])
                ]
                text = "\n\n".join(filter(None, paragraphs))
                if text.strip():
                    return text
        # Ultimate fallback – entire page text.
        return soup.get_text(" ", strip=True)

    def _get(self, url: str) -> str:
        """Perform an HTTP GET with error handling."""

        response = self.session.get(url, timeout=self.request_timeout)
        response.raise_for_status()
        return response.text


# ----------------------------------------------------------------------
# Standalone exporters
# ----------------------------------------------------------------------

def export_to_json(stories: Iterable[StructuredStory], path: Path) -> Path:
    """Export structured stories to a JSON file."""

    serialised = [story.to_serialisable_dict() for story in stories]
    with path.open("w", encoding="utf-8") as fh:
        json.dump(serialised, fh, ensure_ascii=False, indent=2)
    return path


def export_to_csv(stories: Iterable[StructuredStory], path: Path) -> Path:
    """Export structured stories to CSV (wide format)."""

    rows = [story.to_flat_record() for story in stories]
    df = pd.DataFrame(rows)
    df.to_csv(path, index=False)
    return path


__all__ = ["DiiaBusinessScraper", "StructuredStory", "export_to_json", "export_to_csv"]
