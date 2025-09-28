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
