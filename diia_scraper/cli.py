"""Command-line interface for Diia.Business story scraping."""

from __future__ import annotations

import argparse
import json
import logging
from pathlib import Path
from typing import Dict

from .scraper import DiiaBusinessScraper
from .story_parser import DEFAULT_CONFIG, ParserConfig


def _load_keyword_map(path: Path) -> Dict[str, list[str]]:
    with path.open("r", encoding="utf-8") as fh:
        data = json.load(fh)
        if not isinstance(data, dict):
            raise ValueError("Keyword override must be a JSON object")
        return {str(k): list(v) for k, v in data.items()}


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description="Scrape Diia.Business success stories")
    parser.add_argument("--limit", type=int, default=3, help="Number of stories to download (default: 3)")
    parser.add_argument(
        "--output-dir",
        type=Path,
        default=Path("output"),
        help="Directory for JSON/CSV exports",
    )
    parser.add_argument(
        "--keywords",
        type=Path,
        help="Path to JSON file with custom keyword map (block -> list of keywords)",
    )
    parser.add_argument(
        "--delay",
        type=float,
        default=1.0,
        help="Delay between HTTP requests in seconds",
    )
    parser.add_argument(
        "--timeout",
        type=int,
        default=30,
        help="HTTP request timeout in seconds",
    )
    parser.add_argument(
        "--base-url",
        type=str,
        default="https://business.diia.gov.ua",
        help="Override base URL (useful for mirrors or testing)",
    )
    parser.add_argument(
        "--listing-path",
        type=str,
        default="/history-of-success",
        help="Override listing path",
    )
    parser.add_argument("--verbose", action="store_true", help="Enable debug logging")
    return parser


def main(args: list[str] | None = None) -> None:
    parser = build_parser()
    options = parser.parse_args(args=args)

    logging.basicConfig(level=logging.DEBUG if options.verbose else logging.INFO)

    parser_config = DEFAULT_CONFIG
    if options.keywords:
        keyword_map = _load_keyword_map(options.keywords)
        parser_config = ParserConfig(block_order=DEFAULT_CONFIG.block_order, keyword_map=keyword_map)

    scraper = DiiaBusinessScraper(
        base_url=options.base_url,
        listing_path=options.listing_path,
        parser_config=parser_config,
        request_delay=options.delay,
        request_timeout=options.timeout,
    )

    if not scraper.check_robots_allowance():
        logging.warning("Scraping may be disallowed by robots.txt – review before proceeding.")

    stories = scraper.scrape(limit=options.limit)
    if not stories:
        logging.error("No stories were scraped. Check network connectivity or URL settings.")
        return

    outputs = scraper.export(stories, options.output_dir)
    logging.info("Exported %s stories to JSON: %s and CSV: %s", len(stories), outputs["json"], outputs["csv"])


if __name__ == "__main__":  # pragma: no cover - CLI entry point
    main()
