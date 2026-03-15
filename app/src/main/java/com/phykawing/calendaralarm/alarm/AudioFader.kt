package com.phykawing.calendaralarm.alarm

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.phykawing.calendaralarm.util.Constants

class AudioFader(
    private val mediaPlayer: MediaPlayer,
    private val durationMs: Long = Constants.VOLUME_RAMP_DURATION_MS
) {
    private val handler = Handler(Looper.getMainLooper())
    private val stepIntervalMs = 500L
    private val totalSteps = (durationMs / stepIntervalMs).toInt()
    private var currentStep = 0

    fun startFadeIn() {
        currentStep = 0
        mediaPlayer.setVolume(0f, 0f)
        handler.post(fadeRunnable)
    }

    fun stop() {
        handler.removeCallbacks(fadeRunnable)
    }

    private val fadeRunnable = object : Runnable {
        override fun run() {
            if (currentStep >= totalSteps) return

            currentStep++
            val volume = currentStep.toFloat() / totalSteps
            mediaPlayer.setVolume(volume, volume)

            if (currentStep < totalSteps) {
                handler.postDelayed(this, stepIntervalMs)
            }
        }
    }
}
