package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.theme.AppTheme
import com.example.theme.ThemeColors
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Check & request location permission
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasLocationPermission = granted
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            viewModel.startAllTracking()
        } else {
            viewModel.stopAllTracking()
        }
    }

    // Connect state variables
    val currentTheme by viewModel.currentTheme.collectAsState()
    val colorScheme = ThemeColors.getColorScheme(currentTheme)
    
    val currentSpeedMPS by viewModel.currentSpeedMPS.collectAsState()
    val currentPressureHPa by viewModel.rawPressureHPa.collectAsState()
    val currentAltitudeMeters by viewModel.rawAltitudeMeters.collectAsState()
    val verticalSpeedMPS by viewModel.verticalSpeedMPS.collectAsState()
    val isBarometerAvailable by viewModel.isBarometerAvailable.collectAsState()

    val speedUnit by viewModel.speedUnit.collectAsState()
    val pressureUnit by viewModel.pressureUnit.collectAsState()
    val altitudeUnit by viewModel.altitudeUnit.collectAsState()
    val altitudeSource by viewModel.altitudeSource.collectAsState()

    val activeSpeedometerIndex by viewModel.activeSpeedometerIndex.collectAsState()
    val activeAtmosphereIndex by viewModel.activeAtmosphereIndex.collectAsState()
    val activeCompassIndex by viewModel.activeCompassIndex.collectAsState()

    val currentSpeedometerIndexState by rememberUpdatedState(activeSpeedometerIndex)
    val currentAtmosphereIndexState by rememberUpdatedState(activeAtmosphereIndex)
    val currentCompassIndexState by rememberUpdatedState(activeCompassIndex)
    val currentSpeedUnitState by rememberUpdatedState(speedUnit)
    val currentPressureUnitState by rememberUpdatedState(pressureUnit)
    val currentAltitudeUnitState by rememberUpdatedState(altitudeUnit)

    val isSpeedPreviewActive by viewModel.isSpeedPreviewActive.collectAsState()
    val isAtmospherePreviewActive by viewModel.isAtmospherePreviewActive.collectAsState()
    val isCompassPreviewActive by viewModel.isCompassPreviewActive.collectAsState()
    val isThemeSelectionActive by viewModel.isThemeSelectionActive.collectAsState()

    val headingDegrees by viewModel.headingDegrees.collectAsState()

    // Smooth springing values for gorgeous kinetic dials
    val animatedSpeed by animateFloatAsState(
        targetValue = viewModel.getFormattedSpeed(currentSpeedMPS),
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = 110f
        ),
        label = "animatedSpeed"
    )

    val animatedPressure by animateFloatAsState(
        targetValue = viewModel.getFormattedPressure(currentPressureHPa),
        animationSpec = spring(
            dampingRatio = 0.9f,
            stiffness = 80f
        ),
        label = "animatedPressure"
    )

    val animatedAltitude by animateFloatAsState(
        targetValue = viewModel.getFormattedAltitude(currentAltitudeMeters),
        animationSpec = spring(
            dampingRatio = 0.85f,
            stiffness = 90f
        ),
        label = "animatedAltitude"
    )

    // Master Theme Provider scoping Material 3 colors
    MaterialTheme(colorScheme = colorScheme) {
        val uiAccent = MaterialTheme.colorScheme.secondary
        val uiText = MaterialTheme.colorScheme.primary
        val uiBg = MaterialTheme.colorScheme.background

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(uiBg)
                .drawBehind {
                    if (currentTheme == AppTheme.SOPHISTICATED_DARK) {
                        val glow = Brush.radialGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f)),
                            center = center,
                            radius = size.maxDimension
                        )
                        drawRect(brush = glow)
                    }
                }
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            if (!hasLocationPermission) {
                // High fidelity aesthetic permission greeting page
                PermissionFallbackScreen(onGrantRequested = {
                    launcher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                })
            } else {
                val pagerState = rememberPagerState(initialPage = 5000, pageCount = { 10000 })
                val isSimulating by viewModel.isSimulating.collectAsState()

                // Premium haptics: PageSwipe transition feedback
                var hasPageInitiallyLoaded by remember { mutableStateOf(false) }
                LaunchedEffect(pagerState.currentPage) {
                    if (hasPageInitiallyLoaded) {
                        viewModel.hapticEngine.playPageSwipe()
                    } else {
                        hasPageInitiallyLoaded = true
                    }
                }

                // Premium haptics: Tactile mechanical detent ticks for speed milestones (dynamic physical feedback)
                var lastTickedSpeed by remember { mutableIntStateOf(0) }
                val roundedSpeed = animatedSpeed.roundToInt()
                LaunchedEffect(roundedSpeed, pagerState.currentPage) {
                    val pageIndex = Math.floorMod(pagerState.currentPage, 4)
                    if (pageIndex == 0 && currentSpeedMPS > 0.15f && roundedSpeed != lastTickedSpeed) {
                        viewModel.hapticEngine.playSpeedMilestoneTick()
                        lastTickedSpeed = roundedSpeed
                    }
                }

                // Premium haptics: Altimeter physical ticks for altitude milestones
                var lastTickedAltitude by remember { mutableIntStateOf(0) }
                val roundedAltitude = animatedAltitude.roundToInt()
                LaunchedEffect(roundedAltitude, pagerState.currentPage) {
                    val pageIndex = Math.floorMod(pagerState.currentPage, 4)
                    if (pageIndex == 1 && isBarometerAvailable && roundedAltitude != lastTickedAltitude) {
                        viewModel.hapticEngine.playSpeedMilestoneTick()
                        lastTickedAltitude = roundedAltitude
                    }
                }

                val configuration = LocalConfiguration.current
                val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

                if (isLandscape) {
                    val pageIndex = Math.floorMod(pagerState.currentPage, 4)
                    // --------------------------------------------------
                    // LANDSCAPE COCKPIT DASHBOARD LAYOUT
                    // --------------------------------------------------
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // LEFT VIEWPORT: Beautiful expansive Speed or Atmosphere dial
                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                val pageIndex = Math.floorMod(page, 4)
                                if (pageIndex == 0) {
                                    // Speed page dial
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .pointerInput(Unit) {
                                                var dragAccumulator = 0f
                                                var currentTempIndex = activeSpeedometerIndex

                                                detectDragGesturesAfterLongPress(
                                                    onDragStart = {
                                                        viewModel.setSpeedPreviewActive(true)
                                                        dragAccumulator = 0f
                                                        currentTempIndex = currentSpeedometerIndexState
                                                        viewModel.hapticEngine.heavyClick()
                                                    },
                                                    onDragEnd = {
                                                        viewModel.setSpeedPreviewActive(false)
                                                    },
                                                    onDragCancel = {
                                                        viewModel.setSpeedPreviewActive(false)
                                                    },
                                                    onDrag = { _, dragAmount ->
                                                        dragAccumulator += dragAmount.x
                                                        val threshold = 70f
                                                        if (dragAccumulator > threshold) {
                                                            currentTempIndex = (currentTempIndex - 1 + 30) % 30
                                                            viewModel.previewSpeedometerStyle(currentTempIndex)
                                                            viewModel.hapticEngine.playSpeedStyleSlide()
                                                            dragAccumulator = 0f
                                                        } else if (dragAccumulator < -threshold) {
                                                            currentTempIndex = (currentTempIndex + 1) % 30
                                                            viewModel.previewSpeedometerStyle(currentTempIndex)
                                                            viewModel.hapticEngine.playSpeedStyleSlide()
                                                            dragAccumulator = 0f
                                                        }
                                                    }
                                                )
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        SpeedometerDisplay(
                                            animatedSpeed = animatedSpeed,
                                            unitLabel = speedUnit.label,
                                            styleIndex = activeSpeedometerIndex,
                                            accentColor = uiAccent,
                                            textColor = uiText
                                        )

                                        // Style Selection overlay banner in landscape
                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = isSpeedPreviewActive,
                                            enter = fadeIn(animationSpec = tween(150)) + scaleIn(initialScale = 0.9f),
                                            exit = fadeOut(animationSpec = tween(150)) + scaleOut()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 10.dp)
                                                    .background(uiAccent.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                                                    .align(Alignment.TopCenter)
                                            ) {
                                                Text(
                                                    text = "DESIGN: ${activeSpeedometerIndex + 1} / 30",
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                    letterSpacing = 1.sp
                                                )
                                            }
                                        }
                                    }
                                } else if (pageIndex == 1) {
                                    // Atmosphere page dial
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .pointerInput(Unit) {
                                                var dragAccumulator = 0f
                                                var currentTempIndex = activeAtmosphereIndex

                                                detectDragGesturesAfterLongPress(
                                                    onDragStart = {
                                                        viewModel.setAtmospherePreviewActive(true)
                                                        dragAccumulator = 0f
                                                        currentTempIndex = currentAtmosphereIndexState
                                                        viewModel.hapticEngine.heavyClick()
                                                    },
                                                    onDragEnd = {
                                                        viewModel.setAtmospherePreviewActive(false)
                                                    },
                                                    onDragCancel = {
                                                        viewModel.setAtmospherePreviewActive(false)
                                                    },
                                                    onDrag = { _, dragAmount ->
                                                        dragAccumulator += dragAmount.x
                                                        val threshold = 70f
                                                        if (dragAccumulator > threshold) {
                                                            currentTempIndex = (currentTempIndex - 1 + 30) % 30
                                                            viewModel.previewAtmosphereStyle(currentTempIndex)
                                                            viewModel.hapticEngine.playAtmosphereStyleSlide()
                                                            dragAccumulator = 0f
                                                        } else if (dragAccumulator < -threshold) {
                                                            currentTempIndex = (currentTempIndex + 1) % 30
                                                            viewModel.previewAtmosphereStyle(currentTempIndex)
                                                            viewModel.hapticEngine.playAtmosphereStyleSlide()
                                                            dragAccumulator = 0f
                                                        }
                                                    }
                                                )
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!isBarometerAvailable) {
                                            Text(
                                                text = "Barometer Unavailable",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Light,
                                                color = uiText.copy(alpha = 0.5f)
                                            )
                                        } else {
                                            AtmosphereDisplay(
                                                animatedAltitude = animatedAltitude,
                                                altitudeUnit = altitudeUnit,
                                                animatedPressure = animatedPressure,
                                                pressureUnit = pressureUnit,
                                                verticalSpeed = verticalSpeedMPS,
                                                styleIndex = activeAtmosphereIndex,
                                                accentColor = uiAccent,
                                                textColor = uiText
                                            )
                                        }

                                        // Style selection overlay banner in landscape
                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = isAtmospherePreviewActive,
                                            enter = fadeIn(animationSpec = tween(150)) + scaleIn(initialScale = 0.9f),
                                            exit = fadeOut(animationSpec = tween(150)) + scaleOut()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 10.dp)
                                                    .background(uiAccent.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                                                    .align(Alignment.TopCenter)
                                            ) {
                                                Text(
                                                    text = "STYLE: ${activeAtmosphereIndex + 1} / 30",
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                    letterSpacing = 1.sp
                                                )
                                            }
                                        }
                                    }
                                } else if (pageIndex == 2) {
                                    // Compass page dial
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .pointerInput(Unit) {
                                                var dragAccumulator = 0f
                                                var currentTempIndex = activeCompassIndex

                                                detectDragGesturesAfterLongPress(
                                                    onDragStart = {
                                                        viewModel.setCompassPreviewActive(true)
                                                        dragAccumulator = 0f
                                                        currentTempIndex = currentCompassIndexState
                                                        viewModel.hapticEngine.heavyClick()
                                                    },
                                                    onDragEnd = {
                                                        viewModel.setCompassPreviewActive(false)
                                                    },
                                                    onDragCancel = {
                                                        viewModel.setCompassPreviewActive(false)
                                                    },
                                                    onDrag = { _, dragAmount ->
                                                        dragAccumulator += dragAmount.x
                                                        val threshold = 70f
                                                        if (dragAccumulator > threshold) {
                                                            currentTempIndex = (currentTempIndex - 1 + 30) % 30
                                                            viewModel.previewCompassStyle(currentTempIndex)
                                                            viewModel.hapticEngine.playAtmosphereStyleSlide()
                                                            dragAccumulator = 0f
                                                        } else if (dragAccumulator < -threshold) {
                                                            currentTempIndex = (currentTempIndex + 1) % 30
                                                            viewModel.previewCompassStyle(currentTempIndex)
                                                            viewModel.hapticEngine.playAtmosphereStyleSlide()
                                                            dragAccumulator = 0f
                                                        }
                                                    }
                                                )
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CompassDisplay(
                                            heading = headingDegrees,
                                            styleIndex = activeCompassIndex,
                                            accentColor = uiAccent,
                                            textColor = uiText
                                        )

                                        // Style selection overlay banner in landscape
                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = isCompassPreviewActive,
                                            enter = fadeIn(animationSpec = tween(150)) + scaleIn(initialScale = 0.9f),
                                            exit = fadeOut(animationSpec = tween(150)) + scaleOut()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 10.dp)
                                                    .background(uiAccent.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                                                    .align(Alignment.TopCenter)
                                            ) {
                                                Text(
                                                    text = "COMPASS: ${activeCompassIndex + 1} / 30",
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                    letterSpacing = 1.sp
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    // 4th Page: Speedometer fully utilized in landscape!
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        SpeedometerDisplay(
                                            animatedSpeed = animatedSpeed,
                                            unitLabel = speedUnit.label,
                                            styleIndex = activeSpeedometerIndex,
                                            accentColor = uiAccent,
                                            textColor = uiText
                                        )
                                    }
                                }
                            }
                        }

                        // RIGHT VIEWPORT: Command Cockpit Controls
                        Column(
                            modifier = Modifier
                                .weight(0.7f)
                                .fillMaxHeight()
                                .padding(vertical = 12.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = if (pageIndex == 3) Arrangement.Center else Arrangement.SpaceBetween
                        ) {
                            // 1. Double GPS Telemetry & Altitude Source Option (Barometer vs GPS)
                            if (pageIndex != 3) {
                                val altSource = altitudeSource
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(uiText.copy(alpha = 0.04f))
                                        .border(
                                            0.5.dp,
                                            uiText.copy(alpha = 0.12f),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(Color(0xFFFFCC00), CircleShape)
                                    )
                                    Text(
                                        text = "DIRECT GPS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = uiText.copy(alpha = 0.7f),
                                        letterSpacing = 1.sp
                                    )
                                }

                                // Interactive Altitude Source Option Toggle
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (altSource == AltitudeSource.GPS) uiAccent.copy(alpha = 0.12f) else uiText.copy(alpha = 0.04f))
                                        .border(
                                            0.5.dp,
                                            if (altSource == AltitudeSource.GPS) uiAccent else uiText.copy(alpha = 0.12f),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            val nextSource = if (altSource == AltitudeSource.BAROMETER) AltitudeSource.GPS else AltitudeSource.BAROMETER
                                            viewModel.setAltitudeSource(nextSource)
                                            viewModel.hapticEngine.click()
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Canvas(modifier = Modifier.size(6.dp)) {
                                        drawCircle(color = if (altSource == AltitudeSource.GPS) uiAccent else uiText.copy(alpha = 0.5f))
                                    }
                                    Text(
                                        text = if (altSource == AltitudeSource.GPS) "GPS ALT" else "BARO ALT",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (altSource == AltitudeSource.GPS) uiAccent else uiText.copy(alpha = 0.7f),
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                            }

                            // 2. Metric system indicator / selector badges
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                if (pageIndex != 3) {
                                    if (pageIndex == 0) {
                                        var isSpeedLandSliding by remember { mutableStateOf(false) }
                                        var speedLandDragAccumulator by remember { mutableStateOf(0f) }

                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(32.dp))
                                            .border(0.5.dp, if (isSpeedLandSliding) uiAccent else uiText.copy(alpha = 0.08f), RoundedCornerShape(32.dp))
                                            .testTag("speed_unit_toggle_landscape")
                                            .pointerInput(Unit) {
                                                detectDragGesturesAfterLongPress(
                                                    onDragStart = {
                                                        isSpeedLandSliding = true
                                                        speedLandDragAccumulator = 0f
                                                        viewModel.hapticEngine.doubleClick()
                                                    },
                                                    onDragEnd = { isSpeedLandSliding = false },
                                                    onDragCancel = { isSpeedLandSliding = false },
                                                    onDrag = { _, dragAmount ->
                                                        speedLandDragAccumulator += dragAmount.x
                                                        val threshold = 70f
                                                        if (speedLandDragAccumulator > threshold) {
                                                            val values = SpeedUnit.values()
                                                            val nextIdx = (currentSpeedUnitState.ordinal + 1) % values.size
                                                            viewModel.selectSpeedUnit(values[nextIdx])
                                                            viewModel.hapticEngine.playSpeedMilestoneTick()
                                                            speedLandDragAccumulator = 0f
                                                        } else if (speedLandDragAccumulator < -threshold) {
                                                            val values = SpeedUnit.values()
                                                            val prevIdx = (currentSpeedUnitState.ordinal - 1 + values.size) % values.size
                                                            viewModel.selectSpeedUnit(values[prevIdx])
                                                            viewModel.hapticEngine.playSpeedMilestoneTick()
                                                            speedLandDragAccumulator = 0f
                                                        }
                                                    }
                                                )
                                            }
                                            .clickable {
                                                val nextUnitOrdinal = (speedUnit.ordinal + 1) % SpeedUnit.values().size
                                                viewModel.selectSpeedUnit(SpeedUnit.values()[nextUnitOrdinal])
                                                viewModel.hapticEngine.click()
                                            },
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = if (isSpeedLandSliding) 0.45f else 0.25f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (isSpeedLandSliding) "UNIT: ${speedUnit.name}" else speedUnit.name,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = uiAccent,
                                                letterSpacing = 1.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isSpeedLandSliding) "◀ SLIDE ▶" else "• HOLD & SLIDE",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (isSpeedLandSliding) uiAccent else uiText.copy(alpha = 0.25f)
                                            )
                                        }
                                    }
                                } else if (pageIndex == 1 || pageIndex == 3) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        var isAltitudeLandSliding by remember { mutableStateOf(false) }
                                        var altitudeLandDragAccumulator by remember { mutableStateOf(0f) }

                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(32.dp))
                                                .border(0.5.dp, if (isAltitudeLandSliding) uiAccent else uiText.copy(alpha = 0.08f), RoundedCornerShape(32.dp))
                                                .testTag("altitude_unit_toggle_landscape")
                                                .pointerInput(Unit) {
                                                    detectDragGesturesAfterLongPress(
                                                        onDragStart = {
                                                            isAltitudeLandSliding = true
                                                            altitudeLandDragAccumulator = 0f
                                                            viewModel.hapticEngine.doubleClick()
                                                        },
                                                        onDragEnd = { isAltitudeLandSliding = false },
                                                        onDragCancel = { isAltitudeLandSliding = false },
                                                        onDrag = { _, dragAmount ->
                                                            altitudeLandDragAccumulator += dragAmount.x
                                                            val threshold = 70f
                                                            if (altitudeLandDragAccumulator > threshold) {
                                                                val values = AltitudeUnit.values()
                                                                val nextIdx = (currentAltitudeUnitState.ordinal + 1) % values.size
                                                                viewModel.selectAltitudeUnit(values[nextIdx])
                                                                viewModel.hapticEngine.playSpeedMilestoneTick()
                                                                altitudeLandDragAccumulator = 0f
                                                            } else if (altitudeLandDragAccumulator < -threshold) {
                                                                val values = AltitudeUnit.values()
                                                                val prevIdx = (currentAltitudeUnitState.ordinal - 1 + values.size) % values.size
                                                                viewModel.selectAltitudeUnit(values[prevIdx])
                                                                viewModel.hapticEngine.playSpeedMilestoneTick()
                                                                altitudeLandDragAccumulator = 0f
                                                            }
                                                        }
                                                    )
                                                }
                                                .clickable {
                                                    val nextOrdinal = (altitudeUnit.ordinal + 1) % AltitudeUnit.values().size
                                                    viewModel.selectAltitudeUnit(AltitudeUnit.values()[nextOrdinal])
                                                    viewModel.hapticEngine.click()
                                                },
                                            color = MaterialTheme.colorScheme.surface.copy(alpha = if (isAltitudeLandSliding) 0.45f else 0.25f),
                                        ) {
                                            Text(
                                                text = if (isAltitudeLandSliding) "ALT: ${altitudeUnit.label.uppercase()}" else altitudeUnit.label.uppercase(),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = uiAccent,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                letterSpacing = 1.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        var isPressureLandSliding by remember { mutableStateOf(false) }
                                        var pressureLandDragAccumulator by remember { mutableStateOf(0f) }

                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(32.dp))
                                                .border(0.5.dp, if (isPressureLandSliding) uiAccent else uiText.copy(alpha = 0.08f), RoundedCornerShape(32.dp))
                                                .testTag("pressure_unit_toggle_landscape")
                                                .pointerInput(Unit) {
                                                    detectDragGesturesAfterLongPress(
                                                        onDragStart = {
                                                            isPressureLandSliding = true
                                                            pressureLandDragAccumulator = 0f
                                                            viewModel.hapticEngine.doubleClick()
                                                        },
                                                        onDragEnd = { isPressureLandSliding = false },
                                                        onDragCancel = { isPressureLandSliding = false },
                                                        onDrag = { _, dragAmount ->
                                                            pressureLandDragAccumulator += dragAmount.x
                                                            val threshold = 70f
                                                            if (pressureLandDragAccumulator > threshold) {
                                                                val values = PressureUnit.values()
                                                                val nextIdx = (currentPressureUnitState.ordinal + 1) % values.size
                                                                viewModel.selectPressureUnit(values[nextIdx])
                                                                viewModel.hapticEngine.playSpeedMilestoneTick()
                                                                pressureLandDragAccumulator = 0f
                                                            } else if (pressureLandDragAccumulator < -threshold) {
                                                                val values = PressureUnit.values()
                                                                val prevIdx = (currentPressureUnitState.ordinal - 1 + values.size) % values.size
                                                                viewModel.selectPressureUnit(values[prevIdx])
                                                                viewModel.hapticEngine.playSpeedMilestoneTick()
                                                                pressureLandDragAccumulator = 0f
                                                            }
                                                        }
                                                    )
                                                }
                                                .clickable {
                                                    val nextOrdinal = (pressureUnit.ordinal + 1) % PressureUnit.values().size
                                                    viewModel.selectPressureUnit(PressureUnit.values()[nextOrdinal])
                                                    viewModel.hapticEngine.click()
                                                },
                                            color = MaterialTheme.colorScheme.surface.copy(alpha = if (isPressureLandSliding) 0.45f else 0.25f),
                                        ) {
                                            Text(
                                                text = if (isPressureLandSliding) "BARO: ${pressureUnit.label.uppercase()}" else pressureUnit.label.uppercase(),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = uiAccent,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                letterSpacing = 1.sp
                                            )
                                        }
                                    }
                                } else {
                                    // Compass index case 2
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(32.dp))
                                            .border(0.5.dp, uiText.copy(alpha = 0.08f), RoundedCornerShape(32.dp)),
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)
                                    ) {
                                        Text(
                                            text = "HEADING: ${headingDegrees.roundToInt()}°",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = uiAccent,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }
                                }
                            }

                            // 3. Compact landscape footer (theme gear and page indicators)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                                horizontalArrangement = if (pageIndex == 3) Arrangement.Center else Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Theme gear
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (currentTheme == AppTheme.SOPHISTICATED_DARK) Color(0xFF18181F).copy(alpha = 0.5f)
                                            else MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                            CircleShape
                                        )
                                        .testTag("theme_picker_landscape_button")
                                        .clickable { viewModel.toggleThemeSelection(!isThemeSelectionActive) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Themes",
                                        tint = uiAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                if (pageIndex != 3) {
                                    // Carousel Dots
                                    MidCarouselDots(pageIndex = Math.floorMod(pagerState.currentPage, 4), uiText = uiText)
                                }
                            }
                        }
                    }
                } else {
                    // --------------------------------------------------
                    // PORTRAIT COCKPIT DASHBOARD LAYOUT
                    // --------------------------------------------------
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val portraitPageIndex = Math.floorMod(pagerState.currentPage, 4)
                        // 1. PORTRAIT HEADER: Simulation controller and settings theme button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // On the TOP LEFT, we put the altitude source toggle (GPS vs BARO)
                            if (portraitPageIndex == 1 || portraitPageIndex == 3) {
                                val optAltSource by viewModel.altitudeSource.collectAsState()
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(32.dp))
                                        .border(
                                            0.5.dp,
                                            if (optAltSource == AltitudeSource.GPS) uiAccent else uiText.copy(alpha = 0.08f),
                                            RoundedCornerShape(32.dp)
                                        )
                                        .clickable {
                                            val nextSource = if (optAltSource == AltitudeSource.BAROMETER) AltitudeSource.GPS else AltitudeSource.BAROMETER
                                             viewModel.setAltitudeSource(nextSource)
                                             viewModel.hapticEngine.click()
                                        }
                                        .testTag("altitude_source_toggle_portrait"),
                                    color = if (optAltSource == AltitudeSource.GPS) uiAccent.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Canvas(modifier = Modifier.size(6.dp)) {
                                            drawCircle(color = if (optAltSource == AltitudeSource.GPS) uiAccent else uiText.copy(alpha = 0.5f))
                                        }
                                        Text(
                                            text = if (optAltSource == AltitudeSource.GPS) "GPS ALT" else "BARO ALT",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (optAltSource == AltitudeSource.GPS) uiAccent else uiText.copy(alpha = 0.8f),
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.width(1.dp))
                            }

                            // Theme Selection Settings Gear
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (currentTheme == AppTheme.SOPHISTICATED_DARK) Color(0xFF18181F).copy(alpha = 0.5f)
                                        else MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                        CircleShape
                                    )
                                    .then(
                                        if (currentTheme == AppTheme.SOPHISTICATED_DARK) Modifier.border(0.5.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                                        else Modifier.border(0.5.dp, uiText.copy(alpha = 0.15f), CircleShape)
                                    )
                                    .clickable { viewModel.toggleThemeSelection(!isThemeSelectionActive) }
                                    .testTag("theme_picker_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Themes",
                                    tint = uiAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // 2. MAIN PAGER VIEW
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) { page ->
                            val pageIndex = Math.floorMod(page, 4)
                            when (pageIndex) {
                                0 -> {
                                    // PORTRAIT SPEED PAGE
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxWidth()
                                                    .pointerInput(Unit) {
                                                        var dragAccumulator = 0f
                                                        var currentTempIndex = activeSpeedometerIndex

                                                        detectDragGesturesAfterLongPress(
                                                            onDragStart = {
                                                                viewModel.setSpeedPreviewActive(true)
                                                                dragAccumulator = 0f
                                                                currentTempIndex = currentSpeedometerIndexState
                                                                viewModel.hapticEngine.heavyClick()
                                                            },
                                                            onDragEnd = {
                                                                viewModel.setSpeedPreviewActive(false)
                                                            },
                                                            onDragCancel = {
                                                                viewModel.setSpeedPreviewActive(false)
                                                            },
                                                            onDrag = { _, dragAmount ->
                                                                dragAccumulator += dragAmount.x
                                                                val threshold = 70f
                                                                if (dragAccumulator > threshold) {
                                                                    currentTempIndex = (currentTempIndex - 1 + 30) % 30
                                                                    viewModel.previewSpeedometerStyle(currentTempIndex)
                                                                    viewModel.hapticEngine.playSpeedStyleSlide()
                                                                    dragAccumulator = 0f
                                                                } else if (dragAccumulator < -threshold) {
                                                                    currentTempIndex = (currentTempIndex + 1) % 30
                                                                    viewModel.previewSpeedometerStyle(currentTempIndex)
                                                                    viewModel.hapticEngine.playSpeedStyleSlide()
                                                                    dragAccumulator = 0f
                                                                }
                                                            }
                                                        )
                                                    },
                                                contentAlignment = Alignment.Center
                                             ) {
                                                 SpeedometerDisplay(
                                                     modifier = Modifier.padding(bottom = 32.dp),
                                                     animatedSpeed = animatedSpeed,
                                                     unitLabel = speedUnit.label,
                                                     styleIndex = activeSpeedometerIndex,
                                                     accentColor = uiAccent,
                                                     textColor = uiText
                                                 )
                                             }

                                            var isSpeedUnitSliding by remember { mutableStateOf(false) }
                                            var speedUnitDragAccumulator by remember { mutableStateOf(0f) }

                                            // Interactive metrics indicator badge (supports slide & click)
                                            Surface(
                                                modifier = Modifier
                                                    .padding(bottom = 24.dp)
                                                    .clip(RoundedCornerShape(32.dp))
                                                    .border(0.5.dp, if (isSpeedUnitSliding) uiAccent else uiText.copy(alpha = 0.08f), RoundedCornerShape(32.dp))
                                                    .testTag("speed_unit_toggle")
                                                    .pointerInput(Unit) {
                                                        detectDragGesturesAfterLongPress(
                                                             onDragStart = {
                                                                 isSpeedUnitSliding = true
                                                                 speedUnitDragAccumulator = 0f
                                                                 viewModel.hapticEngine.doubleClick()
                                                             },
                                                             onDragEnd = {
                                                                 isSpeedUnitSliding = false
                                                             },
                                                             onDragCancel = {
                                                                 isSpeedUnitSliding = false
                                                             },
                                                             onDrag = { _, dragAmount ->
                                                                 speedUnitDragAccumulator += dragAmount.x
                                                                 val threshold = 70f
                                                                 if (speedUnitDragAccumulator > threshold) {
                                                                     val values = SpeedUnit.values()
                                                                     val nextIndex = (currentSpeedUnitState.ordinal + 1) % values.size
                                                                     viewModel.selectSpeedUnit(values[nextIndex])
                                                                     viewModel.hapticEngine.playSpeedUnitSlide()
                                                                     speedUnitDragAccumulator = 0f
                                                                 } else if (speedUnitDragAccumulator < -threshold) {
                                                                     val values = SpeedUnit.values()
                                                                     val prevIndex = (currentSpeedUnitState.ordinal - 1 + values.size) % values.size
                                                                     viewModel.selectSpeedUnit(values[prevIndex])
                                                                     viewModel.hapticEngine.playSpeedUnitSlide()
                                                                     speedUnitDragAccumulator = 0f
                                                                 }
                                                             }
                                                        )
                                                    }
                                                    .clickable {
                                                        val nextUnitOrdinal = (speedUnit.ordinal + 1) % SpeedUnit.values().size
                                                        viewModel.selectSpeedUnit(SpeedUnit.values()[nextUnitOrdinal])
                                                        viewModel.hapticEngine.click()
                                                    },
                                                color = MaterialTheme.colorScheme.surface.copy(alpha = if (isSpeedUnitSliding) 0.45f else 0.25f)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = if (isSpeedUnitSliding) "UNIT: ${speedUnit.label.uppercase()}" else speedUnit.label.uppercase(),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = uiAccent,
                                                        letterSpacing = 1.sp
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = if (isSpeedUnitSliding) "◀ SLIDE ▶" else "• HOLD & SLIDE",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = if (isSpeedUnitSliding) uiAccent else uiText.copy(alpha = 0.2f),
                                                        letterSpacing = 1.sp
                                                    )
                                                }
                                            }
                                        }

                                        // Selection overlay indicator
                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = isSpeedPreviewActive,
                                            enter = fadeIn(animationSpec = tween(150)) + scaleIn(initialScale = 0.9f),
                                            exit = fadeOut(animationSpec = tween(150)) + scaleOut()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 100.dp)
                                                    .background(uiAccent.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                                                    .align(Alignment.TopCenter)
                                            ) {
                                                Text(
                                                    text = "DESIGN PREVIEW: ${activeSpeedometerIndex + 1} / 30",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                                    letterSpacing = 1.5.sp
                                                )
                                            }
                                        }
                                    }
                                }
                                1 -> {
                                    // PORTRAIT ATMOSPHERE PAGE
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!isBarometerAvailable) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center,
                                                modifier = Modifier.padding(32.dp)
                                            ) {
                                                Text(
                                                    text = "Barometer not available on this device",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Light,
                                                    color = uiText.copy(alpha = 0.6f),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        } else {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxWidth()
                                                     .padding(bottom = 32.dp)
                                                        .pointerInput(Unit) {
                                                            var dragAccumulator = 0f
                                                            var currentTempIndex = activeAtmosphereIndex

                                                            detectDragGesturesAfterLongPress(
                                                                onDragStart = {
                                                                    viewModel.setAtmospherePreviewActive(true)
                                                                    dragAccumulator = 0f
                                                                    currentTempIndex = currentAtmosphereIndexState
                                                                    viewModel.hapticEngine.heavyClick()
                                                                },
                                                                onDragEnd = {
                                                                    viewModel.setAtmospherePreviewActive(false)
                                                                },
                                                                onDragCancel = {
                                                                    viewModel.setAtmospherePreviewActive(false)
                                                                },
                                                                onDrag = { _, dragAmount ->
                                                                    dragAccumulator += dragAmount.x
                                                                    val threshold = 70f
                                                                    if (dragAccumulator > threshold) {
                                                                        currentTempIndex = (currentTempIndex - 1 + 30) % 30
                                                                        viewModel.previewAtmosphereStyle(currentTempIndex)
                                                                        viewModel.hapticEngine.playAtmosphereStyleSlide()
                                                                        dragAccumulator = 0f
                                                                    } else if (dragAccumulator < -threshold) {
                                                                        currentTempIndex = (currentTempIndex + 1) % 30
                                                                        viewModel.previewAtmosphereStyle(currentTempIndex)
                                                                        viewModel.hapticEngine.playAtmosphereStyleSlide()
                                                                        dragAccumulator = 0f
                                                                    }
                                                                }
                                                            )
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    AtmosphereDisplay(
                                                        animatedAltitude = animatedAltitude,
                                                        altitudeUnit = altitudeUnit,
                                                        animatedPressure = animatedPressure,
                                                        pressureUnit = pressureUnit,
                                                        verticalSpeed = verticalSpeedMPS,
                                                        styleIndex = activeAtmosphereIndex,
                                                        accentColor = uiAccent,
                                                        textColor = uiText
                                                    )
                                                }
 
                                                // Double interactive metric badges (supports tap & horizontal slide gestures)
                                                Row(
                                                    modifier = Modifier.padding(bottom = 24.dp),
                                                    horizontalArrangement = Arrangement.Center,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    var isAltitudeSliding by remember { mutableStateOf(false) }
                                                    var altitudeDragAccumulator by remember { mutableStateOf(0f) }

                                                    Surface(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(32.dp))
                                                            .border(0.5.dp, if (isAltitudeSliding) uiAccent else uiText.copy(alpha = 0.08f), RoundedCornerShape(32.dp))
                                                            .testTag("altitude_unit_toggle")
                                                            .pointerInput(Unit) {
                                                                detectDragGesturesAfterLongPress(
                                                                    onDragStart = {
                                                                        isAltitudeSliding = true
                                                                        altitudeDragAccumulator = 0f
                                                                        viewModel.hapticEngine.doubleClick()
                                                                    },
                                                                    onDragEnd = { isAltitudeSliding = false },
                                                                    onDragCancel = { isAltitudeSliding = false },
                                                                    onDrag = { _, dragAmount ->
                                                                        altitudeDragAccumulator += dragAmount.x
                                                                        val threshold = 70f
                                                                        if (altitudeDragAccumulator > threshold) {
                                                                            val values = AltitudeUnit.values()
                                                                            val nextIndex = (currentAltitudeUnitState.ordinal + 1) % values.size
                                                                            viewModel.selectAltitudeUnit(values[nextIndex])
                                                                            viewModel.hapticEngine.playAltitudeUnitSlide()
                                                                            altitudeDragAccumulator = 0f
                                                                        } else if (altitudeDragAccumulator < -threshold) {
                                                                            val values = AltitudeUnit.values()
                                                                            val prevIndex = (currentAltitudeUnitState.ordinal - 1 + values.size) % values.size
                                                                            viewModel.selectAltitudeUnit(values[prevIndex])
                                                                            viewModel.hapticEngine.playAltitudeUnitSlide()
                                                                            altitudeDragAccumulator = 0f
                                                                        }
                                                                    }
                                                                )
                                                            }
                                                            .clickable {
                                                                val nextOrdinal = (altitudeUnit.ordinal + 1) % AltitudeUnit.values().size
                                                                viewModel.selectAltitudeUnit(AltitudeUnit.values()[nextOrdinal])
                                                                viewModel.hapticEngine.click()
                                                            },
                                                        color = MaterialTheme.colorScheme.surface.copy(alpha = if (isAltitudeSliding) 0.45f else 0.25f),
                                                    ) {
                                                        Text(
                                                            text = if (isAltitudeSliding) "ALT: ${altitudeUnit.label.uppercase()}" else altitudeUnit.label.uppercase(),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = uiAccent,
                                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                                            letterSpacing = 1.sp
                                                        )
                                                    }
                                                    
                                                    Spacer(modifier = Modifier.width(12.dp))

                                                    var isPressureSliding by remember { mutableStateOf(false) }
                                                    var pressureDragAccumulator by remember { mutableStateOf(0f) }

                                                    Surface(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(32.dp))
                                                            .border(0.5.dp, if (isPressureSliding) uiAccent else uiText.copy(alpha = 0.08f), RoundedCornerShape(32.dp))
                                                            .testTag("pressure_unit_toggle")
                                                            .pointerInput(Unit) {
                                                                detectDragGesturesAfterLongPress(
                                                                    onDragStart = {
                                                                        isPressureSliding = true
                                                                        pressureDragAccumulator = 0f
                                                                        viewModel.hapticEngine.doubleClick()
                                                                    },
                                                                    onDragEnd = { isPressureSliding = false },
                                                                    onDragCancel = { isPressureSliding = false },
                                                                    onDrag = { _, dragAmount ->
                                                                        pressureDragAccumulator += dragAmount.x
                                                                        val threshold = 70f
                                                                        if (pressureDragAccumulator > threshold) {
                                                                            val values = PressureUnit.values()
                                                                            val nextIndex = (currentPressureUnitState.ordinal + 1) % values.size
                                                                            viewModel.selectPressureUnit(values[nextIndex])
                                                                            viewModel.hapticEngine.playPressureUnitSlide()
                                                                            pressureDragAccumulator = 0f
                                                                        } else if (pressureDragAccumulator < -threshold) {
                                                                            val values = PressureUnit.values()
                                                                            val prevIndex = (currentPressureUnitState.ordinal - 1 + values.size) % values.size
                                                                            viewModel.selectPressureUnit(values[prevIndex])
                                                                            viewModel.hapticEngine.playPressureUnitSlide()
                                                                            pressureDragAccumulator = 0f
                                                                        }
                                                                    }
                                                                )
                                                            }
                                                            .clickable {
                                                                val nextOrdinal = (pressureUnit.ordinal + 1) % PressureUnit.values().size
                                                                viewModel.selectPressureUnit(PressureUnit.values()[nextOrdinal])
                                                                viewModel.hapticEngine.click()
                                                            },
                                                        color = MaterialTheme.colorScheme.surface.copy(alpha = if (isPressureSliding) 0.45f else 0.25f),
                                                    ) {
                                                        Text(
                                                            text = if (isPressureSliding) "BARO: ${pressureUnit.label.uppercase()}" else pressureUnit.label.uppercase(),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = uiAccent,
                                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                                            letterSpacing = 1.sp
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // Selection overlay indicator
                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = isAtmospherePreviewActive,
                                            enter = fadeIn(animationSpec = tween(150)) + scaleIn(initialScale = 0.9f),
                                            exit = fadeOut(animationSpec = tween(150)) + scaleOut()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 100.dp)
                                                    .background(uiAccent.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                                                    .align(Alignment.TopCenter)
                                            ) {
                                                Text(
                                                    text = "STYLE PREVIEW: ${activeAtmosphereIndex + 1} / 30",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                                    letterSpacing = 1.5.sp
                                                )
                                            }
                                        }
                                    }
                                }
                                2 -> {
                                    // PORTRAIT COMPASS PAGE
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxWidth()
                                                    .padding(bottom = 32.dp)
                                                    .pointerInput(Unit) {
                                                        var dragAccumulator = 0f
                                                        var currentTempIndex = activeCompassIndex

                                                        detectDragGesturesAfterLongPress(
                                                            onDragStart = {
                                                                viewModel.setCompassPreviewActive(true)
                                                                dragAccumulator = 0f
                                                                currentTempIndex = currentCompassIndexState
                                                                viewModel.hapticEngine.heavyClick()
                                                            },
                                                            onDragEnd = {
                                                                viewModel.setCompassPreviewActive(false)
                                                            },
                                                            onDragCancel = {
                                                                viewModel.setCompassPreviewActive(false)
                                                            },
                                                            onDrag = { _, dragAmount ->
                                                                dragAccumulator += dragAmount.x
                                                                val threshold = 70f
                                                                if (dragAccumulator > threshold) {
                                                                    currentTempIndex = (currentTempIndex - 1 + 30) % 30
                                                                    viewModel.previewCompassStyle(currentTempIndex)
                                                                    viewModel.hapticEngine.playAtmosphereStyleSlide()
                                                                    dragAccumulator = 0f
                                                                } else if (dragAccumulator < -threshold) {
                                                                    currentTempIndex = (currentTempIndex + 1) % 30
                                                                    viewModel.previewCompassStyle(currentTempIndex)
                                                                    viewModel.hapticEngine.playAtmosphereStyleSlide()
                                                                    dragAccumulator = 0f
                                                                }
                                                            }
                                                        )
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CompassDisplay(
                                                    heading = headingDegrees,
                                                    styleIndex = activeCompassIndex,
                                                    accentColor = uiAccent,
                                                    textColor = uiText
                                                )
                                            }

                                            // Text summary of heading
                                            Surface(
                                                modifier = Modifier
                                                    .padding(bottom = 24.dp)
                                                    .clip(RoundedCornerShape(32.dp))
                                                    .border(0.5.dp, uiText.copy(alpha = 0.08f), RoundedCornerShape(32.dp)),
                                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)
                                            ) {
                                                Text(
                                                    text = "HEADING: ${headingDegrees.roundToInt()}° ${getCardinalDirection(headingDegrees)}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = uiAccent,
                                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                                    letterSpacing = 1.sp
                                                )
                                            }
                                        }

                                        // Selection overlay indicator
                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = isCompassPreviewActive,
                                            enter = fadeIn(animationSpec = tween(150)) + scaleIn(initialScale = 0.9f),
                                            exit = fadeOut(animationSpec = tween(150)) + scaleOut()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 100.dp)
                                                    .background(uiAccent.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                                                    .align(Alignment.TopCenter)
                                            ) {
                                                Text(
                                                    text = "COMPASS STYLE: ${activeCompassIndex + 1} / 30",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                                    letterSpacing = 1.5.sp
                                                )
                                            }
                                        }
                                    }
                                }
                                else -> {
                                    // PORTRAIT COMBINED SPEED & ALTITUDE PAGE
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(bottom = 16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        // Speed dial at 40% height
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            SpeedometerDisplay(
                                                animatedSpeed = animatedSpeed,
                                                unitLabel = speedUnit.label,
                                                styleIndex = activeSpeedometerIndex,
                                                accentColor = uiAccent,
                                                textColor = uiText
                                            )
                                        }

                                        // Thin dividing label
                                        Text(
                                            text = "COMBINED COCKPIT TELEMETRY",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = uiText.copy(alpha = 0.35f),
                                            letterSpacing = 2.sp,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )

                                        // Altitude dial at 40% height
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (!isBarometerAvailable) {
                                                Text(
                                                    text = "Barometer Unavailable",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Light,
                                                    color = uiText.copy(alpha = 0.4f)
                                                )
                                            } else {
                                                AtmosphereDisplay(
                                                    animatedAltitude = animatedAltitude,
                                                    altitudeUnit = altitudeUnit,
                                                    animatedPressure = animatedPressure,
                                                    pressureUnit = pressureUnit,
                                                    verticalSpeed = verticalSpeedMPS,
                                                    styleIndex = activeAtmosphereIndex,
                                                    accentColor = uiAccent,
                                                    textColor = uiText
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 3. CLEAN LIGHTWEIGHT BOTTOM PAGE INDICATOR
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 24.dp, top = 8.dp)
                        ) {
                            for (i in 0 until 4) {
                                val isActive = Math.floorMod(pagerState.currentPage, 4) == i
                                val dotColor by animateColorAsState(
                                    targetValue = if (isActive) uiText else uiText.copy(alpha = 0.15f),
                                    label = "dotColor"
                                )
                                val dotWidth by animateDpAsState(
                                    targetValue = if (isActive) 20.dp else 4.dp,
                                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                                    label = "dotWidth"
                                )

                                Box(
                                    modifier = Modifier
                                        .size(width = dotWidth, height = 4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(dotColor)
                                )
                            }
                        }
                    }
                }
            }

            // Theme Carousel scroll drawer overlay
            AnimatedVisibility(
                visible = isThemeSelectionActive,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                // Dim behind overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            // dismisses selector on tap outside
                            detectDragGesturesAfterLongPress(
                                onDragStart = {},
                                onDragEnd = {},
                                onDragCancel = {},
                                onDrag = { _, _ -> }
                            )
                        }
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            viewModel.toggleThemeSelection(false)
                        },
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 16.dp, end = 16.dp)
                            .shadow(16.dp, RoundedCornerShape(20.dp))
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                                RoundedCornerShape(20.dp)
                            )
                            .clickable(enabled = false, onClick = {}) // prevent pass-through click
                    ) {
                        Column(
                            modifier = Modifier.padding(top = 16.dp, bottom = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "VELOCITY THEMES",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = uiText.copy(alpha = 0.5f),
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Horizontal Swipe Carousel
                            val themes = AppTheme.values()
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                itemsIndexed(themes) { index, theme ->
                                    val isSelected = currentTheme == theme
                                    val designSubset = ThemeColors.getColorScheme(theme)

                                    Surface(
                                        modifier = Modifier
                                            .width(130.dp)
                                            .height(80.dp)
                                            .shadow(4.dp, RoundedCornerShape(12.dp))
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                viewModel.selectTheme(theme)
                                                // auto-dismiss on theme change
                                                viewModel.toggleThemeSelection(false)
                                            },
                                        color = designSubset.background,
                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(3.dp, uiAccent) else null
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(12.dp),
                                            contentAlignment = Alignment.BottomStart
                                        ) {
                                            // Tiny representation of aesthetics
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .background(designSubset.secondary, CircleShape)
                                                    .align(Alignment.TopEnd)
                                            )
                                            Text(
                                                text = theme.displayName,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = designSubset.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------------------
// ASTHETIC PERMISSION SCREEN FALLBACK
// --------------------------------------------------
@Composable
fun PermissionFallbackScreen(
    onGrantRequested: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(36.dp)
            .testTag("permission_fallback"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Aesthetic geometric focal indicator
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(24.dp)) {
                // Perfect satellite beacon icon
                drawCircle(color = Color.LightGray.copy(alpha = 0.2f), radius = this.size.width)
                drawCircle(color = Color.DarkGray, radius = this.size.width / 4f)
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "VELOCITY",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 6.sp,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "This application connects directly with high-fidelity satellite GPS speed sensors to record, smooth, and visualize live velocity readouts. Please authorize location access to engage the drive.",
            fontSize = 13.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(36.dp))
        Button(
            onClick = onGrantRequested,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(48.dp)
                .testTag("grant_permission_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "GRANT ACCESS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
fun MidCarouselDots(
    pageIndex: Int,
    uiText: Color,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        for (i in 0 until 4) {
            val isActive = pageIndex == i
            val dotColor = if (isActive) uiText else uiText.copy(alpha = 0.2f)
            val dotWidth = if (isActive) 12.dp else 4.dp
            Box(
                modifier = Modifier
                    .size(width = dotWidth, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(dotColor)
            )
        }
    }
}

fun getCardinalDirection(degrees: Float): String {
    val directions = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    val index = (((degrees % 360f) / 45f) + 0.5f).toInt() % 8
    return directions[index]
}
