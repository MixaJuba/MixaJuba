import sys
from pathlib import Path

# Ensure project root is on sys.path so `import viz` works even when Streamlit
# changes the current working directory before executing the script.
ROOT = Path(__file__).resolve().parent.parent
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))

import streamlit as st
import pandas as pd
import json
from pathlib import Path
try:
    from viz.plot_utils import plot_block_heatmap, plot_timeline, word_freq_table
except Exception:
    # Streamlit may execute the script in a way that prevents package-style
    # imports. Fall back to loading the module directly from file path.
    import importlib.util

    plot_utils_path = Path(__file__).resolve().parent / "plot_utils.py"
    spec = importlib.util.spec_from_file_location("viz.plot_utils", str(plot_utils_path))
    plot_utils = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(plot_utils)  # type: ignore
    plot_block_heatmap = plot_utils.plot_block_heatmap
    plot_timeline = plot_utils.plot_timeline
    word_freq_table = plot_utils.word_freq_table

st.set_page_config(page_title="Diia.Business Stories Explorer", layout="wide")

st.title("Diia.Business Success Stories — Explorer")

data_dir = st.sidebar.text_input("Exports directory", value="output")
json_path = Path(data_dir) / "stories.json"
csv_path = Path(data_dir) / "stories.csv"

stories = None
if json_path.exists():
    with json_path.open("r", encoding="utf-8") as fh:
        stories = json.load(fh)
elif csv_path.exists():
    stories = pd.read_csv(csv_path).to_dict(orient="records")
else:
    st.warning("No exports found. Run the scraper and set the exports directory.")

if stories:
    df = pd.json_normalize(stories)
    st.sidebar.write(f"Loaded {len(df)} stories")

    st.header("Completeness heatmap")
    fig = plot_block_heatmap(df)
    st.plotly_chart(fig, use_container_width=True)

    st.header("Timeline")
    fig2 = plot_timeline(df)
    st.plotly_chart(fig2, use_container_width=True)

    st.header("Word frequency explorer")
    block = st.selectbox("Select block to inspect", options=[c for c in df.columns if c not in ["title","url","raw_text","metadata","validation"]])
    table = word_freq_table(df, block)
    st.dataframe(table)
