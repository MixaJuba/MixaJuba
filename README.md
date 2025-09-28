# Diia.Business Success Story Scraper

[![CI](https://github.com/MixaJuba/MixaJuba/actions/workflows/ci.yml/badge.svg)](https://github.com/MixaJuba/MixaJuba/actions/workflows/ci.yml)


This repository now contains a production-ready Python utility that collects
Ukrainian business success stories from
[Diia.Business](https://business.diia.gov.ua/history-of-success), normalises the
content into analytical blocks, and exports results to both JSON and CSV for
further research.

## Key features
- **Ethical scraping workflow** – configurable delay, robots.txt check, custom
  user-agent, and graceful error handling.
- **Heuristic structuring engine** – stories are automatically segmented into
  nine analytical blocks (introduction, case description, challenges, etc.)
  using multilingual keyword detection with positional fallbacks.
- **Quality controls** – empty blocks are flagged for manual audit to prevent
  silent data loss.
- **Flexible exports** – helper utilities produce machine-readable JSON and
  analyst-friendly wide CSV tables.
- **Unit-tested codebase** – `python -m unittest` runs deterministic tests with
  fake HTTP sessions; no live requests are issued during CI.

## Project layout

```
├── diia_scraper/
│   ├── cli.py              # CLI entry point (argparse-based)
│   ├── scraper.py          # Network orchestration & export helpers
│   ├── story_parser.py     # Heuristic text segmentation logic
│   └── validation.py       # Block-level quality checks
├── tests/                  # Unit tests (no external dependencies)
├── requirements.txt        # Runtime dependencies
└── README.md
```

## Installation

```bash
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

> **Tip:** Pin dependencies in your production environment to avoid changes in
> HTML parsing behaviour when upstream libraries release updates.

## Usage

```bash
python -m diia_scraper.cli --limit 3 --output-dir output --delay 1.5 --verbose
```

The command above downloads the first three success stories (subject to
network/robots.txt restrictions), structures them, and writes two files:

- `output/stories.json` – nested JSON with metadata, block texts, and validation
  notes.
- `output/stories.csv` – flat table convenient for spreadsheets or BI tools.

### Custom keyword dictionaries

Create a JSON file where keys are block names and values are keyword lists, e.g.:

```json
{
  "Introduction": ["intro", "початок"],
  "Audit": ["провали", "gap"]
}
```

Then run:

```bash
python -m diia_scraper.cli --keywords keywords.json
```

Missing blocks will still be assigned via fallback ordering, but custom keywords
allow you to tailor detection for niche industries or internal terminology.

## Scheduling & extensions

- **Cron** – wrap the CLI in a shell script and add an entry to `crontab -e`
  (e.g. `0 6 * * 1` for a weekly run). Persist output timestamps if you need
  change tracking.
- **Apache Airflow** – load `DiiaBusinessScraper` inside a PythonOperator task to
  integrate with existing ETL pipelines. Use XCom to push JSON payloads.
-- **Google Sheets / Notion** – export the CSV to Google Drive via
  [`gspread`](https://github.com/burnash/gspread) or sync to Notion using the
  official [Notion API](https://developers.notion.com/docs/getting-started).

### Example: Google Sheets integration with gspread

```python
import gspread
import pandas as pd

# Authenticate (see gspread docs for OAuth setup)
gc = gspread.service_account(filename='credentials.json')
sh = gc.create('Diia Stories')

# Load CSV and upload
df = pd.read_csv('output/stories.csv')
worksheet = sh.get_worksheet(0)
worksheet.update([df.columns.values.tolist()] + df.values.tolist())
```

## Best practices

1. **Respect robots.txt** – run the built-in `check_robots_allowance()` before
   scraping or configure mirrors for private datasets.
2. **Rate limiting** – adjust `--delay` if the portal enforces stricter limits.
3. **Network resilience** – wrap production runs with retry logic (e.g.
   [`tenacity`](https://github.com/jd/tenacity)) and persistent caching (e.g.
   [`requests-cache`](https://requests-cache.readthedocs.io/en/stable/)).
4. **Content drift** – HTML structure may change; extend
   `_extract_story_text()` selectors and add regression tests with fixture HTML.
5. **NLP upgrades** – swap the heuristic parser for
   [spaCy](https://spacy.io/usage) text classification або zero-shot/ML моделі з [Hugging Face Transformers](https://huggingface.co/docs/transformers/index) для підвищення якості сегментації блоків.

## Semantic parsing (spaCy / HuggingFace)

Для покращення розбиття на блоки використовуйте:

- [spaCy](https://spacy.io/usage) для rule-based або ML-класифікації:
  ```python
  import spacy
  nlp = spacy.load('uk_core_news_sm')
  doc = nlp(story_text)
  # Використовуйте doc.cats або кастомний pipeline для визначення блоків
  ```
- [Hugging Face Transformers](https://huggingface.co/docs/transformers/index) для zero-shot або кастомних моделей:
  ```python
  from transformers import pipeline
  classifier = pipeline('zero-shot-classification', model='joeddav/xlm-roberta-large-xnli')
  result = classifier(story_text, candidate_labels=["Introduction", "Audit", ...])
  ```

Замініть або доповніть поточний евристичний парсер у `story_parser.py` для більшої гнучкості та точності.

## Testing

```bash
python -m unittest
```

Tests rely on fake HTML responses, so they are fast and repeatable. For
additional validation, you can add doctests or golden-file comparisons for real
stories once network access is available.

## Troubleshooting

- **403/Proxy errors** – the official domain may block certain regions. Deploy
  the scraper within Ukrainian infrastructure or configure approved corporate
  proxies.
- **Empty exports** – ensure the listing URL has stories (pagination can be
  handled by calling `scrape()` again with an offset or extending
  `_fetch_story_links`).

## Visualization (Streamlit)

An interactive explorer is included under `viz/`. It reads `output/stories.json` or `output/stories.csv` and shows:

- block completeness heatmap
- timeline of fetches
- word frequency explorer per block

Run locally:

```bash
pip install -r requirements.txt
streamlit run viz/app.py
```

Run via Docker (recommended for stable deployments):

```bash
docker build -t diia-viz -f viz/Dockerfile .
docker run -p 8501:8501 diia-viz
```
- **Encoding issues** – all exports use UTF-8; if your downstream system expects
  Windows-1251, convert via `iconv` or `pandas.DataFrame.to_csv(encoding="cp1251")`.

## Contribution ideas

- Add pagination awareness by crawling the `aria-label="next"` button.
- Integrate a caching layer to avoid re-downloading unchanged stories.
- Store raw HTML snapshots for reproducibility and auditing.
- Provide optional summarisation via open-source LLMs (e.g. `ukr-large` models)
  with GPU-friendly inference stacks such as
  [AutoGPTQ](https://github.com/PanQiWei/AutoGPTQ) for quantised deployment.

## License

This project builds upon public information available on the Diia.Business
portal. Review local regulations and the website's terms of use before
redistributing scraped content.
