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
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasLocationPermission = granted
        if (granted) {
            viewModel.startAllTracking()
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

    val activeSpeedometerIndex by viewModel.activeSpeedometerIndex.collectAsState()
    val activeAtmosphereIndex by viewModel.activeAtmosphereIndex.collectAsState()

    val isSpeedPreviewActive by viewModel.isSpeedPreviewActive.collectAsState()
    val isAtmospherePreviewActive by viewModel.isAtmospherePreviewActive.collectAsState()
    val isThemeSelectionActive by viewModel.isThemeSelectionActive.collectAsState()

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
                val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
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
                LaunchedEffect(roundedSpeed) {
                    if (currentSpeedMPS > 0.15f && roundedSpeed != lastTickedSpeed) {
                        viewModel.hapticEngine.playSpeedMilestoneTick()
                        lastTickedSpeed = roundedSpeed
                    }
                }

                // Premium haptics: Altimeter physical ticks for altitude milestones
                var lastTickedAltitude by remember { mutableIntStateOf(0) }
                val roundedAltitude = animatedAltitude.roundToInt()
                LaunchedEffect(roundedAltitude) {
                    if (isBarometerAvailable && roundedAltitude != lastTickedAltitude) {
                        viewModel.hapticEngine.playSpeedMilestoneTick()
                        lastTickedAltitude = roundedAltitude
                    }
                }

                val configuration = LocalConfiguration.current
                val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

                if (isLandscape) {
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
                                if (page == 0) {
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
                                                        currentTempIndex = activeSpeedometerIndex
                                                    },
                                                    onDragEnd = {
                                                        viewModel.setSpeedPreviewActive(false)
                                                    },
                                                    onDragCancel = {
                                                        viewModel.setSpeedPreviewActive(false)
                                                    },
                                                    onDrag = { _, dragAmount ->
                                                        dragAccumulator += dragAmount.x
                                                        val threshold = 110f
                                                        if (dragAccumulator > threshold) {
                                                            currentTempIndex = (currentTempIndex - 1 + 20) % 20
                                                            viewModel.previewSpeedometerStyle(currentTempIndex)
                                                            dragAccumulator = 0f
                                                        } else if (dragAccumulator < -threshold) {
                                                            currentTempIndex = (currentTempIndex + 1) % 20
                                                            viewModel.previewSpeedometerStyle(currentTempIndex)
                                                            dragAccumulator = 0f
                                                        }
                                                    }
                                                )
                                            }
                                            .combinedClickable(
                                                onLongClick = {
                                                    val nextUnitOrdinal = (speedUnit.ordinal + 1) % SpeedUnit.values().size
                                                    viewModel.selectSpeedUnit(SpeedUnit.values()[nextUnitOrdinal])
                                                    viewModel.hapticEngine.playThemeChangeSuccess()
                                                },
                                                onClick = {}
                                            ),
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
                                                    text = "DESIGN: ${activeSpeedometerIndex + 1} / 20",
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
                                                        currentTempIndex = activeAtmosphereIndex
                                                    },
                                                    onDragEnd = {
                                                        viewModel.setAtmospherePreviewActive(false)
                                                    },
                                                    onDragCancel = {
                                                        viewModel.setAtmospherePreviewActive(false)
                                                    },
                                                    onDrag = { _, dragAmount ->
                                                        dragAccumulator += dragAmount.x
                                                        val threshold = 110f
                                                        if (dragAccumulator > threshold) {
                                                            currentTempIndex = (currentTempIndex - 1 + 10) % 10
                                                            viewModel.previewAtmosphereStyle(currentTempIndex)
                                                            dragAccumulator = 0f
                                                        } else if (dragAccumulator < -threshold) {
                                                            currentTempIndex = (currentTempIndex + 1) % 10
                                                            viewModel.previewAtmosphereStyle(currentTempIndex)
                                                            dragAccumulator = 0f
                                                        }
                                                    }
                                                )
                                            }
                                            .combinedClickable(
                                                onLongClick = {
                                                    val nextOrdinal = (altitudeUnit.ordinal + 1) % AltitudeUnit.values().size
                                                    viewModel.selectAltitudeUnit(AltitudeUnit.values()[nextOrdinal])
                                                    viewModel.hapticEngine.playThemeChangeSuccess()
                                                },
                                                onClick = {}
                                            ),
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
                                                altitudeUnit = altitudeUnit.label,
                                                animatedPressure = animatedPressure,
                                                pressureUnit = pressureUnit.label,
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
                                                    text = "STYLE: ${activeAtmosphereIndex + 1} / 10",
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                    letterSpacing = 1.sp
                                                )
                                            }
                                        }
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
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // 1. Simulation Source Control Capsule
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSimulating) uiAccent.copy(alpha = 0.08f) else uiText.copy(alpha = 0.04f))
                                    .border(
                                        0.5.dp,
                                        if (isSimulating) uiAccent.copy(alpha = 0.25f) else uiText.copy(alpha = 0.12f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { viewModel.toggleSimulation(!isSimulating) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(if (isSimulating) uiAccent else Color(0xFFFFCC00), CircleShape)
                                )
                                Text(
                                    text = if (isSimulating) "DRIVE SIM" else "REAL GPS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSimulating) uiAccent else uiText.copy(alpha = 0.7f),
                                    letterSpacing = 1.sp
                                )
                            }

                            // 2. Metric system indicator / selector badges
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                if (pagerState.currentPage == 0) {
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(32.dp))
                                            .border(0.5.dp, uiText.copy(alpha = 0.08f), RoundedCornerShape(32.dp))
                                            .testTag("speed_unit_toggle_landscape")
                                            .combinedClickable(
                                                onClick = {
                                                    val nextUnitOrdinal = (speedUnit.ordinal + 1) % SpeedUnit.values().size
                                                    viewModel.selectSpeedUnit(SpeedUnit.values()[nextUnitOrdinal])
                                                },
                                                onLongClick = {
                                                    val nextUnitOrdinal = (speedUnit.ordinal + 1) % SpeedUnit.values().size
                                                    viewModel.selectSpeedUnit(SpeedUnit.values()[nextUnitOrdinal])
                                                    viewModel.hapticEngine.playUnitSelection()
                                                }
                                            ),
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = speedUnit.name,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = uiAccent,
                                                letterSpacing = 1.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "• HOLD STYLE",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = uiText.copy(alpha = 0.25f)
                                            )
                                        }
                                    }
                                } else {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(32.dp))
                                                .border(0.5.dp, uiText.copy(alpha = 0.08f), RoundedCornerShape(32.dp))
                                                .testTag("altitude_unit_toggle_landscape")
                                                .combinedClickable(
                                                    onClick = {
                                                        val nextOrdinal = (altitudeUnit.ordinal + 1) % AltitudeUnit.values().size
                                                        viewModel.selectAltitudeUnit(AltitudeUnit.values()[nextOrdinal])
                                                    },
                                                    onLongClick = {
                                                        val nextOrdinal = (altitudeUnit.ordinal + 1) % AltitudeUnit.values().size
                                                        viewModel.selectAltitudeUnit(AltitudeUnit.values()[nextOrdinal])
                                                        viewModel.hapticEngine.playUnitSelection()
                                                    }
                                                ),
                                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f),
                                        ) {
                                            Text(
                                                text = altitudeUnit.label.uppercase(),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = uiAccent,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                letterSpacing = 1.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(32.dp))
                                                .border(0.5.dp, uiText.copy(alpha = 0.08f), RoundedCornerShape(32.dp))
                                                .testTag("pressure_unit_toggle_landscape")
                                                .combinedClickable(
                                                    onClick = {
                                                        val nextOrdinal = (pressureUnit.ordinal + 1) % PressureUnit.values().size
                                                        viewModel.selectPressureUnit(PressureUnit.values()[nextOrdinal])
                                                    },
                                                    onLongClick = {
                                                        val nextOrdinal = (pressureUnit.ordinal + 1) % PressureUnit.values().size
                                                        viewModel.selectPressureUnit(PressureUnit.values()[nextOrdinal])
                                                        viewModel.hapticEngine.playUnitSelection()
                                                    }
                                                ),
                                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f),
                                        ) {
                                            Text(
                                                text = pressureUnit.label.uppercase(),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = uiAccent,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                letterSpacing = 1.sp
                                            )
                                        }
                                    }
                                }
                            }

                            // 3. Compact landscape footer (theme gear and page indicators)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
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

                                // Carousel Dots
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    for (i in 0 until 2) {
                                        val isActive = pagerState.currentPage == i
                                        val dotColor by animateColorAsState(
                                            targetValue = if (isActive) uiText else uiText.copy(alpha = 0.15f),
                                            label = "dotColor"
                                        )
                                        val dotWidth by animateDpAsState(
                                            targetValue = if (isActive) 16.dp else 4.dp,
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
                } else {
                    // --------------------------------------------------
                    // PORTRAIT COCKPIT DASHBOARD LAYOUT
                    // --------------------------------------------------
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // 1. PORTRAIT HEADER: Simulation controller and settings theme button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Interactive Simulation switch
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSimulating) uiAccent.copy(alpha = 0.08f) else uiText.copy(alpha = 0.04f))
                                    .border(
                                        0.5.dp,
                                        if (isSimulating) uiAccent.copy(alpha = 0.25f) else uiText.copy(alpha = 0.12f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { viewModel.toggleSimulation(!isSimulating) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                                val pulseAlpha by infiniteTransition.animateFloat(
                                    initialValue = 0.4f,
                                    targetValue = 1.0f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(1200, easing = LinearEasing),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "pulseAlpha"
                                )

                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(if (isSimulating) uiAccent else Color(0xFFFFCC00), CircleShape)
                                        .drawBehind {
                                            if (isSimulating) {
                                                drawCircle(
                                                    color = uiAccent.copy(alpha = pulseAlpha),
                                                    radius = size.width * 1.5f
                                                )
                                            }
                                        }
                                )
                                Text(
                                    text = if (isSimulating) "DRIVE SIMULATOR" else "REAL SATELLITE GPS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSimulating) uiAccent else uiText.copy(alpha = 0.7f),
                                    letterSpacing = 1.5.sp
                                )
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
                            when (page) {
                                0 -> {
                                    // PORTRAIT SPEED PAGE
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
                                                        currentTempIndex = activeSpeedometerIndex
                                                    },
                                                    onDragEnd = {
                                                        viewModel.setSpeedPreviewActive(false)
                                                    },
                                                    onDragCancel = {
                                                        viewModel.setSpeedPreviewActive(false)
                                                    },
                                                    onDrag = { _, dragAmount ->
                                                        dragAccumulator += dragAmount.x
                                                        val threshold = 110f
                                                        if (dragAccumulator > threshold) {
                                                            currentTempIndex = (currentTempIndex - 1 + 20) % 20
                                                            viewModel.previewSpeedometerStyle(currentTempIndex)
                                                            dragAccumulator = 0f
                                                        } else if (dragAccumulator < -threshold) {
                                                            currentTempIndex = (currentTempIndex + 1) % 20
                                                            viewModel.previewSpeedometerStyle(currentTempIndex)
                                                            dragAccumulator = 0f
                                                        }
                                                    }
                                                )
                                            }
                                            .combinedClickable(
                                                onLongClick = {
                                                    val nextUnitOrdinal = (speedUnit.ordinal + 1) % SpeedUnit.values().size
                                                    viewModel.selectSpeedUnit(SpeedUnit.values()[nextUnitOrdinal])
                                                    viewModel.hapticEngine.playThemeChangeSuccess()
                                                },
                                                onClick = {}
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
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

                                            // Interactive metrics indicator badge (supports click & long click)
                                            Surface(
                                                modifier = Modifier
                                                    .padding(bottom = 24.dp)
                                                    .clip(RoundedCornerShape(32.dp))
                                                    .border(0.5.dp, uiText.copy(alpha = 0.08f), RoundedCornerShape(32.dp))
                                                    .testTag("speed_unit_toggle")
                                                    .combinedClickable(
                                                        onClick = {
                                                            val nextUnitOrdinal = (speedUnit.ordinal + 1) % SpeedUnit.values().size
                                                            viewModel.selectSpeedUnit(SpeedUnit.values()[nextUnitOrdinal])
                                                        },
                                                        onLongClick = {
                                                            val nextUnitOrdinal = (speedUnit.ordinal + 1) % SpeedUnit.values().size
                                                            viewModel.selectSpeedUnit(SpeedUnit.values()[nextUnitOrdinal])
                                                            viewModel.hapticEngine.playUnitSelection()
                                                        }
                                                    ),
                                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = speedUnit.label.uppercase(),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = uiAccent,
                                                        letterSpacing = 1.sp
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "• HOLD METRIC",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = uiText.copy(alpha = 0.2f),
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
                                                    text = "DESIGN PREVIEW: ${activeSpeedometerIndex + 1} / 20",
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
                                            .fillMaxSize()
                                            .pointerInput(Unit) {
                                                var dragAccumulator = 0f
                                                var currentTempIndex = activeAtmosphereIndex

                                                detectDragGesturesAfterLongPress(
                                                    onDragStart = {
                                                        viewModel.setAtmospherePreviewActive(true)
                                                        dragAccumulator = 0f
                                                        currentTempIndex = activeAtmosphereIndex
                                                    },
                                                    onDragEnd = {
                                                        viewModel.setAtmospherePreviewActive(false)
                                                    },
                                                    onDragCancel = {
                                                        viewModel.setAtmospherePreviewActive(false)
                                                    },
                                                    onDrag = { _, dragAmount ->
                                                        dragAccumulator += dragAmount.x
                                                        val threshold = 110f
                                                        if (dragAccumulator > threshold) {
                                                            currentTempIndex = (currentTempIndex - 1 + 10) % 10
                                                            viewModel.previewAtmosphereStyle(currentTempIndex)
                                                            dragAccumulator = 0f
                                                        } else if (dragAccumulator < -threshold) {
                                                            currentTempIndex = (currentTempIndex + 1) % 10
                                                            viewModel.previewAtmosphereStyle(currentTempIndex)
                                                            dragAccumulator = 0f
                                                        }
                                                    }
                                                )
                                            }
                                            .combinedClickable(
                                                onLongClick = {
                                                    val nextOrdinal = (altitudeUnit.ordinal + 1) % AltitudeUnit.values().size
                                                    viewModel.selectAltitudeUnit(AltitudeUnit.values()[nextOrdinal])
                                                    viewModel.hapticEngine.playThemeChangeSuccess()
                                                },
                                                onClick = {}
                                            ),
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
                                                        .fillMaxWidth(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    AtmosphereDisplay(
                                                        animatedAltitude = animatedAltitude,
                                                        altitudeUnit = altitudeUnit.label,
                                                        animatedPressure = animatedPressure,
                                                        pressureUnit = pressureUnit.label,
                                                        verticalSpeed = verticalSpeedMPS,
                                                        styleIndex = activeAtmosphereIndex,
                                                        accentColor = uiAccent,
                                                        textColor = uiText
                                                    )
                                                }

                                                // Double interactive metric badges (supports tap & long clicks)
                                                Row(
                                                    modifier = Modifier.padding(bottom = 24.dp),
                                                    horizontalArrangement = Arrangement.Center,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Surface(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(32.dp))
                                                            .border(0.5.dp, uiText.copy(alpha = 0.08f), RoundedCornerShape(32.dp))
                                                            .testTag("altitude_unit_toggle")
                                                            .combinedClickable(
                                                                onClick = {
                                                                    val nextOrdinal = (altitudeUnit.ordinal + 1) % AltitudeUnit.values().size
                                                                    viewModel.selectAltitudeUnit(AltitudeUnit.values()[nextOrdinal])
                                                                },
                                                                onLongClick = {
                                                                    val nextOrdinal = (altitudeUnit.ordinal + 1) % AltitudeUnit.values().size
                                                                    viewModel.selectAltitudeUnit(AltitudeUnit.values()[nextOrdinal])
                                                                    viewModel.hapticEngine.playUnitSelection()
                                                                }
                                                            ),
                                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f),
                                                    ) {
                                                        Text(
                                                            text = altitudeUnit.label.uppercase(),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = uiAccent,
                                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                                            letterSpacing = 1.sp
                                                        )
                                                    }
                                                    
                                                    Spacer(modifier = Modifier.width(12.dp))

                                                    Surface(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(32.dp))
                                                            .border(0.5.dp, uiText.copy(alpha = 0.08f), RoundedCornerShape(32.dp))
                                                            .testTag("pressure_unit_toggle")
                                                            .combinedClickable(
                                                                onClick = {
                                                                    val nextOrdinal = (pressureUnit.ordinal + 1) % PressureUnit.values().size
                                                                    viewModel.selectPressureUnit(PressureUnit.values()[nextOrdinal])
                                                                },
                                                                onLongClick = {
                                                                    val nextOrdinal = (pressureUnit.ordinal + 1) % PressureUnit.values().size
                                                                    viewModel.selectPressureUnit(PressureUnit.values()[nextOrdinal])
                                                                    viewModel.hapticEngine.playUnitSelection()
                                                                }
                                                            ),
                                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f),
                                                    ) {
                                                        Text(
                                                            text = pressureUnit.label.uppercase(),
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
                                                    text = "STYLE PREVIEW: ${activeAtmosphereIndex + 1} / 10",
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
                            for (i in 0 until 2) {
                                val isActive = pagerState.currentPage == i
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
