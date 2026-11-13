# CODSOFT Artificial Intelligence Internship --- Projects Portfolio

A collection of three projects developed as part of the **CODSOFT AI
Internship**.

**Author:** Gaurav\
**Repository:**
[codsoft_tasks](https://github.com/singhranjit010138-ctrl/codsoft_tasks)

## Projects at a glance

  -----------------------------------------------------------------------
  Task              Project           Description       Technologies
  ----------------- ----------------- ----------------- -----------------
  1                 **NOVA --- AI     Interactive       Python
                    Chatbot**         command-line      
                                      chatbot with      
                                      supported-topic   
                                      responses and a   
                                      help menu.        

  2                 **NOCTURNE 2.0    Android           Kotlin, Android
                    --- Anime         Tic-Tac-Toe with  
                    Tic-Tac-Toe**     Player vs AI and  
                                      two-player modes. 

  3                 **NOVA AI ---     Streamlit app     Python,
                    Image Caption     that generates    Streamlit, BLIP
                    Studio**          image             
                                      descriptions      
                                      using Salesforce  
                                      BLIP Large.       
  -----------------------------------------------------------------------

------------------------------------------------------------------------

## Task 1 --- NOVA AI Chatbot

A terminal-based Python chatbot. Users can chat with NOVA, ask supported
questions, view the help menu, and exit the conversation.

### Highlights

-   Interactive command-line conversation.
-   Greeting and general-topic responses.
-   Built-in `help` command.
-   Exit handling for common quit commands.

### Screenshot gallery

![NOVA chatbot --- code and
terminal](assets/screenshots/task1-chatbot-code.jpg) ![NOVA chatbot ---
conversation](assets/screenshots/task1-chatbot-conversation.jpg) ![NOVA
chatbot --- help menu](assets/screenshots/task1-chatbot-help.jpg)

### Run locally

``` bash
python chatbot.py
```

Run this command from the Task 1 project directory. Type `help` to view
supported topics and `exit` or `bye` to leave.

------------------------------------------------------------------------

## Task 2 --- NOCTURNE 2.0: Anime Tic-Tac-Toe

An Android Tic-Tac-Toe game with a dark anime-inspired visual style.

### Highlights

-   Player vs AI gameplay.
-   Two-player pass-and-play mode.
-   Minimax-based AI opponent, as described in the game interface.
-   Scoreboard for wins and draws.
-   Anime-inspired character and visual presentation.
-   Round result and score-reset controls.

### Screenshot gallery

![NOCTURNE 2.0 --- home
screen](assets/screenshots/task2-nocturne-home.jpg) ![NOCTURNE 2.0 ---
gameplay](assets/screenshots/task2-nocturne-gameplay.jpg) ![NOCTURNE 2.0
--- round result](assets/screenshots/task2-nocturne-result.jpg)

### Run locally

1.  Open the `Nocturne2` folder in Android Studio.
2.  Allow Gradle sync to finish and dependencies to download.
3.  Select an Android emulator or connect an Android device with USB
    debugging enabled.
4.  Click **Run**.

**Package name:** `com.kamlendra.tictactoe`

------------------------------------------------------------------------

## Task 3 --- NOVA AI: Image Caption Studio

A Streamlit web application that uses Salesforce BLIP Large to generate
natural-language captions for uploaded images.

### Highlights

-   Upload up to **5 images** per session.
-   Supports **JPG, JPEG, PNG, and WEBP**.
-   Upload limit shown in the app: **12 MB per image**.
-   Image preview and caption-style selection.
-   Caption results displayed in the app.
-   Download generated captions.
-   Local model inference; the first run downloads the model and later
    runs can use cached model files.

### Screenshot gallery

![NOVA AI --- studio and upload
interface](assets/screenshots/task3-novaai-home.jpg) ![NOVA AI --- image
preview and generate-caption
section](assets/screenshots/task3-novaai-preview.jpg) ![NOVA AI ---
generated caption results](assets/screenshots/task3-novaai-results.jpg)

### Run locally

Open PowerShell in the `NOVA_task3` folder:

``` powershell
py -3.12 -m venv venv
.env\Scripts\Activate.ps1
python -m pip install --upgrade pip
python -m pip install -r requirements.txt
python -m streamlit run app.py
```

If `venv` already exists, skip the first command and activate it. Open
the local URL printed by Streamlit (usually `http://localhost:8501`) in
your browser.

**Note:** The first run may take longer because BLIP Large must be
downloaded. Runtime and inference speed depend on the computer; the
supplied screenshot shows CPU runtime.

------------------------------------------------------------------------

## Repository structure

``` text
codsoft_tasks/
├── TASK 1 CHAT  BOT/
│   ├── chatbot.py
│   ├── readme.md
│   └── Screenshots/
├── Nocturne2/
│   └── Android Studio project files
├── NOVA_task3/
│   ├── app.py
│   ├── requirements.txt
│   ├── README.md
│   └── START_NOVA.bat
├── assets/
│   └── screenshots/
└── README.md
```

The gallery paths above expect the screenshots to be stored in
`assets/screenshots/` at the repository root.

## Learning outcomes

-   Python programming and interactive command-line application
    development.
-   Android development with Kotlin and Android Studio.
-   Game logic and AI opponent implementation.
-   Building web interfaces with Streamlit.
-   Using a pretrained vision-language model for image caption
    generation.
-   Organising and documenting projects in GitHub.

## Acknowledgements

Developed as part of the **CODSOFT Artificial Intelligence Internship**.

## Disclaimer

These are educational internship projects. AI-generated image captions
may contain errors and should be reviewed before use in important
contexts.
