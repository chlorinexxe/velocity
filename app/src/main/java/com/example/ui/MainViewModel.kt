package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.haptic.HapticEngine
import com.example.sensor.AtmosphereTracker
import com.example.sensor.SpeedTracker
import com.example.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SpeedUnit(val label: String) {
    KMH("km/h"),
    MPH("mph"),
    KNOTS("knots"),
    MS("m/s")
}

enum class PressureUnit(val label: String) {
    HPA("hPa"),
    MBAR("mbar"),
    INHG("inHg")
}

enum class AltitudeUnit(val label: String) {
    METERS("meters"),
    FEET("feet")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val speedTracker = SpeedTracker(application)
    private val atmosphereTracker = AtmosphereTracker(application)
    val hapticEngine = HapticEngine(application)

    // Telemetry flows from hardware
    val currentSpeedMPS: StateFlow<Float> = speedTracker.currentSpeedMPS
    val gpsAccuracy: StateFlow<Float> = speedTracker.gpsAccuracy
    val isTrackingSpeed: StateFlow<Boolean> = speedTracker.isTracking

    val rawPressureHPa: StateFlow<Float> = atmosphereTracker.pressureHPa
    val rawAltitudeMeters: StateFlow<Float> = atmosphereTracker.estimatedAltitudeMeters
    val verticalSpeedMPS: StateFlow<Float> = atmosphereTracker.verticalSpeedMPS
    val isBarometerAvailable: StateFlow<Boolean> = atmosphereTracker.isBarometerAvailable

    // Selected Units
    private val _speedUnit = MutableStateFlow(SpeedUnit.KMH)
    val speedUnit: StateFlow<SpeedUnit> = _speedUnit.asStateFlow()

    private val _pressureUnit = MutableStateFlow(PressureUnit.HPA)
    val pressureUnit: StateFlow<PressureUnit> = _pressureUnit.asStateFlow()

    private val _altitudeUnit = MutableStateFlow(AltitudeUnit.METERS)
    val altitudeUnit: StateFlow<AltitudeUnit> = _altitudeUnit.asStateFlow()

    // Style Selections
    private val _activeSpeedometerIndex = MutableStateFlow(1) // 0..19 (20 styles)
    val activeSpeedometerIndex: StateFlow<Int> = _activeSpeedometerIndex.asStateFlow()

    private val _activeAtmosphereIndex = MutableStateFlow(0) // 0..9 (10 styles)
    val activeAtmosphereIndex: StateFlow<Int> = _activeAtmosphereIndex.asStateFlow()

    // Design Themes
    private val _currentTheme = MutableStateFlow(AppTheme.SOPHISTICATED_DARK)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    // Mode Overlays / Interactive states
    private val _isSpeedPreviewActive = MutableStateFlow(false)
    val isSpeedPreviewActive: StateFlow<Boolean> = _isSpeedPreviewActive.asStateFlow()

    private val _isAtmospherePreviewActive = MutableStateFlow(false)
    val isAtmospherePreviewActive: StateFlow<Boolean> = _isAtmospherePreviewActive.asStateFlow()

    private val _isThemeSelectionActive = MutableStateFlow(false)
    val isThemeSelectionActive: StateFlow<Boolean> = _isThemeSelectionActive.asStateFlow()

    init {
        // Automatically begin tracking environment sensors & location speeds
        startAllTracking()
    }

    fun startAllTracking() {
        speedTracker.startTracking()
        atmosphereTracker.startTracking()
    }

    fun stopAllTracking() {
        speedTracker.stopTracking()
        atmosphereTracker.stopTracking()
    }

    // Interactive selections
    fun selectSpeedUnit(unit: SpeedUnit) {
        if (_speedUnit.value != unit) {
            _speedUnit.value = unit
            hapticEngine.click()
        }
    }

    fun selectPressureUnit(unit: PressureUnit) {
        if (_pressureUnit.value != unit) {
            _pressureUnit.value = unit
            hapticEngine.click()
        }
    }

    fun selectAltitudeUnit(unit: AltitudeUnit) {
        if (_altitudeUnit.value != unit) {
            _altitudeUnit.value = unit
            hapticEngine.click()
        }
    }

    fun setSpeedometerStyle(index: Int) {
        val bounded = index.coerceIn(0, 19)
        if (_activeSpeedometerIndex.value != bounded) {
            _activeSpeedometerIndex.value = bounded
            hapticEngine.heavyClick()
        }
    }

    fun previewSpeedometerStyle(index: Int) {
        val bounded = index.coerceIn(0, 19)
        if (_activeSpeedometerIndex.value != bounded) {
            _activeSpeedometerIndex.value = bounded
            hapticEngine.tick()
        }
    }

    fun setAtmosphereStyle(index: Int) {
        val bounded = index.coerceIn(0, 9)
        if (_activeAtmosphereIndex.value != bounded) {
            _activeAtmosphereIndex.value = bounded
            hapticEngine.heavyClick()
        }
    }

    fun previewAtmosphereStyle(index: Int) {
        val bounded = index.coerceIn(0, 9)
        if (_activeAtmosphereIndex.value != bounded) {
            _activeAtmosphereIndex.value = bounded
            hapticEngine.tick()
        }
    }

    fun toggleThemeSelection(active: Boolean) {
        if (_isThemeSelectionActive.value != active) {
            _isThemeSelectionActive.value = active
            hapticEngine.doubleClick()
        }
    }

    fun selectTheme(theme: AppTheme) {
        if (_currentTheme.value != theme) {
            _currentTheme.value = theme
            hapticEngine.heavyClick()
        }
    }

    fun setSpeedPreviewActive(active: Boolean) {
        if (_isSpeedPreviewActive.value != active) {
            _isSpeedPreviewActive.value = active
            if (active) hapticEngine.doubleClick() else hapticEngine.click()
        }
    }

    fun setAtmospherePreviewActive(active: Boolean) {
        if (_isAtmospherePreviewActive.value != active) {
            _isAtmospherePreviewActive.value = active
            if (active) hapticEngine.doubleClick() else hapticEngine.click()
        }
    }

    // Unit Conversion Calculations
    fun getFormattedSpeed(mps: Float): Float {
        return when (_speedUnit.value) {
            SpeedUnit.KMH -> mps * 3.6f
            SpeedUnit.MPH -> mps * 2.23694f
            SpeedUnit.KNOTS -> mps * 1.94384f
            SpeedUnit.MS -> mps
        }
    }

    fun getFormattedPressure(hpa: Float): Float {
        return when (_pressureUnit.value) {
            PressureUnit.HPA -> hpa
            PressureUnit.MBAR -> hpa // 1:1 ratio
            PressureUnit.INHG -> hpa * 0.02953f
        }
    }

    fun getFormattedAltitude(meters: Float): Float {
        return when (_altitudeUnit.value) {
            AltitudeUnit.METERS -> meters
            AltitudeUnit.FEET -> meters * 3.28084f
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAllTracking()
    }
}
