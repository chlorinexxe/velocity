package com.example.haptic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

class HapticEngine(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    /**
     * Light tick - used for ticking values, slider increments, or page updates.
     */
    fun tick() {
        vibrate(millis = 10, amplitude = 50, effectId = VibrationEffect.EFFECT_TICK)
    }

    /**
     * Standard crisp click - used for toggling units or opening minor states.
     */
    fun click() {
        vibrate(millis = 20, amplitude = 120, effectId = VibrationEffect.EFFECT_CLICK)
    }

    /**
     * Dynamic double pulse - used for transitions, mode entries or selections.
     */
    fun doubleClick() {
        vibrate(millis = 35, amplitude = 180, effectId = VibrationEffect.EFFECT_DOUBLE_CLICK)
    }

    /**
     * Heavy thump - used for long-press trigger activations or state confirmations.
     */
    fun heavyClick() {
        vibrate(millis = 40, amplitude = 220, effectId = VibrationEffect.EFFECT_HEAVY_CLICK)
    }

    /**
     * Tactile pulse - custom wave pulse
     */
    fun slidePulse() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 12, 40, 12)
            val amplitudes = intArrayOf(0, 70, 0, 70)
            try {
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } catch (e: Exception) {
                tick()
            }
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(15)
        }
    }

    private fun vibrate(millis: Long, amplitude: Int, effectId: Int) {
        if (vibrator == null || !vibrator.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use pre-defined native platform vibration effects for ultimate precision
                vibrator.vibrate(VibrationEffect.createPredefined(effectId))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Fallback to millisecond precise amplitude on Oreo and Pie
                val clampedAmplitude = amplitude.coerceIn(1, 255)
                vibrator.vibrate(VibrationEffect.createOneShot(millis, clampedAmplitude))
            } else {
                // Legacy system fallback
                @Suppress("DEPRECATION")
                vibrator.vibrate(millis)
            }
        } catch (e: Exception) {
            Log.e("HapticEngine", "Vibration failed", e)
        }
    }
}
