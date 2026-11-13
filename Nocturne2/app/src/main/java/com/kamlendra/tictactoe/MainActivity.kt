package com.kamlendra.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay

private val Night = Color(0xFF100A13)
private val Panel = Color(0xFF1D1320)
private val PanelSoft = Color(0xFF281A2C)
private val Rose = Color(0xFFE18BAA)
private val Gold = Color(0xFFD9B47E)
private val Wine = Color(0xFF4B1F35)
private val Lavender = Color(0xFFBBA7F5)
private val TextMain = Color(0xFFFFEDF4)
private val TextMuted = Color(0xFFB9A5B3)

private enum class Page { HOME, CHARACTERS, GAME, SETTINGS, SCORES }
private enum class MatchMode { AI, TWO_PLAYERS }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(16, 10, 19)
        window.navigationBarColor = android.graphics.Color.rgb(16, 10, 19)
        setContent { NocturneTheme { NocturneApp() } }
    }
}

@Composable
private fun NocturneApp() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val prefs = remember { context.getSharedPreferences("nocturne_settings", 0) }
    val audio = remember { AudioController(context) }

    var page by remember { mutableStateOf(Page.HOME) }
    var mode by remember { mutableStateOf(MatchMode.AI) }
    var selected by remember { mutableIntStateOf(prefs.getInt("character", 0).coerceIn(0, 2)) }
    var alternateStyle by remember { mutableStateOf(prefs.getBoolean("alternate_style", false)) }
    var musicOn by remember { mutableStateOf(prefs.getBoolean("music", true)) }
    var effectsOn by remember { mutableStateOf(prefs.getBoolean("effects", true)) }
    var board by remember { mutableStateOf(List(9) { "" }) }
    var turn by remember { mutableStateOf("X") }
    var result by remember { mutableStateOf("") }
    var winningCells by remember { mutableStateOf(emptyList<Int>()) }
    var thinking by remember { mutableStateOf(false) }
    var xWins by remember { mutableIntStateOf(prefs.getInt("x_wins", 0)) }
    var oWins by remember { mutableIntStateOf(prefs.getInt("o_wins", 0)) }
    var draws by remember { mutableIntStateOf(prefs.getInt("draws", 0)) }

    fun persistSettings() {
        prefs.edit()
            .putInt("character", selected)
            .putBoolean("alternate_style", alternateStyle)
            .putBoolean("music", musicOn)
            .putBoolean("effects", effectsOn)
            .apply()
    }
    fun persistScores() {
        prefs.edit().putInt("x_wins", xWins).putInt("o_wins", oWins).putInt("draws", draws).apply()
    }
    fun resetRound() {
        board = List(9) { "" }
        turn = "X"
        result = ""
        winningCells = emptyList()
        thinking = false
    }
    fun checkResult(next: List<String>): Boolean {
        val line = GameLogic.winningLine(next)
        if (line.isNotEmpty()) {
            winningCells = line
            result = next[line.first()]
            if (result == "X") xWins++ else oWins++
            persistScores()
            if (effectsOn) {
                if (result == "X") audio.playWin() else audio.playLose()
            }
            return true
        }
        if (GameLogic.isDraw(next)) {
            result = "DRAW"
            draws++
            persistScores()
            if (effectsOn) audio.playDraw()
            return true
        }
        return false
    }
    fun playerMove(index: Int) {
        if (page != Page.GAME || index !in 0..8 ||
            board[index].isNotEmpty() || result.isNotEmpty() || thinking
        ) return

        if (mode == MatchMode.AI && turn != "X") return

        val mark = turn
        val next = board.toMutableList()
        next[index] = mark
        board = next

        if (effectsOn) audio.playMove()

        if (!checkResult(next)) {
            turn = if (mark == "X") "O" else "X"
        }
    }

    DisposableEffect(audio) {
        onDispose { audio.release() }
    }
    DisposableEffect(lifecycleOwner, musicOn, page) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START && musicOn && page != Page.SETTINGS) audio.startMusic()
            if (event == Lifecycle.Event.ON_STOP) audio.pauseMusic()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) && musicOn && page != Page.SETTINGS) {
            audio.startMusic()
        } else {
            audio.pauseMusic()
        }
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(board, turn, result, mode, page) {
        if (page == Page.GAME && mode == MatchMode.AI && turn == "O" && result.isEmpty() && board.any { it.isEmpty() }) {
            thinking = true
            delay(480)
            val index = GameLogic.bestMove(board)
            if (index >= 0 && result.isEmpty()) {
                val next = board.toMutableList()
                next[index] = "O"
                board = next
                if (effectsOn) audio.playMove()
                if (!checkResult(next)) turn = "X"
            }
            thinking = false
        }
    }

    BackHandler(enabled = page != Page.HOME) {
        page = if (page == Page.GAME) Page.HOME else Page.HOME
    }

    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF2B1328), Night, Color(0xFF09070D)))
        )
    ) {
        BackgroundOrnaments()
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            TopBar(
                showBack = page != Page.HOME,
                onBack = { page = Page.HOME },
                onSettings = { page = Page.SETTINGS }
            )
            Spacer(Modifier.height(14.dp))
            AnimatedContent(targetState = page, label = "screenChange") { target ->
                when (target) {
                    Page.HOME -> HomePage(
                        selectedCharacter = nocturneCharacters[selected],
                        alternateStyle = alternateStyle,
                        onAi = { mode = MatchMode.AI; page = Page.CHARACTERS },
                        onTwoPlayers = { mode = MatchMode.TWO_PLAYERS; page = Page.CHARACTERS },
                        onScores = { page = Page.SCORES }
                    )
                    Page.CHARACTERS -> CharacterSelectPage(
                        selected = selected,
                        alternateStyle = alternateStyle,
                        onSelect = {
                            selected = it
                            prefs.edit().putInt("character", it).apply()
                            resetRound()
                            page = Page.GAME
                        }
                    )
                    Page.GAME -> GamePage(
                        mode = mode,
                        character = nocturneCharacters[selected],
                        alternateStyle = alternateStyle,
                        board = board,
                        turn = turn,
                        result = result,
                        winningCells = winningCells,
                        thinking = thinking,
                        xWins = xWins,
                        oWins = oWins,
                        draws = draws,
                        onMove = ::playerMove,
                        onNewRound = ::resetRound,
                        onResetScores = {
                            xWins = 0; oWins = 0; draws = 0
                            persistScores()
                            resetRound()
                        },
                        onHome = { page = Page.HOME }
                    )
                    Page.SETTINGS -> SettingsPage(
                        musicOn = musicOn,
                        effectsOn = effectsOn,
                        alternateStyle = alternateStyle,
                        onMusic = { musicOn = it; persistSettings() },
                        onEffects = { effectsOn = it; persistSettings() },
                        onAlternateStyle = { alternateStyle = it; persistSettings() }
                    )
                    Page.SCORES -> ScorePage(
                        xWins, oWins, draws,
                        onReset = { xWins = 0; oWins = 0; draws = 0; persistScores() }
                    )
                }
            }
            Spacer(Modifier.height(26.dp))
            Text(
                "A QUIET NIGHT  •  ONE PERFECT MOVE",
                modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                color = Color(0xFF856E7E), fontSize = 9.sp, letterSpacing = 2.sp, textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TopBar(showBack: Boolean, onBack: () -> Unit, onSettings: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        if (showBack) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Rose) }
        } else {
            Text("☾", color = Gold, fontSize = 27.sp, modifier = Modifier.padding(horizontal = 8.dp))
        }
        Column(Modifier.weight(1f)) {
            Text("NOCTURNE", color = TextMain, fontSize = 18.sp, fontWeight = FontWeight.Black, letterSpacing = 2.5.sp)
            Text("ANIME TIC-TAC-TOE  •  2.0", color = Rose, fontSize = 9.sp, letterSpacing = 2.sp)
        }
        IconButton(onClick = onSettings) {
            Icon(Icons.Default.Settings, null, tint = Rose)
        }
    }
}

