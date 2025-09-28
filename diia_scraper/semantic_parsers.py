"""Optional semantic parsers (spaCy / HuggingFace) used as plugin backends.

These functions are intentionally lightweight wrappers so CI doesn't require
heavy models unless explicitly enabled in the environment.
"""
from __future__ import annotations

from typing import Dict
from .story_parser import ParserConfig, parse_story


def spacy_parse(text: str, config: ParserConfig) -> Dict[str, str]:
    try:
        import spacy
    except Exception:
        raise
    # This is a minimal example: split into sentences and assign heuristically
    nlp = spacy.load("uk_core_news_sm") if "uk" in spacy.util.get_installed_models() else spacy.load("en_core_web_sm")
    doc = nlp(text)
    sents = [sent.text.strip() for sent in doc.sents]
    # Very naive: join sentences and fall back to heuristic parser
    return parse_story("\n\n".join(sents), config)


def zero_shot_parse(text: str, config: ParserConfig) -> Dict[str, str]:
    try:
        from transformers import pipeline
    except Exception:
        raise
    classifier = pipeline("zero-shot-classification", model="joeddav/xlm-roberta-large-xnli")
    labels = list(config.block_order)
    # Split into paragraphs and classify each
    paragraphs = [p.strip() for p in text.split("\n\n") if p.strip()]
    result = {k: "" for k in labels}
    for p in paragraphs:
        cls = classifier(p, candidate_labels=labels)
        label = cls["labels"][0]
        result[label] = (result[label] + "\n\n" + p).strip() if result[label] else p
    return result
