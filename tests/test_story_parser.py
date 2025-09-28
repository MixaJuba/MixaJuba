import unittest

from diia_scraper.story_parser import DEFAULT_CONFIG, ParserConfig, parse_story


SAMPLE_TEXT = """
Вступ: Мета кейсу — перевести офлайн-продажі в онлайн.

Стартові умови
Ринок перенасичений, команда з трьох людей, бюджет 5000 доларів.

Труднощі: Відсутність довіри клієнтів.

Знайдені рішення
Запустили контент-маркетинг та партнерство з локальними медіа.

Аналіз ефективності
Перші два місяці без зростання, але згодом стабільний приріст 15%.

Ресурси та інструменти
CRM, email-розсилки, консультації менторів.

Самоаналіз
"Ми недооцінили силу простих рекомендацій" — засновник.

Узагальнені висновки
Регулярна робота з відгуками — ключ до масштабування.

Аудит
Не розкрито роботу з безпекою даних.
"""


class StoryParserTests(unittest.TestCase):
    def test_parse_story_detects_all_blocks(self) -> None:
        parsed = parse_story(SAMPLE_TEXT)
        for block in DEFAULT_CONFIG.block_order:
            self.assertTrue(parsed[block])

    def test_parse_story_fallback_without_headings(self) -> None:
        text = "Перша частина кейсу.\n\nДругий абзац пояснює старт.\n\nТретій параграф про проблеми."
        parsed = parse_story(text)
        self.assertTrue(parsed["Introduction"].startswith("Перша частина"))
        self.assertTrue(parsed["Case Description"].startswith("Другий"))
        self.assertIn("Третій", parsed["Challenges"])

    def test_custom_keyword_configuration(self) -> None:
        custom_map = {block: list(keywords) for block, keywords in DEFAULT_CONFIG.keyword_map.items()}
        custom_map["Introduction"] = ["intro"]
        config = ParserConfig(block_order=DEFAULT_CONFIG.block_order, keyword_map=custom_map)
        parsed = parse_story("Intro: Custom content", config=config)
        self.assertTrue(parsed["Introduction"].startswith("Custom content"))


if __name__ == "__main__":  # pragma: no cover
    unittest.main()