@Composable
private fun BackgroundOrnaments() {
    val transition = rememberInfiniteTransition(label = "backgroundGlow")
    val alpha by transition.animateFloat(
        0.12f, 0.24f,
        infiniteRepeatable(tween(3200), RepeatMode.Reverse),
        label = "glowAlpha"
    )
    Canvas(Modifier.fillMaxSize()) {
        drawCircle(Rose.copy(alpha), radius = size.width * 0.47f, center = Offset(size.width * 1.02f, size.height * 0.12f))
        drawCircle(Lavender.copy(alpha * 0.55f), radius = size.width * 0.35f, center = Offset(-size.width * 0.12f, size.height * 0.78f))
    }
}

@Composable
private fun HomePage(
    selectedCharacter: AnimeCharacter,
    alternateStyle: Boolean,
    onAi: () -> Unit,
    onTwoPlayers: () -> Unit,
    onScores: () -> Unit
) {
    Column {
        Text("THE NIGHT IS YOURS", color = Gold, fontSize = 10.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(7.dp))
        Text("Choose your\nnext move.", color = TextMain, fontSize = 34.sp, lineHeight = 39.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(26.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF55203F), Color(0xFF211524), Color(0xFF332647))))
                .border(1.dp, Rose.copy(alpha = 0.50f), RoundedCornerShape(26.dp))
        ) {
            Row(Modifier.fillMaxSize().padding(start = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("YOUR PARTNER", color = Gold, fontSize = 9.sp, letterSpacing = 2.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(selectedCharacter.name, color = TextMain, fontSize = 25.sp, fontWeight = FontWeight.Black)
                    Text("${selectedCharacter.age} YEARS  •  ${selectedCharacter.tagline}", color = Rose, fontSize = 10.sp)
                    Spacer(Modifier.height(15.dp))
                    Text("Ready when you are.", color = TextMuted, fontSize = 12.sp)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        if (alternateStyle) "ALT STYLE ON" else "CLASSIC STYLE",
                        color = Gold, fontSize = 9.sp, letterSpacing = 1.5.sp
                    )
                }
                CharacterArt(
                    selectedCharacter,
                    Modifier.fillMaxHeight().width(142.dp),
                    CharacterMood.IDLE
                )
            }
        }
        Spacer(Modifier.height(21.dp))
        Text("SELECT GAME MODE", color = Rose, fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        FeatureButton("PLAYER  VS  AI", "Unbeatable Minimax opponent", "✦", onAi)
        FeatureButton("TWO PLAYERS", "Pass-and-play on one device", "♧", onTwoPlayers)
        FeatureButton("SCOREBOARD", "Your victories, saved locally", "♛", onScores)
        Spacer(Modifier.height(9.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, null, tint = Gold, modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(7.dp))
            Text("Three original adult characters • 21+", color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun FeatureButton(title: String, subtitle: String, symbol: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 5.dp).clip(RoundedCornerShape(17.dp))
            .background(Brush.horizontalGradient(listOf(PanelSoft, Panel)))
            .border(1.dp, Rose.copy(alpha = 0.24f), RoundedCornerShape(17.dp))
            .clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(Wine),
            contentAlignment = Alignment.Center
        ) { Text(symbol, color = Gold, fontSize = 22.sp) }
        Spacer(Modifier.width(13.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = TextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text(subtitle, color = TextMuted, fontSize = 10.sp)
        }
        Text("›", color = Rose, fontSize = 27.sp)
    }
}

@Composable
private fun CharacterSelectPage(
    selected: Int,
    alternateStyle: Boolean,
    onSelect: (Int) -> Unit
) {
    Column {
        Text("PICK YOUR PARTNER", color = Gold, fontSize = 10.sp, letterSpacing = 2.5.sp)
        Text("A little character.\nA lot of personality.", color = TextMain, fontSize = 28.sp, lineHeight = 33.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(7.dp))
        Text("All three are original adult characters, aged 21+.", color = TextMuted, fontSize = 11.sp)
        Spacer(Modifier.height(14.dp))
        nocturneCharacters.forEachIndexed { index, character ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 6.dp).height(142.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (selected == index) Brush.horizontalGradient(listOf(character.deepAccent, Panel)) else Brush.horizontalGradient(listOf(PanelSoft, Panel)))
                    .border(if (selected == index) 1.5.dp else 1.dp, if (selected == index) character.accent else character.accent.copy(alpha = 0.25f), RoundedCornerShape(22.dp))
                    .clickable { onSelect(index) }.padding(9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CharacterArt(character, Modifier.width(104.dp).fillMaxHeight(), CharacterMood.IDLE)
                Spacer(Modifier.width(13.dp))
                Column(Modifier.weight(1f)) {
                    Text(character.name, color = TextMain, fontSize = 21.sp, fontWeight = FontWeight.Black)
                    Text("AGE  ${character.age}", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(5.dp))
                    Text(if (alternateStyle) "Glamour palette" else character.tagline, color = character.accent, fontSize = 11.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(if (selected == index) "CURRENT PARTNER" else "TAP TO PLAY  ›", color = TextMuted, fontSize = 9.sp, letterSpacing = 1.sp)
                }
            }
        }
    }
}

@Composable
private fun GamePage(
    mode: MatchMode,
    character: AnimeCharacter,
    alternateStyle: Boolean,
    board: List<String>,
    turn: String,
    result: String,
    winningCells: List<Int>,
    thinking: Boolean,
    xWins: Int,
    oWins: Int,
    draws: Int,
    onMove: (Int) -> Unit,
    onNewRound: () -> Unit,
    onResetScores: () -> Unit,
    onHome: () -> Unit
) {
    val mood = when {
        result == "X" -> CharacterMood.LOSING
        result == "O" -> CharacterMood.WINNING
        result == "DRAW" -> CharacterMood.DISAPPOINTED
        thinking -> CharacterMood.THINKING
        else -> CharacterMood.IDLE
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(if (mode == MatchMode.AI) "PLAYER VS AI" else "TWO PLAYERS", color = Gold, fontSize = 10.sp, letterSpacing = 2.sp)
                Text(if (mode == MatchMode.AI) "Challenge the perfect move." else "Take turns. Play fair.", color = TextMuted, fontSize = 11.sp)
            }
            Text(if (alternateStyle) "✧ ALT" else "✦ CLASSIC", color = Rose, fontSize = 9.sp)
        }
        Spacer(Modifier.height(13.dp))
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Panel)
                .border(1.dp, Rose.copy(.2f), RoundedCornerShape(20.dp)).padding(11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CharacterArt(character, Modifier.size(82.dp), mood)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(character.name, color = TextMain, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(if (thinking) "Thinking…" else when (result) {
                    "X" -> "That was brilliant!"
                    "O" -> "A beautiful round."
                    "DRAW" -> "A balanced match."
                    else -> if (turn == "X") "Your turn. Make it count." else "Waiting for the move…"
                }, color = Rose, fontSize = 11.sp)
                Text("AGE ${character.age}  •  ORIGINAL CHARACTER", color = TextMuted, fontSize = 8.sp, letterSpacing = 1.sp)
            }
        }
        Spacer(Modifier.height(13.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ScorePill("YOU  •  X", xWins.toString(), Rose)
            ScorePill(if (mode == MatchMode.AI) "AI  •  O" else "PLAYER 2  •  O", oWins.toString(), Lavender)
            ScorePill("DRAWS", draws.toString(), Gold)
        }
        Spacer(Modifier.height(15.dp))
        AnimatedVisibility(visible = result.isNotEmpty(), enter = fadeIn(tween(250)), exit = fadeOut(tween(150))) {
            Column {
                ResultFx(result)
                ResultBanner(result)
            }
        }
        if (result.isEmpty()) {
            Text(
                if (thinking) "THE OPPONENT IS THINKING" else "TURN  $turn",
                color = if (turn == "X") Rose else Lavender, fontSize = 12.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 2.sp
            )
        }
        Spacer(Modifier.height(10.dp))
        TicTacToeBoard(board, winningCells, result.isNotEmpty() || thinking || (mode == MatchMode.AI && turn == "O"), onMove)
        Spacer(Modifier.height(17.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            Button(
                onClick = onNewRound, modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Wine),
                shape = RoundedCornerShape(15.dp)
            ) { Text("NEW ROUND", fontSize = 11.sp, letterSpacing = 1.sp) }
            OutlinedButton(
                onClick = onResetScores, modifier = Modifier.weight(1f).height(50.dp),
                border = BorderStroke(1.dp, Rose.copy(.55f)), shape = RoundedCornerShape(15.dp)
            ) { Text("RESET SCORES", fontSize = 10.sp, color = Rose) }
        }
        TextButton(onClick = onHome) { Text("BACK TO HOME", color = TextMuted, letterSpacing = 1.sp, fontSize = 10.sp) }
    }
}

