# NOVA AI — Image Caption Studio

A Python image-captioning application with a premium Streamlit interface. Upload up to five images, generate captions with the **Salesforce BLIP Large** vision-language model, and download individual captions or a combined report.

## Features

- JPG, JPEG, PNG and WEBP image uploads
- Batch captioning (up to five images per run)
- Three caption styles: Natural, More descriptive and Social media
- BLIP Large model with beam-search decoding
- Individual `.txt` downloads and a combined caption report
- Model is cached locally after its first download
- Automatically uses an NVIDIA GPU when a CUDA-enabled PyTorch build is installed; otherwise uses CPU
- Image processing happens locally after the model has downloaded

## Requirements

- Windows 10/11
- Python 3.11 (recommended) or Python 3.12
- Internet connection for the first setup and model download
- Several GB of free disk space for Python packages and the model cache

The BLIP Large checkpoint is substantial (around 1.8 GB according to the `blip-caption` package documentation), so the first launch can take time. The model weights are not bundled in this ZIP.

## Easiest way to run on Windows

1. Extract the ZIP to a normal folder, for example `Desktop\NOVA_IMAGE_CAPTIONING`.
2. Install Python 3.11 or 3.12 if it is not already installed. During Python setup, enable **Add Python to PATH**.
3. Double-click **`START_NOVA.bat`**.
4. On the first launch, the script creates a virtual environment and installs CPU PyTorch and the required packages. Keep the terminal open.
5. Streamlit opens the app in your browser. If it does not, open the local URL printed in the terminal (usually `http://localhost:8501`).
6. Upload images, choose a caption style and click **Generate caption(s)**.

On later launches, double-click `START_NOVA.bat` again. The setup is skipped and the app starts directly.

## Manual setup (VS Code terminal)

Open this folder in VS Code and run:

```powershell
py -3.11 -m venv venv
.\venv\Scripts\Activate.ps1
python -m pip install --upgrade pip
python -m pip install torch --index-url https://download.pytorch.org/whl/cpu
python -m pip install -r requirements.txt
python -m streamlit run app.py
```

If PowerShell blocks activation, you can skip activation and run the commands using `venv\Scripts\python.exe -m pip ...` followed by `venv\Scripts\python.exe -m streamlit run app.py`.

## Model and attribution

- Model: [Salesforce/blip-image-captioning-large](https://huggingface.co/Salesforce/blip-image-captioning-large)
- Framework: [Hugging Face Transformers](https://huggingface.co/docs/transformers/index)
- UI: [Streamlit](https://docs.streamlit.io/)
- The BLIP Large model card lists a BSD-3-Clause license. Check the model card and each dependency's license before redistribution or commercial use.

## Troubleshooting

**The first run seems stuck:** The model is downloading. Wait for the download to finish and keep the terminal open. A stable internet connection is recommended.

**`py` or Python is not found:** Install Python 3.11 or 3.12 from https://www.python.org/downloads/ and enable `Add Python to PATH`, then reopen the terminal.

**A package installation fails:** Check the internet connection and available disk space, then run `START_NOVA.bat` again. If setup was interrupted, delete `.nova_setup_complete` if it exists and run the script again.

**Out of memory:** Close other applications and try one image at a time. The app automatically uses CPU PyTorch from the included Windows setup script. CPU inference can be slower, especially with the Large model.

**Port 8501 is already in use:** Stop the other Streamlit app, or run `venv\Scripts\python.exe -m streamlit run app.py --server.port 8502`.

## Important note about accuracy

BLIP is an image-captioning model, not a perfect image-understanding system. It can miss details or describe an image incorrectly. Review captions before using them in public or high-stakes contexts.

## Project

**NOVA AI — CODSOFT Internship, Project 03**
