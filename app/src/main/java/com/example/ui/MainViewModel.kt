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

    // Simulation Engine Controls
    private val _isSimulating = MutableStateFlow(false)
    val isSimulating: StateFlow<Boolean> = _isSimulating.asStateFlow()

    private val _currentSpeedMPS = MutableStateFlow(0f)
    val currentSpeedMPS: StateFlow<Float> = _currentSpeedMPS.asStateFlow()

    private val _gpsAccuracy = MutableStateFlow(0.8f)
    val gpsAccuracy: StateFlow<Float> = _gpsAccuracy.asStateFlow()

    private val _rawPressureHPa = MutableStateFlow(1013.25f)
    val rawPressureHPa: StateFlow<Float> = _rawPressureHPa.asStateFlow()

    private val _rawAltitudeMeters = MutableStateFlow(120f)
    val rawAltitudeMeters: StateFlow<Float> = _rawAltitudeMeters.asStateFlow()

    private val _verticalSpeedMPS = MutableStateFlow(0f)
    val verticalSpeedMPS: StateFlow<Float> = _verticalSpeedMPS.asStateFlow()

    val isTrackingSpeed: StateFlow<Boolean> = speedTracker.isTracking
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
        // Collect real speedTracker & atmosphereTracker flows
        viewModelScope.launch {
            speedTracker.currentSpeedMPS.collect { realSpeed ->
                if (!_isSimulating.value) {
                    _currentSpeedMPS.value = realSpeed
                }
            }
        }
        viewModelScope.launch {
            speedTracker.gpsAccuracy.collect { accuracy ->
                if (!_isSimulating.value) {
                    _gpsAccuracy.value = accuracy
                }
            }
        }
        viewModelScope.launch {
            atmosphereTracker.pressureHPa.collect { pressure ->
                if (!_isSimulating.value) {
                    _rawPressureHPa.value = pressure
                }
            }
        }
        viewModelScope.launch {
            atmosphereTracker.estimatedAltitudeMeters.collect { altitude ->
                if (!_isSimulating.value) {
                    _rawAltitudeMeters.value = altitude
                }
            }
        }
        viewModelScope.launch {
            atmosphereTracker.verticalSpeedMPS.collect { vs ->
                if (!_isSimulating.value) {
                    _verticalSpeedMPS.value = vs
                }
            }
        }

        // Start premium active simulation loop
        startSimulationLoop()

        // Automatically begin tracking environment sensors & location speeds
        startAllTracking()
    }

    private fun startSimulationLoop() {
        viewModelScope.launch {
            var time = 0.0
            while (true) {
                if (_isSimulating.value) {
                    time += 0.05
                    
                    // Simulate dynamic physical drive with acceleration, cruising fluctuations, and breaking deceleration halts
                    val baseSpeed = 16.0f + 14.0f * kotlin.math.sin(time * 0.04).toFloat()
                    val microfluctuation = 1.2f * kotlin.math.cos(time * 0.35).toFloat() * kotlin.math.sin(time * 0.12).toFloat()
                    
                    val stopCycle = kotlin.math.sin(time * 0.015).toFloat()
                    val speedScale = if (stopCycle < -0.85f) {
                        val factor = (stopCycle + 1.0f) / 0.15f // decelerate to standstill
                        factor.coerceIn(0f, 1f)
                    } else {
                        1f
                    }
                    
                    val targetSpeedVal = ((baseSpeed + microfluctuation) * speedScale).coerceAtLeast(0f)
                    _currentSpeedMPS.value = targetSpeedVal

                    // Simulate corresponding atmospheric metrics drift (altimeter climbs/descents)
                    val altitudeVal = 160f + 85f * kotlin.math.sin(time * 0.018).toFloat() + 4f * kotlin.math.cos(time * 0.11).toFloat()
                    _rawAltitudeMeters.value = altitudeVal

                    // Barometric pressure drops as altitude ascends (1 hPa per ~8.3m)
                    val pressureVal = 1013.25f - (altitudeVal / 8.3f)
                    _rawPressureHPa.value = pressureVal

                    // Rate of Climb/Descent matches the derivative of the altitude wave
                    val vsVal = 1.6f * kotlin.math.cos(time * 0.018).toFloat() - 0.5f * kotlin.math.sin(time * 0.11).toFloat()
                    _verticalSpeedMPS.value = if (kotlin.math.abs(vsVal) < 0.06f) 0f else vsVal
                }
                kotlinx.coroutines.delay(50) // 20 Hz updates for high-fidelity dials
            }
        }
    }

    fun toggleSimulation(active: Boolean) {
        if (_isSimulating.value != active) {
            _isSimulating.value = active
            hapticEngine.playUnitSelection()
            if (!active) {
                // Return to actual live hardware metrics immediately
                _currentSpeedMPS.value = speedTracker.currentSpeedMPS.value
                _gpsAccuracy.value = speedTracker.gpsAccuracy.value
                _rawPressureHPa.value = atmosphereTracker.pressureHPa.value
                _rawAltitudeMeters.value = atmosphereTracker.estimatedAltitudeMeters.value
                _verticalSpeedMPS.value = atmosphereTracker.verticalSpeedMPS.value
            } else {
                _gpsAccuracy.value = 0.8f
            }
        }
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
            hapticEngine.playUnitSelection()
        }
    }

    fun selectPressureUnit(unit: PressureUnit) {
        if (_pressureUnit.value != unit) {
            _pressureUnit.value = unit
            hapticEngine.playUnitSelection()
        }
    }

    fun selectAltitudeUnit(unit: AltitudeUnit) {
        if (_altitudeUnit.value != unit) {
            _altitudeUnit.value = unit
            hapticEngine.playUnitSelection()
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
            if (active) hapticEngine.playPanelOpen() else hapticEngine.playPanelClose()
        }
    }

    fun selectTheme(theme: AppTheme) {
        if (_currentTheme.value != theme) {
            _currentTheme.value = theme
            hapticEngine.playThemeChangeSuccess()
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
