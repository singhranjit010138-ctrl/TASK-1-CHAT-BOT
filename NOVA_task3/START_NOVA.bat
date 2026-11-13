@echo off
setlocal
cd /d "%~dp0"
title NOVA AI - Image Caption Studio

 echo.
echo  ======================================================
echo                 NOVA AI - IMAGE CAPTIONING
echo  ======================================================
echo.

if not exist "venv\Scripts\python.exe" (
    echo [1/4] Creating a Python virtual environment...
    py -3.11 -m venv venv >nul 2>&1
    if errorlevel 1 (
        py -3.12 -m venv venv >nul 2>&1
    )
    if errorlevel 1 (
        python -m venv venv
    )
    if errorlevel 1 goto :python_error
) else (
    echo [1/4] Existing virtual environment found.
)

if not exist "venv\Scripts\python.exe" goto :python_error

echo.
echo [2/4] Updating pip...
"venv\Scripts\python.exe" -m pip install --upgrade pip
if errorlevel 1 goto :install_error

if not exist ".nova_setup_complete" (
    echo.
    echo [3/4] Installing CPU version of PyTorch...
    echo This is a large download and may take several minutes.
    "venv\Scripts\python.exe" -m pip install torch --index-url https://download.pytorch.org/whl/cpu
    if errorlevel 1 goto :install_error

    echo.
    echo [4/4] Installing NOVA AI requirements...
    "venv\Scripts\python.exe" -m pip install -r requirements.txt
    if errorlevel 1 goto :install_error
    echo setup complete> .nova_setup_complete
) else (
    echo [3/4] Dependencies already installed.
    echo [4/4] Starting NOVA AI...
)

echo.
echo Starting NOVA AI in your browser...
echo Keep this window open while using the app.
echo Press Ctrl+C in this window to stop the app.
echo.
"venv\Scripts\python.exe" -m streamlit run app.py
if errorlevel 1 goto :run_error
goto :end

:python_error
echo.
echo ERROR: Python 3.11 or 3.12 was not found.
echo Install Python 3.11 or 3.12 from https://www.python.org/downloads/
echo During setup, tick "Add Python to PATH".
pause
goto :end

:install_error
echo.
echo ERROR: A package could not be installed.
echo Check your internet connection, then run START_NOVA.bat again.
pause
goto :end

:run_error
echo.
echo NOVA AI stopped because of an error. Read the message above.
pause

:end
endlocal
