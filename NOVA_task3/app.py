from __future__ import annotations

import html
import io
import time
import zipfile
from datetime import datetime
from typing import Any

import streamlit as st
import torch
from PIL import Image, ImageOps, UnidentifiedImageError
from transformers import BlipForConditionalGeneration, BlipProcessor


# -----------------------------------------------------------------------------
# NOVA AI - Image Caption Generator
# Model: Salesforce/blip-image-captioning-large
# -----------------------------------------------------------------------------

APP_NAME = "NOVA AI"
MODEL_ID = "Salesforce/blip-image-captioning-large"
MAX_FILES = 5
MAX_FILE_SIZE_MB = 12
MAX_NEW_TOKENS = 64

st.set_page_config(
    page_title="NOVA AI | Image Caption Studio",
    page_icon="✨",
    layout="wide",
    initial_sidebar_state="expanded",
)


# ---- Theme -------------------------------------------------------------------
st.markdown(
    """
    <style>
    @import url('https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Space+Grotesk:wght@500;600;700&display=swap');

    :root {
        --nova-bg: #090a12;
        --nova-panel: #121421;
        --nova-line: rgba(167, 139, 250, .20);
        --nova-purple: #a78bfa;
        --nova-cyan: #67e8f9;
        --nova-text: #f4f2ff;
        --nova-muted: #a4a2b8;
    }
    .stApp {
        background:
            radial-gradient(ellipse at 12% 0%, rgba(124, 58, 237, .17), transparent 34%),
            radial-gradient(ellipse at 90% 10%, rgba(34, 211, 238, .09), transparent 30%),
            var(--nova-bg);
        color: var(--nova-text);
        font-family: 'DM Sans', sans-serif;
    }
    [data-testid="stHeader"] { background: rgba(9,10,18,.72); }
    .block-container { max-width: 1280px; padding-top: 2rem; padding-bottom: 3rem; }
    h1, h2, h3, h4 { font-family: 'Space Grotesk', sans-serif !important; }
    .nova-brand { font-family: 'Space Grotesk',sans-serif; font-size: 13px; font-weight: 700;
        letter-spacing: .24em; color: #c4b5fd; text-transform: uppercase; }
    .nova-hero { padding: 26px 0 24px; }
    .nova-title { font-family: 'Space Grotesk',sans-serif; font-size: clamp(2.5rem, 7vw, 4.6rem);
        line-height: 1.02; letter-spacing: -.055em; font-weight: 700; margin: 10px 0 12px;
        background: linear-gradient(100deg,#fff 8%,#c4b5fd 52%,#67e8f9 100%);
        -webkit-background-clip: text; background-clip: text; color: transparent; }
    .nova-subtitle { color: var(--nova-muted); font-size: 1.04rem; max-width: 690px; line-height: 1.7; }
    .nova-chip { display:inline-block; border:1px solid var(--nova-line); color:#d8ccff;
        background:rgba(167,139,250,.08); border-radius:99px; padding:6px 11px;
        font-size: .75rem; letter-spacing:.04em; margin-right:6px; }
    .nova-panel { background:linear-gradient(145deg,rgba(25,27,45,.94),rgba(16,18,30,.94));
        border:1px solid var(--nova-line); border-radius:20px; padding:20px; }
    .nova-caption { background:linear-gradient(135deg,rgba(124,58,237,.18),rgba(8,145,178,.10));
        border:1px solid rgba(167,139,250,.34); border-radius:16px; padding:20px 22px;
        color:#f7f5ff; font-size:1.08rem; line-height:1.75; }
    .nova-muted { color:var(--nova-muted); font-size:.9rem; }
    .nova-stat { background:rgba(255,255,255,.035); border:1px solid rgba(255,255,255,.08);
        border-radius:15px; padding:13px 15px; }
    .nova-stat-label { color:var(--nova-muted); font-size:.75rem; text-transform:uppercase; letter-spacing:.1em; }
    .nova-stat-value { color:#fff; font-family:'Space Grotesk',sans-serif; font-size:1.2rem; font-weight:600; margin-top:4px; }
    div[data-testid="stFileUploader"] { background:rgba(255,255,255,.025); border:1px dashed rgba(167,139,250,.35); border-radius:15px; padding:10px; }
    div.stButton > button, div.stDownloadButton > button {
        border:0; border-radius:12px; min-height:2.8rem; font-weight:700;
        background:linear-gradient(100deg,#7c3aed,#6d5dfc 55%,#0891b2); color:white;
        transition:filter .18s ease, transform .18s ease;
    }
    div.stButton > button:hover, div.stDownloadButton > button:hover { filter:brightness(1.12); transform:translateY(-1px); color:white; }
    div.stButton > button:focus, div.stDownloadButton > button:focus { color:white; border:1px solid #c4b5fd; }
    [data-testid="stSidebar"] { background:#0d0f19; border-right:1px solid rgba(167,139,250,.12); }
    [data-testid="stExpander"] { border:1px solid rgba(167,139,250,.17); border-radius:13px; }
    hr { border-color:rgba(167,139,250,.16); }
    #MainMenu, footer { visibility:hidden; }
    </style>
    """,
    unsafe_allow_html=True,
)


