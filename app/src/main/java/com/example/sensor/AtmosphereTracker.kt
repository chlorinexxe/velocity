package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.pow

class AtmosphereTracker(context: Context) : SensorEventListener {

    private val sensorManager: SensorManager? = 
        context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    private val barometerSensor: Sensor? = 
        sensorManager?.getDefaultSensor(Sensor.TYPE_PRESSURE)

    private val _pressureHPa = MutableStateFlow(1013.25f) // standard baseline
    val pressureHPa: StateFlow<Float> = _pressureHPa.asStateFlow()

    private val _estimatedAltitudeMeters = MutableStateFlow(0f)
    val estimatedAltitudeMeters: StateFlow<Float> = _estimatedAltitudeMeters.asStateFlow()

    private val _verticalSpeedMPS = MutableStateFlow(0f) // rate of climb/descent
    val verticalSpeedMPS: StateFlow<Float> = _verticalSpeedMPS.asStateFlow()

    private val _isBarometerAvailable = MutableStateFlow(barometerSensor != null)
    val isBarometerAvailable: StateFlow<Boolean> = _isBarometerAvailable.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private var lastAltitude = 0f
    private var lastAltitudeTime = 0L

    // Filters to reduce sensory noise and jitter
    private var smoothedPressure = 1013.25f
    private var smoothedVerticalSpeed = 0f
    private val pressureSmoothing = 0.28f // Increased frequency and reduced smoothing delay for instantaneous feedback
    private val verticalSpeedSmoothing = 0.15f

    fun startTracking() {
        if (_isTracking.value || barometerSensor == null) return
        _isTracking.value = true
        lastAltitudeTime = System.currentTimeMillis()
        
        sensorManager?.registerListener(
            this,
            barometerSensor,
            SensorManager.SENSOR_DELAY_FASTEST // Absolute highest frequency for real-time responsiveness
        )
    }

    fun stopTracking() {
        if (!_isTracking.value) return
        _isTracking.value = false
        sensorManager?.unregisterListener(this)
        _verticalSpeedMPS.value = 0f
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_PRESSURE) return

        val rawPressure = event.values[0]
        if (rawPressure <= 0f) return

        // 1. Smooth the barometric pressure reading
        smoothedPressure = if (smoothedPressure == 1013.25f) {
            rawPressure
        } else {
            smoothedPressure + pressureSmoothing * (rawPressure - smoothedPressure)
        }
        _pressureHPa.value = smoothedPressure

        // 2. Estimate Altitude based on ISA (International Standard Atmosphere) formula
        val altitude = calculateAltitude(smoothedPressure)
        _estimatedAltitudeMeters.value = altitude

        // 3. Calculate Vertical Speed (Rate of Climb / Descent)
        val currentTime = System.currentTimeMillis()
        val timeDiffSeconds = (currentTime - lastAltitudeTime) / 1000f

        if (timeDiffSeconds >= 0.25f) { // calculate rate 4 times a second
            val altitudeDiff = altitude - lastAltitude
            val instantVerticalSpeed = altitudeDiff / timeDiffSeconds

            // Filter the vertical speed reading to avoid dramatic arrow switching
            smoothedVerticalSpeed = smoothedVerticalSpeed + verticalSpeedSmoothing * (instantVerticalSpeed - smoothedVerticalSpeed)
            
            // Apply a noise threshold (ignoring speeds less than 0.05 m/s)
            val finalVerticalSpeed = if (kotlin.math.abs(smoothedVerticalSpeed) < 0.05f) 0f else smoothedVerticalSpeed
            _verticalSpeedMPS.value = finalVerticalSpeed

            lastAltitude = altitude
            lastAltitudeTime = currentTime
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    /**
     * Calculates altitude from standard pressure using the barometric formula.
     * h = 44330 * (1.0 - (p / 1013.25)^(1.0 / 5.255))
     */
    private fun calculateAltitude(pressure: Float): Float {
        val standardPressure = 1013.25f
        val exponent = 1.0f / 5.255f
        val altitudeFraction = 1.0f - (pressure / standardPressure).pow(exponent)
        return 44330.0f * altitudeFraction
    }
}
