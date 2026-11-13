package com.kamlendra.tictactoe

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.drawscope.DrawScope

@Composable
fun CharacterArt(
    character: AnimeCharacter,
    modifier: Modifier = Modifier,
    mood: CharacterMood = CharacterMood.IDLE
) {
    val imageRes = when (character.name.uppercase()) {
        "REINA" -> R.drawable.reina
        "MIYUKI" -> R.drawable.miyuki
        "AIKO" -> R.drawable.aiko
        else -> R.drawable.reina
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        character.deepAccent,
                        Color(0xFF1A101D),
                        character.accent.copy(alpha = 0.55f)
                    )
                )
            )
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = character.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

private fun DrawScope.drawCharacter(
    c: AnimeCharacter,
    bob: Float,
    mood: CharacterMood
) {
    val w = size.width
    val h = size.height
    val cx = w * 0.50f
    val yShift = bob

    drawCircle(
        c.accent.copy(alpha = 0.16f),
        radius = w * 0.42f,
        center = Offset(cx, h * 0.38f)
    )
    drawCircle(
        Color.White.copy(alpha = 0.24f),
        radius = w * 0.012f,
        center = Offset(w * 0.18f, h * 0.22f)
    )
    drawCircle(
        c.accent.copy(alpha = 0.7f),
        radius = w * 0.018f,
        center = Offset(w * 0.82f, h * 0.31f)
    )

    val torso = Path().apply {
        moveTo(w * 0.12f, h)
        cubicTo(w * 0.16f, h * 0.70f, w * 0.34f, h * 0.68f, cx, h * 0.68f)
        cubicTo(w * 0.68f, h * 0.68f, w * 0.86f, h * 0.72f, w * 0.91f, h)
        close()
    }

    drawPath(torso, c.outfit)
    drawPath(torso, c.accent.copy(alpha = 0.8f), style = Stroke(width = w * 0.012f))
    drawCircle(Color(0xFFD9B47E), w * 0.025f, Offset(cx, h * 0.81f))

    drawOval(c.hair, topLeft = Offset(w * 0.19f, h * 0.08f + yShift), size = Size(w * 0.62f, h * 0.65f))
    drawOval(c.hair, topLeft = Offset(w * 0.14f, h * 0.22f + yShift), size = Size(w * 0.19f, h * 0.56f))
    drawOval(c.hair, topLeft = Offset(w * 0.67f, h * 0.22f + yShift), size = Size(w * 0.19f, h * 0.56f))

    drawOval(c.skin, topLeft = Offset(w * 0.31f, h * 0.20f + yShift), size = Size(w * 0.38f, h * 0.42f))
    drawOval(c.skin.copy(alpha = 0.55f), topLeft = Offset(w * 0.28f, h * 0.40f + yShift), size = Size(w * 0.08f, h * 0.08f))
    drawOval(c.skin.copy(alpha = 0.55f), topLeft = Offset(w * 0.64f, h * 0.40f + yShift), size = Size(w * 0.08f, h * 0.08f))

    val fringe = Path().apply {
        moveTo(w * 0.25f, h * 0.35f + yShift)
        cubicTo(w * 0.18f, h * 0.10f + yShift, w * 0.48f, h * 0.02f + yShift, w * 0.72f, h * 0.20f + yShift)
        cubicTo(w * 0.82f, h * 0.28f + yShift, w * 0.73f, h * 0.39f + yShift, w * 0.67f, h * 0.34f + yShift)
        cubicTo(w * 0.58f, h * 0.27f + yShift, w * 0.52f, h * 0.34f + yShift, w * 0.48f, h * 0.38f + yShift)
        cubicTo(w * 0.42f, h * 0.28f + yShift, w * 0.35f, h * 0.31f + yShift, w * 0.25f, h * 0.35f + yShift)
        close()
    }
    drawPath(fringe, c.hair)

    val eyeY = h * 0.43f + yShift
    val eyeW = w * 0.065f

    if (mood == CharacterMood.LOSING || mood == CharacterMood.DISAPPOINTED) {
        drawLine(c.eye, Offset(w * 0.39f, eyeY), Offset(w * 0.46f, eyeY + h * 0.012f), w * 0.012f)
        drawLine(c.eye, Offset(w * 0.54f, eyeY + h * 0.012f), Offset(w * 0.61f, eyeY), w * 0.012f)
    } else {
        drawOval(c.eye, Offset(w * 0.38f, eyeY - h * 0.025f), Size(eyeW, h * 0.075f))
        drawOval(c.eye, Offset(w * 0.55f, eyeY - h * 0.025f), Size(eyeW, h * 0.075f))
        drawCircle(Color.White, w * 0.012f, Offset(w * 0.405f, eyeY - h * 0.005f))
        drawCircle(Color.White, w * 0.012f, Offset(w * 0.575f, eyeY - h * 0.005f))
    }

    drawLine(c.hair.copy(alpha = 0.85f), Offset(w * 0.37f, eyeY - h * 0.055f), Offset(w * 0.46f, eyeY - h * 0.065f), w * 0.014f)
    drawLine(c.hair.copy(alpha = 0.85f), Offset(w * 0.54f, eyeY - h * 0.065f), Offset(w * 0.63f, eyeY - h * 0.055f), w * 0.014f)

    val mouthY = h * 0.535f + yShift
    val mouth = Path().apply {
        moveTo(w * 0.46f, mouthY)
        if (mood == CharacterMood.HAPPY || mood == CharacterMood.WINNING) {
            cubicTo(w * 0.49f, mouthY + h * 0.045f, w * 0.53f, mouthY + h * 0.045f, w * 0.56f, mouthY)
        } else {
            cubicTo(w * 0.49f, mouthY - h * 0.018f, w * 0.53f, mouthY - h * 0.018f, w * 0.56f, mouthY)
        }
    }

    drawPath(mouth, Color(0xFF9C4D65), style = Stroke(width = w * 0.012f))
    drawLine(c.accent, Offset(w * 0.22f, h * 0.17f + yShift), Offset(w * 0.15f, h * 0.74f), w * 0.055f)
    drawLine(c.accent, Offset(w * 0.76f, h * 0.17f + yShift), Offset(w * 0.84f, h * 0.71f), w * 0.055f)
}