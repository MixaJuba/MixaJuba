import unittest

from diia_scraper.validation import ValidationResult, assess_story_blocks


class ValidationTests(unittest.TestCase):
    def test_assess_story_blocks_marks_missing_sections(self) -> None:
        blocks = {"Introduction": "Text", "Case Description": "", "Challenges": ""}
        result = assess_story_blocks(blocks, ["Introduction", "Case Description", "Challenges"])
        self.assertTrue(result.needs_review)
        self.assertEqual(result.missing_blocks, ["Case Description", "Challenges"])
        self.assertIn("Case Description", result.notes)
        self.assertIn("Challenges", result.notes)

    def test_validation_result_repr(self) -> None:
        result = ValidationResult(missing_blocks=["A"], needs_review=True, notes={"A": "Missing"})
        self.assertIn("A", repr(result))


if __name__ == "__main__":  # pragma: no cover
    unittest.main()