# ---- Model loading -----------------------------------------------------------
@st.cache_resource(show_spinner=False)
def load_caption_model() -> tuple[BlipProcessor, BlipForConditionalGeneration, str]:
    """Load BLIP Large once and reuse it for the whole Streamlit session."""
    processor = BlipProcessor.from_pretrained(MODEL_ID)
    device = "cuda" if torch.cuda.is_available() else "cpu"
    model_kwargs: dict[str, Any] = {}
    if device == "cuda":
        model_kwargs["torch_dtype"] = torch.float16
    model = BlipForConditionalGeneration.from_pretrained(MODEL_ID, **model_kwargs)
    model.to(device)
    model.eval()
    return processor, model, device


def prepare_image(uploaded_file: Any) -> Image.Image:
    raw = uploaded_file.getvalue()
    if len(raw) > MAX_FILE_SIZE_MB * 1024 * 1024:
        raise ValueError(f"{uploaded_file.name} is larger than {MAX_FILE_SIZE_MB} MB.")
    try:
        image = Image.open(io.BytesIO(raw))
        image = ImageOps.exif_transpose(image).convert("RGB")
        image.thumbnail((1800, 1800), Image.Resampling.LANCZOS)
        return image
    except (UnidentifiedImageError, OSError, ValueError) as exc:
        raise ValueError(f"Could not read {uploaded_file.name} as a valid image.") from exc


def create_caption(
    image: Image.Image,
    processor: BlipProcessor,
    model: BlipForConditionalGeneration,
    device: str,
    style: str,
) -> str:
    """Generate a caption with deterministic beam search for consistent results."""
    prompts = {
        "Natural": None,
        "More descriptive": "a detailed photograph of",
        "Social media": "a photo showing",
    }
    prompt = prompts[style]
    if prompt:
        inputs = processor(images=image, text=prompt, return_tensors="pt")
    else:
        inputs = processor(images=image, return_tensors="pt")

    inputs = {key: value.to(device) for key, value in inputs.items()}
    if device == "cuda" and "pixel_values" in inputs:
        inputs["pixel_values"] = inputs["pixel_values"].to(dtype=torch.float16)

    with torch.inference_mode():
        output = model.generate(
            **inputs,
            max_new_tokens=MAX_NEW_TOKENS,
            num_beams=5,
            repetition_penalty=1.15,
            no_repeat_ngram_size=2,
            early_stopping=True,
        )
    caption = processor.decode(output[0], skip_special_tokens=True).strip()
    if prompt and caption.lower().startswith(prompt):
        caption = caption[len(prompt):].strip(" ,.:;-\n")
    if not caption:
        raise RuntimeError("The model returned an empty caption. Please try another image.")
    return caption[0].upper() + caption[1:] if len(caption) > 1 else caption.upper()


