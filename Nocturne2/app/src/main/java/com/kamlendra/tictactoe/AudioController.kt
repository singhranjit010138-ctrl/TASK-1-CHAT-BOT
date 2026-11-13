package com.kamlendra.tictactoe

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator

/**
 * All audio is local. Music is paused when the app leaves the foreground.
 */
class AudioController(context: Context) {
    private val appContext = context.applicationContext
    private val tone = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
    private var player: MediaPlayer? = null

    fun playMove() = tone.startTone(ToneGenerator.TONE_PROP_BEEP, 75)
    fun playWin() = tone.startTone(ToneGenerator.TONE_PROP_ACK, 280)
    fun playLose() = tone.startTone(ToneGenerator.TONE_PROP_NACK, 260)
    fun playDraw() = tone.startTone(ToneGenerator.TONE_PROP_PROMPT, 240)

    fun startMusic() {
        if (player == null) {
            player = MediaPlayer.create(appContext, R.raw.nocturne_ambient)?.apply {
                isLooping = true
                setVolume(0.22f, 0.22f)
            }
        }
        player?.let { if (!it.isPlaying) it.start() }
    }

    fun pauseMusic() {
        player?.let { if (it.isPlaying) it.pause() }
    }

    fun release() {
        pauseMusic()
        player?.release()
        player = null
        tone.release()
    }
}
