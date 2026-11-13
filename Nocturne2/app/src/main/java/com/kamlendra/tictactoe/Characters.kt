package com.kamlendra.tictactoe

import androidx.compose.ui.graphics.Color

data class AnimeCharacter(
    val name: String,
    val age: Int,
    val tagline: String,
    val accent: Color,
    val deepAccent: Color,
    val hair: Color,
    val skin: Color,
    val eye: Color,
    val outfit: Color,
    val kanji: String
)

val nocturneCharacters = listOf(
    AnimeCharacter(
        name = "REINA", age = 24, tagline = "Elegant • Confident",
        accent = Color(0xFFE18BAA), deepAccent = Color(0xFF681D3B),
        hair = Color(0xFF21121D), skin = Color(0xFFFFDCCF),
        eye = Color(0xFFB85A79), outfit = Color(0xFF5D1835), kanji = "玲"
    ),
    AnimeCharacter(
        name = "MIYUKI", age = 23, tagline = "Sweet • Playful",
        accent = Color(0xFFF1A5C9), deepAccent = Color(0xFF85345C),
        hair = Color(0xFFE89AB9), skin = Color(0xFFFFE0D8),
        eye = Color(0xFF6A8FBD), outfit = Color(0xFF8D315F), kanji = "美"
    ),
    AnimeCharacter(
        name = "AIKO", age = 22, tagline = "Energetic • Cheerful",
        accent = Color(0xFFBBA7F5), deepAccent = Color(0xFF51417F),
        hair = Color(0xFFD5D2E8), skin = Color(0xFFFFDFD2),
        eye = Color(0xFF8B79C6), outfit = Color(0xFF45406F), kanji = "愛"
    )
)

enum class CharacterMood { IDLE, THINKING, HAPPY, DISAPPOINTED, WINNING, LOSING }
