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
     * Custom composition helper using API 30+ primitives with graceful legacy fallback.
     */
    private fun playComposition(primitives: List<Pair<Int, Float>>, fallback: () -> Unit) {
        if (vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val composition = VibrationEffect.startComposition()
                for (prim in primitives) {
                    val primitiveId = prim.first
                    val scale = prim.second.coerceIn(0f, 1f)
                    composition.addPrimitive(primitiveId, scale)
                }
                vibrator.vibrate(composition.compose())
            } else {
                fallback()
            }
        } catch (e: Exception) {
            Log.e("HapticEngine", "Composition vibration failed", e)
            fallback()
        }
    }

    /**
     * Premium Swipe transition vibration - ultra low-amplitude micro tick.
     */
    fun playPageSwipe() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            playComposition(
                listOf(VibrationEffect.Composition.PRIMITIVE_LOW_TICK to 0.45f),
                fallback = { tick() }
            )
        } else {
            tick()
        }
    }

    /**
     * Speed milestone change tick - tactile mechanical detent simulation.
     */
    fun playSpeedMilestoneTick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            playComposition(
                listOf(VibrationEffect.Composition.PRIMITIVE_TICK to 0.35f),
                fallback = { tick() }
            )
        } else {
            tick()
        }
    }

    /**
     * Specialized distinct tick for speed unit sliding.
     */
    fun playSpeedUnitSlide() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            playComposition(
                listOf(VibrationEffect.Composition.PRIMITIVE_LOW_TICK to 0.6f),
                fallback = { tick() }
            )
        } else {
            tick()
        }
    }

    /**
     * Specialized distinct tick for altitude unit sliding.
     */
    fun playAltitudeUnitSlide() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            playComposition(
                listOf(VibrationEffect.Composition.PRIMITIVE_TICK to 0.7f),
                fallback = { tick() }
            )
        } else {
            tick()
        }
    }

    /**
     * Specialized distinct tick for pressure unit sliding.
     */
    fun playPressureUnitSlide() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            playComposition(
                listOf(VibrationEffect.Composition.PRIMITIVE_CLICK to 0.4f),
                fallback = { click() }
            )
        } else {
            click()
        }
    }

    /**
     * Specialized tick for sliding and selecting speedometer styles.
     */
    fun playSpeedStyleSlide() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            playComposition(
                listOf(VibrationEffect.Composition.PRIMITIVE_LOW_TICK to 0.75f),
                fallback = { tick() }
            )
        } else {
            tick()
        }
    }

    /**
     * Specialized tick for sliding and selecting atmospheric styles.
     */
    fun playAtmosphereStyleSlide() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            playComposition(
                listOf(VibrationEffect.Composition.PRIMITIVE_TICK to 0.6f),
                fallback = { tick() }
            )
        } else {
            tick()
        }
    }

    /**
     * Selection of a new unit configuration - double micro detent.
     */
    fun playUnitSelection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            playComposition(
                listOf(
                    VibrationEffect.Composition.PRIMITIVE_LOW_TICK to 0.5f,
                    VibrationEffect.Composition.PRIMITIVE_TICK to 0.7f
                ),
                fallback = { doubleClick() }
            )
        } else {
            doubleClick()
        }
    }

    /**
     * Transition when opening a full drawer panel - swelling/rising haptic.
     */
    fun playPanelOpen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            playComposition(
                listOf(VibrationEffect.Composition.PRIMITIVE_QUICK_RISE to 0.65f),
                fallback = { doubleClick() }
            )
        } else {
            doubleClick()
        }
    }

    /**
     * Transition when closing a drawer panel - falling haptic.
     */
    fun playPanelClose() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            playComposition(
                listOf(VibrationEffect.Composition.PRIMITIVE_QUICK_FALL to 0.5f),
                fallback = { click() }
            )
        } else {
            click()
        }
    }

    /**
     * Theme shift transition - premium cascading double pulse.
     */
    fun playThemeChangeSuccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            playComposition(
                listOf(
                    VibrationEffect.Composition.PRIMITIVE_TICK to 0.4f,   // light micro tap
                    VibrationEffect.Composition.PRIMITIVE_CLICK to 0.6f   // gentle confirmation
                ),
                fallback = { click() }  // fallback to medium click for older devices
            )
        } else {
            click()  // keeps it soft on older versions
        }
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