def make_caption_bundle(results: list[dict[str, str]]) -> bytes:
    text = ["NOVA AI - IMAGE CAPTION REPORT", "=" * 36, ""]
    for item in results:
        text.extend([
            f"Image: {item['filename']}",
            f"Generated: {item['time']}",
            f"Caption: {item['caption']}",
            "",
            "-" * 36,
            "",
        ])
    return "\n".join(text).encode("utf-8")


# ---- Session state -----------------------------------------------------------
if "results" not in st.session_state:
    st.session_state.results = []
if "last_run_device" not in st.session_state:
    st.session_state.last_run_device = "Not loaded"


# ---- Sidebar -----------------------------------------------------------------
with st.sidebar:
    st.markdown("<div class='nova-brand'>✦ NOVA AI STUDIO</div>", unsafe_allow_html=True)
    st.markdown("## Your image, described.")
    st.markdown("<p class='nova-muted'>An AI-powered image captioning workspace.</p>", unsafe_allow_html=True)
    st.divider()
    st.markdown("**MODEL**")
    st.caption("Salesforce / BLIP Large")
    st.markdown("**SUPPORTED FILES**")
    st.caption("JPG · JPEG · PNG · WEBP")
    st.markdown("**UPLOAD LIMIT**")
    st.caption(f"Up to {MAX_FILES} images · {MAX_FILE_SIZE_MB} MB each")
    st.divider()
    st.markdown("**WORKFLOW**")
    st.caption("01  Upload images\n\n02  Choose caption style\n\n03  Generate and download")
    if st.button("Clear caption history", use_container_width=True):
        st.session_state.results = []
        st.rerun()
    st.divider()
    st.caption("CODSOFT Internship · Project 03")


# ---- Hero --------------------------------------------------------------------
st.markdown(
    """
    <div class="nova-hero">
        <div class="nova-brand">✦ VISUAL INTELLIGENCE / 001</div>
        <div class="nova-title">See the image.<br>Find the words.</div>
        <div class="nova-subtitle">Turn photographs into clear, natural-language captions with a locally running vision-language model. Upload up to five images and build a caption report in seconds.</div>
        <div style="margin-top:18px">
            <span class="nova-chip">BLIP LARGE</span>
            <span class="nova-chip">PYTHON</span>
            <span class="nova-chip">PRIVATE LOCAL INFERENCE</span>
        </div>
    </div>
    """,
    unsafe_allow_html=True,
)


# ---- Upload and settings -----------------------------------------------------
settings_col, tips_col = st.columns([1.35, 1], gap="large")
with settings_col:
    st.markdown("### 01 / Add your images")
    uploaded_files = st.file_uploader(
        "Choose one or more images",
        type=["jpg", "jpeg", "png", "webp"],
        accept_multiple_files=True,
        help=f"Choose up to {MAX_FILES} images. Each image must be {MAX_FILE_SIZE_MB} MB or smaller.",
        label_visibility="collapsed",
    )
    if uploaded_files and len(uploaded_files) > MAX_FILES:
        st.warning(f"Please select no more than {MAX_FILES} images.")
        uploaded_files = uploaded_files[:MAX_FILES]

with tips_col:
    st.markdown("### 02 / Caption settings")
    caption_style = st.selectbox(
        "Caption style",
        ["Natural", "More descriptive", "Social media"],
        help="The style prompt guides the model. The model may still produce a concise caption.",
    )
    st.markdown(
        "<div class='nova-panel'><div class='nova-stat-label'>QUALITY PROFILE</div>"
        "<div style='font-weight:700;margin:7px 0'>Beam search · 5 beams</div>"
        "<div class='nova-muted'>Uses the larger BLIP checkpoint for stronger visual descriptions. Captions can still contain mistakes.</div></div>",
        unsafe_allow_html=True,
    )


