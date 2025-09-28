import pandas as pd
import plotly.express as px
from collections import Counter
import re

_WORD_RE = re.compile(r"\w+", flags=re.UNICODE)


def plot_block_heatmap(df: pd.DataFrame):
    # Determine block columns (heuristic)
    block_cols = [c for c in df.columns if c not in ["title", "url", "raw_text", "metadata", "validation"]]
    # Ensure we treat non-empty strings as presence; use applymap to avoid
    # Series.str on a DataFrame which raises AttributeError.
    as_str = df[block_cols].astype(str)
    # Use apply on columns and Series.str operations to avoid deprecated
    # DataFrame.applymap and keep behaviour stable across pandas versions.
    non_empty = as_str.apply(lambda col: col.str.strip().ne("") if hasattr(col, "str") else col.map(lambda v: bool(str(v).strip())))
    presence = df[block_cols].notna() & non_empty
    matrix = presence.astype(int)
    matrix.index = df["title"] if "title" in df.columns else matrix.index
    fig = px.imshow(matrix.T, labels=dict(x="Story", y="Block", color="Present"), aspect="auto")
    return fig


def plot_timeline(df: pd.DataFrame):
    if "metadata.fetched_at" in df.columns:
        df["fetched_at"] = pd.to_datetime(df["metadata.fetched_at"])
    elif "metadata" in df.columns:
        df["fetched_at"] = pd.to_datetime(df["metadata"].apply(lambda m: m.get("fetched_at") if isinstance(m, dict) else None))
    else:
        df["fetched_at"] = pd.NaT
    df["count"] = 1
    fig = px.line(df.groupby("fetched_at")["count"].sum().reset_index(), x="fetched_at", y="count", markers=True)
    return fig


def word_freq_table(df: pd.DataFrame, block: str, top_n: int = 50):
    texts = df.get(block, pd.Series(dtype=str)).dropna().astype(str)
    words = Counter()
    for t in texts:
        words.update(w.lower() for w in _WORD_RE.findall(t))
    rows = [(w, c) for w, c in words.most_common(top_n)]
    return pd.DataFrame(rows, columns=["word", "count"])