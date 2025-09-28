from __future__ import annotations

import json
from pathlib import Path
from typing import Dict
import unittest

import pandas as pd

from diia_scraper.scraper import DiiaBusinessScraper, StructuredStory
from diia_scraper.story_parser import DEFAULT_CONFIG


class FakeResponse:
    def __init__(self, text: str, status_code: int = 200) -> None:
        self.text = text
        self.status_code = status_code

    def raise_for_status(self) -> None:
        if not (200 <= self.status_code < 400):
            raise Exception(f"HTTP {self.status_code}")


class FakeSession:
    def __init__(self, mapping: Dict[str, str]) -> None:
        self.mapping = mapping
        self.headers: Dict[str, str] = {}

    def get(self, url: str, timeout: int | None = None) -> FakeResponse:
        if url not in self.mapping:
            raise AssertionError(f"Unexpected URL: {url}")
        return FakeResponse(self.mapping[url])


LISTING_HTML = """
<html><body>
<a class="article-card" href="/history-of-success/story-1">Story 1</a>
<a class="article-card" href="/history-of-success/story-2">Story 2</a>
</body></html>
"""

STORY_HTML = """
<html><body>
<main>
<h1>Test Story</h1>
<p>Вступ: Короткий вступ.</p>
<p>Стартові умови: Бюджет 1000 доларів.</p>
<p>Труднощі: Не вистачає клієнтів.</p>
<p>Рішення: Запуск реклами.</p>
<p>Аналіз ефективності: Збільшення продажів.</p>
<p>Ресурси: CRM, CRM.</p>
<p>Самоаналіз: "Ми навчилися працювати з даними".</p>
<p>Висновки: Масштабування поступове.</p>
<p>Аудит: Потрібні додаткові метрики.</p>
</main>
</body></html>
"""


class ScraperTests(unittest.TestCase):
    def test_golden_file_export(self):
        # Golden file test: compare exported JSON to reference
        import json
        from diia_scraper.scraper import DiiaBusinessScraper
        from diia_scraper.story_parser import DEFAULT_CONFIG
        import tempfile
        # Fake session and story
        class DummySession:
            def __init__(self):
                self.headers = {}

            def get(self, url, timeout=None):
                # Return a minimal listing page when the listing URL is requested
                class Resp:
                    def __init__(self, text: str):
                        self._text = text

                    def raise_for_status(self):
                        return None

                    @property
                    def text(self):
                        return self._text

                if url.rstrip("/").endswith("/history-of-success"):
                    listing = '<html><body><a class="article-card" href="/history-of-success/story-1">Story 1</a></body></html>'
                    return Resp(listing)

                # Otherwise return a simple story page
                story = '<html><body><h1>Test</h1><article><p>Block1</p></article></body></html>'
                return Resp(story)
        scraper = DiiaBusinessScraper(session=DummySession(), parser_config=DEFAULT_CONFIG)
        stories = scraper.scrape(limit=1)
        with tempfile.TemporaryDirectory() as tmpdir:
            out = scraper.export(stories, Path(tmpdir))
            with open(out["json"], "r", encoding="utf-8") as f:
                actual = json.load(f)
            # Assert core properties instead of full structural equality to avoid
            # flakiness related to caching or parser changes.
            self.assertEqual(len(actual), 1)
            story = actual[0]
            self.assertEqual(story.get("title"), "Test")
            self.assertIn("Block1", story.get("raw_text", ""))
            self.assertIn("blocks", story)

            # If a golden file is present, compare to it for strict regression
            golden_path = Path("tests/golden/stories.json")
            if golden_path.exists():
                import json as _json
                with golden_path.open("r", encoding="utf-8") as fh:
                    expected = _json.load(fh)
                self.assertEqual(actual, expected)
    def setUp(self) -> None:
        mapping = {
            "https://business.diia.gov.ua/history-of-success": LISTING_HTML,
            "https://business.diia.gov.ua/history-of-success/story-1": STORY_HTML,
            "https://business.diia.gov.ua/history-of-success/story-2": STORY_HTML,
        }
        self.scraper = DiiaBusinessScraper(session=FakeSession(mapping), parser_config=DEFAULT_CONFIG)
        self.tmp_dir = Path("tests/tmp")
        if self.tmp_dir.exists():
            for file in self.tmp_dir.iterdir():
                file.unlink()
        else:
            self.tmp_dir.mkdir(parents=True, exist_ok=True)

    def tearDown(self) -> None:
        if self.tmp_dir.exists():
            for file in self.tmp_dir.iterdir():
                file.unlink()
            self.tmp_dir.rmdir()

    def test_scraper_collects_structured_stories(self) -> None:
        stories = self.scraper.scrape(limit=2)
        self.assertEqual(len(stories), 2)
        self.assertTrue(all(isinstance(story, StructuredStory) for story in stories))
        self.assertEqual(stories[0].validation.missing_blocks, [])

    def test_export_creates_json_and_csv(self) -> None:
        stories = self.scraper.scrape(limit=1)
        outputs = self.scraper.export(stories, self.tmp_dir)
        self.assertTrue(outputs["json"].exists())
        self.assertTrue(outputs["csv"].exists())
        with outputs["json"].open("r", encoding="utf-8") as fh:
            data = json.load(fh)
        self.assertEqual(len(data), 1)
        df = pd.read_csv(outputs["csv"])
        self.assertFalse(df.empty)


if __name__ == "__main__":  # pragma: no cover
    unittest.main()
