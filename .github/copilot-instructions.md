# Copilot Instructions for Diia.Business Success Story Scraper

## Project Overview
This Python utility scrapes Ukrainian business success stories from Diia.Business, segments them into analytical blocks, and exports results to JSON and CSV. The codebase is modular, with clear separation between CLI, scraping, parsing, and validation logic.

## Architecture & Key Components
- `diia_scraper/cli.py`: CLI entry point using `argparse`. Handles user arguments and orchestrates the workflow.
- `diia_scraper/scraper.py`: Manages network requests, robots.txt checks, and export helpers. Handles story fetching and output file generation.
- `diia_scraper/story_parser.py`: Implements heuristic segmentation of stories into analytical blocks using multilingual keyword detection and positional fallbacks.
- `diia_scraper/validation.py`: Performs block-level quality checks, flags empty blocks for manual review.
- `tests/`: Contains unit tests using fake HTTP responses. No live network calls during tests.

## Developer Workflows
- **Install dependencies:**
  ```bash
  python -m venv .venv
  source .venv/bin/activate
  pip install -r requirements.txt
  ```
- **Run scraper:**
  ```bash
  python -m diia_scraper.cli --limit 3 --output-dir output --delay 1.5 --verbose
  ```
- **Run tests:**
  ```bash
  python -m unittest
  ```
- **Custom keyword dictionaries:**
  Pass a JSON file with block names and keywords via `--keywords keywords.json` to tailor segmentation.

## Project-Specific Patterns
- **Ethical scraping:** Always check robots.txt and use rate limiting (`--delay`).
- **Heuristic parsing:** Analytical blocks are detected via keywords and fallback ordering. Extend keyword lists for new industries.
- **Quality control:** Empty blocks are flagged; validation logic is in `validation.py`.
- **Flexible exports:** Outputs are written as both nested JSON and wide CSV tables.
- **Testing:** All tests use mocked HTTP responses for speed and reliability.

## Integration Points
- **Cron/Airflow:** CLI can be scheduled or integrated into ETL pipelines.
- **Google Sheets/Notion:** Exported CSV can be synced using external tools/APIs.

## Troubleshooting & Conventions
- **Network errors:** Scraper may be blocked outside Ukraine; use proxies if needed.
- **Content drift:** HTML selectors may need updates if site structure changes.
- **Encoding:** All exports are UTF-8; convert as needed for downstream systems.

## Example Usage
```bash
python -m diia_scraper.cli --limit 5 --output-dir results --keywords custom_keywords.json
```

## References
- See `README.md` for detailed instructions, best practices, and contribution ideas.
- Key files: `diia_scraper/cli.py`, `diia_scraper/scraper.py`, `diia_scraper/story_parser.py`, `diia_scraper/validation.py`, `tests/`

---
If any section is unclear or missing, please provide feedback to improve these instructions.
