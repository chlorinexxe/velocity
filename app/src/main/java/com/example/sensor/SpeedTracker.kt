package com.example.sensor

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

class SpeedTracker(private val context: Context) {

    private val _currentSpeedMPS = MutableStateFlow(0f) // meters per second
    val currentSpeedMPS: StateFlow<Float> = _currentSpeedMPS.asStateFlow()

    private val _gpsAccuracy = MutableStateFlow(0f) // in meters
    val gpsAccuracy: StateFlow<Float> = _gpsAccuracy.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationManager: LocationManager? = null
    private var fallbackListener: LocationListener? = null

    // For manual speed estimation when hardware doesn't report location.getSpeed()
    private var lastLocation: Location? = null
    
    // Exponential Moving Average (EMA) smoothing factor (0.35 = extremely high responsiveness, live physical feedback)
    private var smoothedSpeed = 0f
    private val smoothingFactor = 0.35f

    init {
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        } catch (e: Exception) {
            Log.e("SpeedTracker", "Error initializing location providers", e)
        }
    }

    @SuppressLint("MissingPermission")
    fun startTracking() {
        if (_isTracking.value) return
        _isTracking.value = true
        lastLocation = null
        smoothedSpeed = 0f

        // 1. Priming Fused Location Client
        try {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 200)
                .setMinUpdateIntervalMillis(100)
                .setWaitForAccurateLocation(true)
                .build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation ?: return
                    processNewLocation(location)
                }
            }

            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
            Log.d("SpeedTracker", "Fused Location updates started.")
        } catch (e: Exception) {
            Log.e("SpeedTracker", "Failed starting Fused Location services, falling back.", e)
            startManualGpsFallback()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startManualGpsFallback() {
        try {
            fallbackListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    processNewLocation(location)
                }
                @Deprecated("Deprecated in API 29")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                500L,
                0f,
                fallbackListener!!,
                Looper.getMainLooper()
            )
            Log.d("SpeedTracker", "Fallback LocationManager GPS updates started.")
        } catch (e: Exception) {
            Log.e("SpeedTracker", "Failed starting traditional GPS updates", e)
        }
    }

    fun stopTracking() {
        if (!_isTracking.value) return
        _isTracking.value = false

        try {
            locationCallback?.let {
                fusedLocationClient?.removeLocationUpdates(it)
            }
            fallbackListener?.let {
                locationManager?.removeUpdates(it)
            }
        } catch (e: Exception) {
            Log.e("SpeedTracker", "Error stopping locations", e)
        } finally {
            _currentSpeedMPS.value = 0f
            _gpsAccuracy.value = 0f
            lastLocation = null
            smoothedSpeed = 0f
        }
    }

    private fun processNewLocation(location: Location) {
        _gpsAccuracy.value = location.accuracy

        var rawSpeed = 0f
        if (location.hasSpeed()) {
            rawSpeed = location.speed
        } else {
            // Calculate manual speed if location.hasSpeed() is unavailable
            lastLocation?.let { last ->
                val elapsedSeconds = (location.time - last.time) / 1000f
                if (elapsedSeconds > 0.1f) {
                    val distanceMeters = location.distanceTo(last)
                    val calculatedSpeed = distanceMeters / elapsedSeconds
                    // Extreme outlier removal (e.g. 150 m/s is supersonic flight or GPS glitch)
                    if (calculatedSpeed < 150f) {
                        rawSpeed = calculatedSpeed
                    }
                }
            }
        }

        // Apply Intelligent Spike Filtering / Speed Smoothing
        smoothedSpeed = if (smoothedSpeed == 0f && rawSpeed > 0f) {
            rawSpeed
        } else {
            smoothedSpeed + smoothingFactor * (rawSpeed - smoothedSpeed)
        }

        // Clamp very small speeds to zero to avoid continuous minor decimals when stationary
        val finalSpeed = if (smoothedSpeed < 0.25f) 0f else smoothedSpeed
        _currentSpeedMPS.value = finalSpeed
        lastLocation = location
    }
}
