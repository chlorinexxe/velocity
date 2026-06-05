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

class CompassTracker(context: Context) : SensorEventListener {

    private val sensorManager: SensorManager? = 
        context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    private val rotationSensor: Sensor? = 
        sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private val accelSensor: Sensor? = 
        sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val magneticSensor: Sensor? = 
        sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val _headingDegrees = MutableStateFlow(0f)
    val headingDegrees: StateFlow<Float> = _headingDegrees.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private var smoothedHeading = 0f
    private val smoothingFactor = 0.2f

    private var lastAccelerometer = FloatArray(3)
    private var lastMagnetometer = FloatArray(3)
    private var isAccelerometerSet = false
    private var isMagnetometerSet = false

    fun startTracking() {
        if (_isTracking.value) return
        _isTracking.value = true
        isAccelerometerSet = false
        isMagnetometerSet = false

        if (rotationSensor != null) {
            sensorManager?.registerListener(
                this,
                rotationSensor,
                SensorManager.SENSOR_DELAY_UI
            )
        } else {
            accelSensor?.let {
                sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
            magneticSensor?.let {
                sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
        }
    }

    fun stopTracking() {
        if (!_isTracking.value) return
        _isTracking.value = false
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)
            val azimuthRad = orientation[0]
            var azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
            azimuthDeg = (azimuthDeg + 360f) % 360f
            updateHeading(azimuthDeg)
        } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)
            isAccelerometerSet = true
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.size)
            isMagnetometerSet = true
        }

        if (isAccelerometerSet && isMagnetometerSet) {
            val rMatrix = FloatArray(9)
            val iMatrix = FloatArray(9)
            if (SensorManager.getRotationMatrix(rMatrix, iMatrix, lastAccelerometer, lastMagnetometer)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rMatrix, orientation)
                val azimuthRad = orientation[0]
                var azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
                azimuthDeg = (azimuthDeg + 360f) % 360f
                updateHeading(azimuthDeg)
            }
        }
    }

    private fun updateHeading(newHeading: Float) {
        var diff = newHeading - smoothedHeading
        while (diff < -180f) diff += 360f
        while (diff > 180f) diff -= 360f

        smoothedHeading = (smoothedHeading + smoothingFactor * diff + 360f) % 360f
        _headingDegrees.value = smoothedHeading
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
