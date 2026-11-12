import random
import re
from datetime import datetime

# ==========================================
# CODSOFT AI INTERNSHIP
# TASK 1: ADVANCED RULE-BASED CHATBOT
# ==========================================

BOT_NAME = "Nova"

responses = {
    "greeting": [
        "Hello! Welcome. How can I help you today?",
        "Hey there! It's great to talk with you.",
        "Hi! What would you like to discuss?",
        "Hello! I am ready to assist you."
    ],

    "how_are_you": [
        "I'm doing great! Thanks for asking.",
        "I'm functioning perfectly. How about you?",
        "Doing wonderful! What can I help you with?"
    ],

    "name": [
        "My name is Nova, your Python-powered chatbot.",
        "I'm Nova, your virtual assistant."
    ],

    "creator": [
        "I was developed by Gaurav as part of the CODSOFT AI Internship.",
        "Gaurav created me using Python programming."
    ],

    "purpose": [
        "I'm designed to answer basic questions and have simple conversations.",
        "My purpose is to demonstrate rule-based conversational AI."
    ],

    "python": [
        "Python is a popular, high-level programming language.",
        "Python is widely used in AI, automation, web development and data science."
    ],

    "ai": [
        "Artificial Intelligence enables machines to perform tasks that normally require human intelligence.",
        "AI includes areas like machine learning, natural language processing and computer vision."
    ],

    "machine_learning": [
        "Machine Learning is a branch of AI that allows computers to learn patterns from data.",
        "Machine Learning uses algorithms to make predictions and decisions."
    ],

    "coding": [
        "Coding is the process of writing instructions for computers.",
        "Programming helps us solve real-world problems through logic."
    ],

    "study": [
        "Consistency is the key to learning. Practice a little every day!",
        "Try breaking difficult topics into smaller concepts."
    ],

    "motivation": [
        "Success comes from consistency, not perfection.",
        "Every expert was once a beginner. Keep going!",
        "Don't stop when you are tired. Stop when your work is done."
    ],

    "joke": [
        "Why do programmers prefer dark mode? Because light attracts bugs!",
        "Why did the Python developer wear glasses? Because he couldn't C!",
        "There are 10 types of people: those who understand binary and those who don't."
    ],

    "thanks": [
        "You're welcome!",
        "Happy to help!",
        "Anytime! Let me know if you need anything else."
    ],

    "goodbye": [
        "Goodbye! Have an amazing day!",
        "See you soon. Keep learning!",
        "Take care! It was nice talking to you."
    ],

    "weather": [
        "I cannot access live weather data yet, but you can check your local weather app."
    ],

    "fallback": [
        "Interesting! Could you explain that differently?",
        "I'm not sure I understand. Try asking another question.",
        "I don't have an answer for that yet. Can you rephrase it?",
        "That's a good question! Unfortunately, I haven't been trained for that response."
    ]
}


def get_response(user_input):

    message = user_input.lower().strip()
    message = re.sub(r"[^\w\s]", "", message)

    if not message:
        return "Please enter something so we can start chatting!"

    # Greetings
    if re.search(r"\b(hello|hi|hey|namaste|good morning|good evening)\b", message):
        return random.choice(responses["greeting"])

    # Identity
    elif re.search(r"\b(your name|who are you|what are you)\b", message):
        return random.choice(responses["name"])

    # Creator
    elif re.search(r"\b(who created you|who made you|your creator)\b", message):
        return random.choice(responses["creator"])

    # Purpose
    elif re.search(r"\b(what can you do|your purpose|your work)\b", message):
        return random.choice(responses["purpose"])

    # Conversation
    elif "how are you" in message or "how do you feel" in message:
        return random.choice(responses["how_are_you"])

    # Date and time
    elif re.search(r"\b(time|current time)\b", message):
        return "Current time: " + datetime.now().strftime("%I:%M %p")

    elif re.search(r"\b(date|today date|todays date)\b", message):
        return "Today's date: " + datetime.now().strftime("%d-%m-%Y")

    # Technology
    elif re.search(r"\b(python|what is python)\b", message):
        return random.choice(responses["python"])

    elif re.search(r"\b(artificial intelligence|what is ai|ai)\b", message):
        return random.choice(responses["ai"])

    elif re.search(r"\b(machine learning|what is ml)\b", message):
        return random.choice(responses["machine_learning"])

    elif re.search(r"\b(coding|programming|programmer)\b", message):
        return random.choice(responses["coding"])

    # Personal assistance
    elif re.search(r"\b(study|studying|exam|learning)\b", message):
        return random.choice(responses["study"])

    elif re.search(r"\b(motivate|motivation|inspire|inspiration)\b", message):
        return random.choice(responses["motivation"])

    # Entertainment
    elif re.search(r"\b(joke|jokes|make me laugh|funny)\b", message):
        return random.choice(responses["joke"])

    # General
    elif re.search(r"\b(weather|temperature)\b", message):
        return random.choice(responses["weather"])

    elif re.search(r"\b(thank|thanks|thank you)\b", message):
        return random.choice(responses["thanks"])

    # Exit
    elif re.search(r"\b(bye|goodbye|exit|quit)\b", message):
        return random.choice(responses["goodbye"])

    # Unknown questions
    else:
        return random.choice(responses["fallback"])


def display_menu():
    print("""
--------------- CHATBOT HELP ----------------
You can ask me about:

1. Greetings
2. My name and creator
3. My purpose
4. Python programming
5. Artificial Intelligence
6. Machine Learning
7. Coding
8. Study and motivation
9. Jokes
10. Date and time
11. Weather
12. Exit

Type 'help' anytime to see this menu.
------------------------------------------------
""")


def main():

    print("=" * 55)
    print("             NOVA - AI CHATBOT")
    print("       CODSOFT ARTIFICIAL INTELLIGENCE INTERNSHIP")
    print("=" * 55)

    print("\nNova: Hello! I'm Nova, your virtual assistant.")
    print("Nova: Type 'help' to explore my features.")
    print("Nova: Type 'bye' whenever you want to exit.\n")

    while True:

        user_input = input("You: ").strip()

        if user_input.lower() == "help":
            display_menu()
            continue

        if not user_input:
            print("Nova: Please type a message.\n")
            continue

        response = get_response(user_input)

        print(f"Nova: {response}\n")

        if re.search(r"\b(bye|goodbye|exit|quit)\b",
                     re.sub(r"[^\w\s]", "", user_input.lower())):
            break

    print("=" * 55)
    print("Thank you for chatting with NOVA!")
    print("Developed by Gaurav | CODSOFT AI Internship")
    print("=" * 55)


if __name__ == "__main__":
    main()