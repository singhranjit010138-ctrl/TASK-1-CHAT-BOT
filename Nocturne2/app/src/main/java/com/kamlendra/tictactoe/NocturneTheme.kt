package com.kamlendra.tictactoe

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NocturneColors = darkColorScheme(
    primary = Color(0xFFE18BAA),
    onPrimary = Color(0xFF2A0D1C),
    secondary = Color(0xFFD9B47E),
    onSecondary = Color(0xFF27170A),
    tertiary = Color(0xFFBBA7F5),
    background = Color(0xFF100A13),
    surface = Color(0xFF1C121F),
    surfaceVariant = Color(0xFF2B1A2B),
    onBackground = Color(0xFFFFEDF4),
    onSurface = Color(0xFFFFEDF4)
)

@Composable
fun NocturneTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = NocturneColors, content = content)
}
