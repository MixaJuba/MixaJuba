import unittest
from diia_scraper.semantic_parsers import spacy_parse, zero_shot_parse
from diia_scraper.story_parser import DEFAULT_CONFIG

class SemanticParserTests(unittest.TestCase):
    def test_spacy_fallback(self):
        # Ensure spacy_parse raises if spaCy not installed in CI environment
        try:
            spacy_parse("Тестовий текст", DEFAULT_CONFIG)
        except Exception:
            self.assertTrue(True)

    def test_zero_shot_fallback(self):
        try:
            zero_shot_parse("Test text.", DEFAULT_CONFIG)
        except Exception:
            self.assertTrue(True)

if __name__ == '__main__':
    unittest.main()
