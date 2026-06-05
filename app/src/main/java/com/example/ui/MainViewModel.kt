package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.haptic.HapticEngine
import com.example.sensor.AtmosphereTracker
import com.example.sensor.CompassTracker
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

enum class AltitudeSource {
    BAROMETER,
    GPS
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val speedTracker = SpeedTracker(application)
    private val atmosphereTracker = AtmosphereTracker(application)
    val compassTracker = CompassTracker(application)
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

    private val _headingDegrees = MutableStateFlow(0f)
    val headingDegrees: StateFlow<Float> = _headingDegrees.asStateFlow()

    val isTrackingSpeed: StateFlow<Boolean> = speedTracker.isTracking
    val isBarometerAvailable: StateFlow<Boolean> = atmosphereTracker.isBarometerAvailable

    // Selected Units
    private val _altitudeSource = MutableStateFlow(AltitudeSource.BAROMETER)
    val altitudeSource: StateFlow<AltitudeSource> = _altitudeSource.asStateFlow()

    private val _speedUnit = MutableStateFlow(SpeedUnit.KMH)
    val speedUnit: StateFlow<SpeedUnit> = _speedUnit.asStateFlow()

    private val _pressureUnit = MutableStateFlow(PressureUnit.HPA)
    val pressureUnit: StateFlow<PressureUnit> = _pressureUnit.asStateFlow()

    private val _altitudeUnit = MutableStateFlow(AltitudeUnit.METERS)
    val altitudeUnit: StateFlow<AltitudeUnit> = _altitudeUnit.asStateFlow()

    // Style Selections
    private val _activeSpeedometerIndex = MutableStateFlow(1) // 0..29 (30 styles)
    val activeSpeedometerIndex: StateFlow<Int> = _activeSpeedometerIndex.asStateFlow()

    private val _activeAtmosphereIndex = MutableStateFlow(0) // 0..29 (30 styles)
    val activeAtmosphereIndex: StateFlow<Int> = _activeAtmosphereIndex.asStateFlow()

    private val _activeCompassIndex = MutableStateFlow(0) // 0..29 (30 styles)
    val activeCompassIndex: StateFlow<Int> = _activeCompassIndex.asStateFlow()

    // Design Themes
    private val _currentTheme = MutableStateFlow(AppTheme.SOPHISTICATED_DARK)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    // Mode Overlays / Interactive states
    private val _isSpeedPreviewActive = MutableStateFlow(false)
    val isSpeedPreviewActive: StateFlow<Boolean> = _isSpeedPreviewActive.asStateFlow()

    private val _isAtmospherePreviewActive = MutableStateFlow(false)
    val isAtmospherePreviewActive: StateFlow<Boolean> = _isAtmospherePreviewActive.asStateFlow()

    private val _isCompassPreviewActive = MutableStateFlow(false)
    val isCompassPreviewActive: StateFlow<Boolean> = _isCompassPreviewActive.asStateFlow()

    private val _isThemeSelectionActive = MutableStateFlow(false)
    val isThemeSelectionActive: StateFlow<Boolean> = _isThemeSelectionActive.asStateFlow()

    init {
        // Collect real speedTracker & atmosphereTracker flows directly with no checks or simulation overrides
        viewModelScope.launch {
            speedTracker.currentSpeedMPS.collect { realSpeed ->
                _currentSpeedMPS.value = realSpeed
            }
        }
        viewModelScope.launch {
            speedTracker.gpsAccuracy.collect { accuracy ->
                _gpsAccuracy.value = accuracy
            }
        }
        viewModelScope.launch {
            atmosphereTracker.pressureHPa.collect { pressure ->
                _rawPressureHPa.value = pressure
            }
        }
        viewModelScope.launch {
            atmosphereTracker.estimatedAltitudeMeters.collect { altitude ->
                if (_altitudeSource.value == AltitudeSource.BAROMETER) {
                    _rawAltitudeMeters.value = altitude
                }
            }
        }
        viewModelScope.launch {
            speedTracker.gpsAltitudeMeters.collect { altitude ->
                if (_altitudeSource.value == AltitudeSource.GPS) {
                    _rawAltitudeMeters.value = altitude
                }
            }
        }
        viewModelScope.launch {
            atmosphereTracker.verticalSpeedMPS.collect { vs ->
                _verticalSpeedMPS.value = vs
            }
        }
        viewModelScope.launch {
            compassTracker.headingDegrees.collect { hd ->
                _headingDegrees.value = hd
            }
        }
    }

    fun startAllTracking() {
        speedTracker.startTracking()
        atmosphereTracker.startTracking()
        compassTracker.startTracking()
    }

    fun stopAllTracking() {
        speedTracker.stopTracking()
        atmosphereTracker.stopTracking()
        compassTracker.stopTracking()
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

    fun setAltitudeSource(source: AltitudeSource) {
        if (_altitudeSource.value != source) {
            _altitudeSource.value = source
            hapticEngine.playUnitSelection()
            _rawAltitudeMeters.value = if (source == AltitudeSource.BAROMETER) {
                atmosphereTracker.estimatedAltitudeMeters.value
            } else {
                speedTracker.gpsAltitudeMeters.value
            }
        }
    }

    fun setSpeedometerStyle(index: Int) {
        val bounded = index.coerceIn(0, 29)
        if (_activeSpeedometerIndex.value != bounded) {
            _activeSpeedometerIndex.value = bounded
            hapticEngine.heavyClick()
        }
    }

    fun previewSpeedometerStyle(index: Int) {
        val bounded = index.coerceIn(0, 29)
        if (_activeSpeedometerIndex.value != bounded) {
            _activeSpeedometerIndex.value = bounded
            hapticEngine.tick()
        }
    }

    fun setAtmosphereStyle(index: Int) {
        val bounded = index.coerceIn(0, 29)
        if (_activeAtmosphereIndex.value != bounded) {
            _activeAtmosphereIndex.value = bounded
            hapticEngine.heavyClick()
        }
    }

    fun previewAtmosphereStyle(index: Int) {
        val bounded = index.coerceIn(0, 29)
        if (_activeAtmosphereIndex.value != bounded) {
            _activeAtmosphereIndex.value = bounded
            hapticEngine.tick()
        }
    }

    fun setCompassStyle(index: Int) {
        val bounded = index.coerceIn(0, 29)
        if (_activeCompassIndex.value != bounded) {
            _activeCompassIndex.value = bounded
            hapticEngine.heavyClick()
        }
    }

    fun previewCompassStyle(index: Int) {
        val bounded = index.coerceIn(0, 29)
        if (_activeCompassIndex.value != bounded) {
            _activeCompassIndex.value = bounded
            hapticEngine.tick()
        }
    }

    fun setCompassPreviewActive(active: Boolean) {
        if (_isCompassPreviewActive.value != active) {
            _isCompassPreviewActive.value = active
            if (active) hapticEngine.doubleClick() else hapticEngine.click()
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