# ---- Image previews ----------------------------------------------------------
valid_images: list[tuple[Any, Image.Image]] = []
image_errors: list[str] = []
if uploaded_files:
    st.markdown("### 03 / Preview")
    preview_cols = st.columns(min(len(uploaded_files), 5))
    for index, uploaded_file in enumerate(uploaded_files):
        try:
            image = prepare_image(uploaded_file)
            valid_images.append((uploaded_file, image))
            with preview_cols[index]:
                st.image(image, caption=uploaded_file.name, use_container_width=True)
        except ValueError as exc:
            image_errors.append(str(exc))
    for error in image_errors:
        st.error(error)


# ---- Generate ---------------------------------------------------------------
st.markdown("### 04 / Generate captions")
run_col, info_col = st.columns([1, 2], vertical_alignment="center")
with run_col:
    generate_clicked = st.button(
        "✦  Generate caption" if len(valid_images) <= 1 else "✦  Generate captions",
        type="primary",
        use_container_width=True,
        disabled=not valid_images,
    )
with info_col:
    st.markdown(
        "<div class='nova-muted'>The first run downloads the BLIP Large model. After that, model files are cached on this computer.</div>",
        unsafe_allow_html=True,
    )

if generate_clicked and valid_images:
    try:
        with st.spinner("Loading NOVA vision model… first run may take a few minutes"):
            processor, model, device = load_caption_model()
        st.session_state.last_run_device = "NVIDIA GPU" if device == "cuda" else "CPU"
        progress = st.progress(0, text="Preparing images…")
        new_results: list[dict[str, str]] = []
        for index, (uploaded_file, image) in enumerate(valid_images, start=1):
            progress.progress((index - 1) / len(valid_images), text=f"Analyzing {uploaded_file.name}…")
            caption = create_caption(image, processor, model, device, caption_style)
            new_results.append({
                "filename": uploaded_file.name,
                "caption": caption,
                "time": datetime.now().strftime("%d %b %Y · %I:%M %p"),
            })
            progress.progress(index / len(valid_images), text=f"Completed {index} of {len(valid_images)}")
        st.session_state.results = new_results + st.session_state.results
        st.success(f"Successfully generated {len(new_results)} caption(s).")
    except Exception as exc:
        st.error("NOVA could not complete caption generation.")
        st.warning("Check your internet connection on the first run, available memory, and the troubleshooting section in README.md.")
        with st.expander("Technical details"):
            st.code(f"{type(exc).__name__}: {exc}")


# ---- Results -----------------------------------------------------------------
if st.session_state.results:
    st.divider()
    result_header, result_meta = st.columns([2, 1], vertical_alignment="center")
    with result_header:
        st.markdown("### 05 / Caption results")
    with result_meta:
        st.markdown(
            f"<div style='text-align:right;color:#a4a2b8;font-size:.85rem'>Runtime: {html.escape(st.session_state.last_run_device)}</div>",
            unsafe_allow_html=True,
        )

    for index, item in enumerate(st.session_state.results[:10]):
        with st.container(border=True):
            left, right = st.columns([5, 1], vertical_alignment="center")
            with left:
                st.markdown(f"**{html.escape(item['filename'])}**")
                st.markdown(
                    f"<div class='nova-caption'>{html.escape(item['caption'])}</div>",
                    unsafe_allow_html=True,
                )
                st.caption(f"Generated {item['time']}")
            with right:
                st.download_button(
                    "Download .txt",
                    data=item["caption"],
                    file_name=f"{item['filename'].rsplit('.', 1)[0]}_caption.txt",
                    mime="text/plain",
                    key=f"download_one_{index}_{item['filename']}",
                    use_container_width=True,
                )

    report = make_caption_bundle(st.session_state.results)
    st.download_button(
        "↓  Download all captions",
        data=report,
        file_name="nova_ai_caption_report.txt",
        mime="text/plain",
        use_container_width=True,
    )


# ---- Footer ------------------------------------------------------------------
st.divider()
st.markdown(
    "<div style='text-align:center;padding:14px 0 4px;color:#77758d;font-size:.82rem'>"
    "NOVA AI · Image Caption Studio · Built with Python, Streamlit and Salesforce BLIP"
    "<br>AI-generated captions are suggestions and should be reviewed for accuracy."
    "</div>",
    unsafe_allow_html=True,
)