@Composable
private fun ScorePill(label: String, value: String, tint: Color) {
    Column(
        Modifier.clip(RoundedCornerShape(13.dp)).background(PanelSoft).padding(horizontal = 12.dp, vertical = 9.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = tint, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Text(label, color = TextMuted, fontSize = 8.sp, letterSpacing = 0.5.sp)
    }
}


@Composable
private fun ResultFx(result: String) {
    val phase by rememberInfiniteTransition(label = "matchFx").animateFloat(
        0f, 1f, infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "fxPhase"
    )
    Canvas(Modifier.fillMaxWidth().height(76.dp)) {
        when (result) {
            "X" -> {
                val palette = listOf(Gold, Rose, Lavender, Color.White)
                for (i in 0 until 34) {
                    val xSeed = ((i * 37) % 97) / 97f
                    val ySeed = ((i * 23) % 89) / 89f
                    val x = size.width * xSeed
                    val y = size.height * ((ySeed + phase) % 1f)
                    val radius = (2.5f + (i % 3) * 1.2f).dp.toPx()
                    drawCircle(palette[i % palette.size].copy(alpha = 0.85f), radius, Offset(x, y))
                    if (i % 4 == 0) {
                        drawLine(palette[i % palette.size], Offset(x, y), Offset(x + 7.dp.toPx(), y + 11.dp.toPx()), 2.dp.toPx())
                    }
                }
            }
            "O" -> {
                for (i in 0 until 9) {
                    val x = size.width * (i / 8f)
                    val offset = (phase * size.width * 0.28f)
                    drawLine(
                        Color(0xFF8D3158).copy(alpha = 0.40f),
                        Offset(x - offset, size.height),
                        Offset(x + size.width * 0.13f - offset, 0f),
                        strokeWidth = (2 + i % 3).dp.toPx()
                    )
                }
            }
            else -> {
                val radius = (18 + phase * 24).dp.toPx()
                drawCircle(
                    Gold.copy(alpha = 0.55f * (1f - phase * 0.45f)),
                    radius,
                    Offset(size.width / 2f, size.height / 2f),
                    style = Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    Lavender.copy(alpha = 0.30f),
                    radius * 0.55f,
                    Offset(size.width / 2f, size.height / 2f),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }
    }
}

@Composable
private fun ResultBanner(result: String) {
    val headline = when (result) {
        "X" -> "YOU WIN  ✦"
        "O" -> "ROUND LOST"
        else -> "A PERFECT DRAW"
    }
    val sub = when (result) {
        "X" -> "The board belongs to you tonight."
        "O" -> "The AI found its way this time."
        else -> "Neither side gave an inch."
    }
    val tint = when (result) { "X" -> Gold; "O" -> Lavender; else -> Rose }
    val pulse by rememberInfiniteTransition(label = "resultPulse").animateFloat(
        0.97f, 1.025f, infiniteRepeatable(tween(780), RepeatMode.Reverse), label = "scale"
    )
    Row(
        Modifier.fillMaxWidth().scale(pulse).clip(RoundedCornerShape(15.dp))
            .background(Brush.horizontalGradient(listOf(tint.copy(.25f), PanelSoft)))
            .border(1.dp, tint.copy(.65f), RoundedCornerShape(15.dp)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.EmojiEvents, null, tint = tint, modifier = Modifier.size(27.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(headline, color = TextMain, fontSize = 15.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            Text(sub, color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun TicTacToeBoard(
    board: List<String>,
    winningCells: List<Int>,
    disabled: Boolean,
    onMove: (Int) -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        val boardSize = minOf(maxWidth, 360.dp)
        Box(
            Modifier.size(boardSize).clip(RoundedCornerShape(25.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF271627), Color(0xFF120D17))))
                .border(1.dp, Rose.copy(.5f), RoundedCornerShape(25.dp)).padding(9.dp)
        ) {
            Column(Modifier.fillMaxSize()) {
                for (row in 0..2) {
                    Row(Modifier.weight(1f)) {
                        for (col in 0..2) {
                            val index = row * 3 + col
                            val selected = index in winningCells
                            val pulse by animateFloatAsState(
                                targetValue = if (selected) 1f else 0f,
                                animationSpec = tween(500), label = "winCell$index"
                            )
                            Box(
                                Modifier.weight(1f).fillMaxHeight().padding(4.dp)
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(
                                        if (selected) Brush.linearGradient(listOf(Color(0xFF74344F), Color(0xFF3B203D)))
                                        else Brush.linearGradient(listOf(Color(0xFF211724), Color(0xFF19111E)))
                                    )
                                    .border(1.dp, Rose.copy(alpha = 0.18f + pulse * 0.7f), RoundedCornerShape(15.dp))
                                    .clickable(enabled = !disabled && board[index].isEmpty()) { onMove(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.animation.AnimatedContent(targetState = board[index], label = "symbol$index") { mark ->
                                    Text(
                                        mark,
                                        color = if (mark == "X") Rose else Lavender,
                                        fontSize = (boardSize.value / 6.3f).sp,
                                        fontWeight = FontWeight.Light
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (winningCells.isNotEmpty()) {
                WinningLineOverlay(winningCells)
            }
        }
    }
}

@Composable
private fun WinningLineOverlay(cells: List<Int>) {
    val alpha by rememberInfiniteTransition(label = "winningGlow").animateFloat(
        0.55f, 1f, infiniteRepeatable(tween(550), RepeatMode.Reverse), label = "lineAlpha"
    )
    Canvas(Modifier.fillMaxSize().padding(14.dp)) {
        val first = cells.first()
        val last = cells.last()
        val start = Offset(
            x = size.width * ((first % 3) * 2 + 1) / 6f,
            y = size.height * ((first / 3) * 2 + 1) / 6f
        )
        val end = Offset(
            x = size.width * ((last % 3) * 2 + 1) / 6f,
            y = size.height * ((last / 3) * 2 + 1) / 6f
        )
        drawLine(Gold.copy(alpha = alpha), start, end, strokeWidth = 7.dp.toPx(), cap = StrokeCap.Round)
        drawLine(Color.White.copy(alpha = alpha * .55f), start, end, strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
private fun SettingsPage(
    musicOn: Boolean,
    effectsOn: Boolean,
    alternateStyle: Boolean,
    onMusic: (Boolean) -> Unit,
    onEffects: (Boolean) -> Unit,
    onAlternateStyle: (Boolean) -> Unit
) {
    Column {
        Text("YOUR NIGHT, YOUR RULES", color = Gold, fontSize = 10.sp, letterSpacing = 2.sp)
        Text("Settings", color = TextMain, fontSize = 32.sp, fontWeight = FontWeight.Black)
        Text("Preferences are saved on this device.", color = TextMuted, fontSize = 11.sp)
        Spacer(Modifier.height(17.dp))
        SettingsCard {
            SettingsSwitch(Icons.Default.MusicNote, "Background music", "Original ambient loop", musicOn, onMusic)
            HorizontalDivider(color = Rose.copy(.17f))
            SettingsSwitch(Icons.Default.VolumeUp, "Sound effects", "Moves and match results", effectsOn, onEffects)
        }
        Spacer(Modifier.height(13.dp))
        SettingsCard {
            SettingsSwitch(Icons.Default.AutoAwesome, "Alternate styling", "Mature character palette • 21+", alternateStyle, onAlternateStyle)
        }
        Spacer(Modifier.height(13.dp))
        Text("The alternate style changes the colour treatment only. All characters are adults and the game remains non-explicit.", color = TextMuted, fontSize = 10.sp, lineHeight = 15.sp)
    }
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Panel)
            .border(1.dp, Rose.copy(.2f), RoundedCornerShape(20.dp)).padding(horizontal = 15.dp),
        content = content
    )
}

@Composable
private fun SettingsSwitch(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(Modifier.fillMaxWidth().padding(vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Rose, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = TextMain, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = TextMuted, fontSize = 10.sp)
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun ScorePage(xWins: Int, oWins: Int, draws: Int, onReset: () -> Unit) {
    Column {
        Text("MATCH HISTORY", color = Gold, fontSize = 10.sp, letterSpacing = 2.sp)
        Text("Hall of fame", color = TextMain, fontSize = 31.sp, fontWeight = FontWeight.Black)
        Text("Your scoreboard is stored locally.", color = TextMuted, fontSize = 11.sp)
        Spacer(Modifier.height(18.dp))
        ScoreLargeCard("PLAYER 1  •  X", xWins, Rose)
        ScoreLargeCard("PLAYER 2 / AI  •  O", oWins, Lavender)
        ScoreLargeCard("DRAWS", draws, Gold)
        Spacer(Modifier.height(13.dp))
        OutlinedButton(
            onClick = onReset, modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, Rose.copy(.55f)), shape = RoundedCornerShape(14.dp)
        ) { Text("RESET SCOREBOARD", color = Rose, letterSpacing = 1.sp) }
    }
}

@Composable
private fun ScoreLargeCard(label: String, value: Int, tint: Color) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp).clip(RoundedCornerShape(18.dp))
            .background(Panel).border(1.dp, tint.copy(.3f), RoundedCornerShape(18.dp)).padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(value.toString(), color = tint, fontSize = 36.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.width(16.dp))
        Text(label, color = TextMain, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}
