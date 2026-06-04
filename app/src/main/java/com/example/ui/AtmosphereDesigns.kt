package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AtmosphereDisplay(
    animatedAltitude: Float,
    altitudeUnit: String,
    animatedPressure: Float,
    pressureUnit: String,
    verticalSpeed: Float, // climb descent rate in m/s
    styleIndex: Int,
    accentColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (styleIndex) {
            0 -> MinimalNumberStyle(animatedAltitude, altitudeUnit, animatedPressure, pressureUnit, verticalSpeed, accentColor, textColor)
            1 -> CircularPressureRing(animatedAltitude, altitudeUnit, animatedPressure, pressureUnit, verticalSpeed, accentColor, textColor)
            2 -> VerticalAltitudeScale(animatedAltitude, altitudeUnit, animatedPressure, pressureUnit, verticalSpeed, accentColor, textColor)
            3 -> PressureWaveStyle(animatedAltitude, altitudeUnit, animatedPressure, pressureUnit, verticalSpeed, accentColor, textColor)
            4 -> GradientHorizonStyle(animatedAltitude, altitudeUnit, animatedPressure, pressureUnit, verticalSpeed, accentColor, textColor)
            5 -> PrecisionGaugeStyle(animatedAltitude, altitudeUnit, animatedPressure, pressureUnit, verticalSpeed, accentColor, textColor)
            6 -> FloatingDigitsStyle(animatedAltitude, altitudeUnit, animatedPressure, pressureUnit, verticalSpeed, accentColor, textColor)
            7 -> DotMatrixStyle(animatedAltitude, altitudeUnit, animatedPressure, pressureUnit, verticalSpeed, accentColor, textColor)
            8 -> LineIndicatorStyle(animatedAltitude, altitudeUnit, animatedPressure, pressureUnit, verticalSpeed, accentColor, textColor)
            9 -> UltraMinimalStyle(animatedAltitude, altitudeUnit, animatedPressure, pressureUnit, verticalSpeed, accentColor, textColor)
            else -> MinimalNumberStyle(animatedAltitude, altitudeUnit, animatedPressure, pressureUnit, verticalSpeed, accentColor, textColor)
        }
    }
}

// --------------------------------------------------
// VERTICAL POSITION INDICATOR COMPOSABLE Helper
// --------------------------------------------------
@Composable
fun VerticalSpeedIndicator(
    verticalSpeed: Float, // rate of climb/descent
    accentColor: Color,
    textColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Simple elegant upward or downward arrowhead or level dashes
        Canvas(modifier = Modifier.size(16.dp)) {
            val w = size.width
            val h = size.height
            val strokeWidth = 2.dp.toPx()

            when {
                verticalSpeed > 0.15f -> { // CLIMBING (Up Arrow)
                    val path = Path().apply {
                        moveTo(w / 2f, h * 0.2f)
                        lineTo(w * 0.15f, h * 0.7f)
                        lineTo(w * 0.85f, h * 0.7f)
                        close()
                    }
                    drawPath(path = path, color = accentColor)
                }
                verticalSpeed < -0.15f -> { // DESCENDING (Down Arrow)
                    val path = Path().apply {
                        moveTo(w / 2f, h * 0.8f)
                        lineTo(w * 0.15f, h * 0.3f)
                        lineTo(w * 0.85f, h * 0.3f)
                        close()
                    }
                    drawPath(path = path, color = accentColor)
                }
                else -> { // STABLE (Level dashes)
                    drawLine(
                        color = textColor.copy(alpha = 0.4f),
                        start = Offset(w * 0.15f, h / 2f),
                        end = Offset(w * 0.85f, h / 2f),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
        val rateLabel = if (verticalSpeed > 0.15f) "climbing" else if (verticalSpeed < -0.15f) "descending" else "level"
        Text(
            text = rateLabel.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (rateLabel == "level") textColor.copy(alpha = 0.4f) else accentColor,
            letterSpacing = 2.sp
        )
    }
}

// --------------------------------------------------
// 1. MINIMAL NUMBER
// --------------------------------------------------
@Composable
fun MinimalNumberStyle(
    alt: Float, altUnit: String,
    press: Float, pressUnit: String,
    vSpeed: Float, accent: Color, textCol: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = String.format("%.0f", alt),
            fontSize = 72.sp,
            fontWeight = FontWeight.Bold,
            color = textCol,
            fontFamily = FontFamily.SansSerif
        )
        Text(
            text = "ALTITUDE (${altUnit.uppercase()})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textCol.copy(alpha = 0.5f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(28.dp))
        VerticalSpeedIndicator(vSpeed, accent, textCol)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = String.format("%.2f %s", press, pressUnit),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = textCol
        )
        Text(
            text = "BAROMETRIC PRESSURE",
            fontSize = 10.sp,
            color = textCol.copy(alpha = 0.4f),
            letterSpacing = 1.5.sp
        )
    }
}

// --------------------------------------------------
// 2. CIRCULAR PRESSURE RING
// --------------------------------------------------
@Composable
fun CircularPressureRing(
    alt: Float, altUnit: String,
    press: Float, pressUnit: String,
    vSpeed: Float, accent: Color, textCol: Color
) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f - 24.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)

            // Static background ring
            drawCircle(
                color = textCol.copy(alpha = 0.08f),
                radius = r,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )

            // Dynamic pressure arc mapping from 950hPa (0%) to 1050hPa (100%)
            val pressPercent = ((press - 950f) / 100f).coerceIn(0f, 1f)
            val sweepAngle = pressPercent * 360f

            drawArc(
                color = accent,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2, r * 2),
                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format("%.0f", alt),
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                color = textCol
            )
            Text(
                text = altUnit.lowercase(),
                fontSize = 11.sp,
                color = textCol.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            VerticalSpeedIndicator(vSpeed, accent, textCol)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = String.format("%.2f %s", press, pressUnit),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = accent
            )
        }
    }
}

// --------------------------------------------------
// 3. VERTICAL ALTITUDE SCALE
// --------------------------------------------------
@Composable
fun VerticalAltitudeScale(
    alt: Float, altUnit: String,
    press: Float, pressUnit: String,
    vSpeed: Float, accent: Color, textCol: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight().padding(horizontal = 24.dp)
    ) {
        // Vertical scale bar representing vertical displacement
        Canvas(modifier = Modifier.width(50.dp).fillMaxHeight(0.65f)) {
            val w = size.width
            val h = size.height
            val midY = h / 2f

            // Baseline Vertical guide
            drawLine(
                color = textCol.copy(alpha = 0.1f),
                start = Offset(w, 0f),
                end = Offset(w, h),
                strokeWidth = 1.5.dp.toPx()
            )

            // Markers spacing representing offset altitude delta
            val offsetLimit = 50
            val altInt = alt.toInt()
            for (i in (altInt - offsetLimit)..(altInt + offsetLimit)) {
                if (i % 5 != 0) continue // only drawing increments of 5 for tidiness
                val delta = i - alt
                val yPos = midY - delta * 4.dp.toPx() // spacing factor of 4dp per altitude unit

                if (yPos in 0f..h) {
                    val isMajor = i % 20 == 0
                    val markW = if (isMajor) w * 0.7f else w * 0.35f
                    drawLine(
                        color = if (i == altInt) accent else textCol.copy(alpha = if (isMajor) 0.5f else 0.2f),
                        start = Offset(w, yPos),
                        end = Offset(w - markW, yPos),
                        strokeWidth = if (i == altInt) 2.5.dp.toPx() else 1.dp.toPx()
                    )
                }
            }

            // Central locator block
            drawRect(
                color = accent,
                topLeft = Offset(w - 6.dp.toPx(), midY - 2.dp.toPx()),
                size = Size(6.dp.toPx(), 4.dp.toPx())
            )
        }

        Spacer(modifier = Modifier.width(36.dp))

        // Large textual data Column next to layout scale
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = String.format("%.0f", alt),
                fontSize = 58.sp,
                fontWeight = FontWeight.Bold,
                color = textCol
            )
            Text(
                text = "ALTITUDE (${altUnit.uppercase()})",
                fontSize = 11.sp,
                color = textCol.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            VerticalSpeedIndicator(vSpeed, accent, textCol)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = String.format("%.1f %s", press, pressUnit),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textCol
            )
        }
    }
}

// --------------------------------------------------
// 4. PRESSURE WAVE
// --------------------------------------------------
@Composable
fun PressureWaveStyle(
    alt: Float, altUnit: String,
    press: Float, pressUnit: String,
    vSpeed: Float, accent: Color, textCol: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pressureWave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        // Continuous kinetic pressure wave resting on back layer
        Canvas(modifier = Modifier.fillMaxWidth().height(160.dp).align(Alignment.Center)) {
            val w = size.width
            val h = size.height
            val path = Path()

            // Map pressure amplitude (more atmospheric pressure makes flatter or higher waves)
            val baseLevel = h / 2f
            val waveAmplitude = 18.dp.toPx() * (press / 1013f).coerceIn(0.5f, 1.8f)

            var first = true
            for (x in 0..w.toInt() step 5) {
                val relativeX = x.toFloat()
                val angle = (relativeX / w) * (2 * Math.PI).toFloat() * 1.5f + waveOffset
                val y = baseLevel + waveAmplitude * sin(angle)
                if (first) {
                    path.moveTo(relativeX, y)
                    first = false
                } else {
                    path.lineTo(relativeX, y)
                }
            }

            drawPath(
                path = path,
                color = accent.copy(alpha = 0.15f),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw a second thin wire wave
            val path2 = Path()
            first = true
            for (x in 0..w.toInt() step 5) {
                val relativeX = x.toFloat()
                val angle = (relativeX / w) * (2 * Math.PI).toFloat() * 1.5f + waveOffset + Math.PI.toFloat()
                val y = baseLevel + waveAmplitude * 0.7f * sin(angle)
                if (first) {
                    path2.moveTo(relativeX, y)
                    first = false
                } else {
                    path2.lineTo(relativeX, y)
                }
            }
            drawPath(
                path = path2,
                color = textCol.copy(alpha = 0.08f),
                style = Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                )
            )
        }

        // Forefront data
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format("%.0f", alt),
                fontSize = 58.sp,
                fontWeight = FontWeight.Bold,
                color = textCol
            )
            Text(
                text = altUnit.lowercase(),
                fontSize = 11.sp,
                color = textCol.copy(alpha = 0.5f),
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            VerticalSpeedIndicator(vSpeed, accent, textCol)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = String.format("%.2f %s", press, pressUnit),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = accent
            )
        }
    }
}

// --------------------------------------------------
// 5. GRADIENT HORIZON
// --------------------------------------------------
@Composable
fun GradientHorizonStyle(
    alt: Float, altUnit: String,
    press: Float, pressUnit: String,
    vSpeed: Float, accent: Color, textCol: Color
) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f - 24.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)

            // Outer horizon encasement
            drawCircle(
                color = textCol.copy(alpha = 0.08f),
                radius = r,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // Altitude displacement vertical pitch (simulated instrument pitch line)
            // Climb/Descent rotates the flight horizon slightly!
            val pitchOffset = (vSpeed * 10f).coerceIn(-40f, 40f)
            val rollAngle = (vSpeed * 3f).coerceIn(-15f, 15f)

            rotate(rollAngle, pivot = center) {
                // Horizon division line
                drawLine(
                    color = accent.copy(alpha = 0.4f),
                    start = Offset(center.x - r + 8.dp.toPx(), center.y + pitchOffset),
                    end = Offset(center.x + r - 8.dp.toPx(), center.y + pitchOffset),
                    strokeWidth = 2.dp.toPx()
                )

                // Sub ladders representing altitude units of displacement
                drawLine(
                    color = textCol.copy(alpha = 0.15f),
                    start = Offset(center.x - 20.dp.toPx(), center.y + pitchOffset - 25.dp.toPx()),
                    end = Offset(center.x + 20.dp.toPx(), center.y + pitchOffset - 25.dp.toPx()),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = textCol.copy(alpha = 0.15f),
                    start = Offset(center.x - 20.dp.toPx(), center.y + pitchOffset + 25.dp.toPx()),
                    end = Offset(center.x + 20.dp.toPx(), center.y + pitchOffset + 25.dp.toPx()),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Fixed aircraft reticle point center
            drawCircle(color = accent, radius = 4.dp.toPx(), center = center)
            drawLine(
                color = accent,
                start = Offset(center.x - 18.dp.toPx(), center.y),
                end = Offset(center.x - 7.dp.toPx(), center.y),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = accent,
                start = Offset(center.x + 7.dp.toPx(), center.y),
                end = Offset(center.x + 18.dp.toPx(), center.y),
                strokeWidth = 2.dp.toPx()
            )
        }

        Box(modifier = Modifier.padding(top = 110.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%.0f %s", alt, altUnit),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textCol
                )
                Text(
                    text = String.format("%.1f %s", press, pressUnit),
                    fontSize = 11.sp,
                    color = textCol.copy(alpha = 0.5f)
                )
            }
        }
    }
}

// --------------------------------------------------
// 6. PRECISION GAUGE (Aviation inspired raw instrumentation)
// --------------------------------------------------
@Composable
fun PrecisionGaugeStyle(
    alt: Float, altUnit: String,
    press: Float, pressUnit: String,
    vSpeed: Float, accent: Color, textCol: Color
) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f - 24.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)

            // Dynamic ticking dial rings
            drawCircle(
                color = textCol.copy(alpha = 0.05f),
                radius = r,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // Minor graduations
            for (i in 0 until 12) {
                val angle = i * 30f
                val rad = Math.toRadians(angle.toDouble())
                val tipLen = 10.dp.toPx()
                val start = Offset(
                    (center.x + (r - tipLen) * cos(rad)).toFloat(),
                    (center.y + (r - tipLen) * sin(rad)).toFloat()
                )
                val end = Offset(
                    (center.x + r * cos(rad)).toFloat(),
                    (center.y + r * sin(rad)).toFloat()
                )
                drawLine(
                    color = textCol.copy(alpha = 0.2f),
                    start = start,
                    end = end,
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Multi needles!
            // First pointer: Altitude 100s dial (sweeps complete circle every 1000 units)
            val altRemainder = alt % 1000f
            val pointer1Angle = (altRemainder / 1000f * 360f) - 90f
            val rad1 = Math.toRadians(pointer1Angle.toDouble())
            drawLine(
                color = accent,
                start = center,
                end = Offset(
                    (center.x + (r * 0.8f) * cos(rad1)).toFloat(),
                    (center.y + (r * 0.8f) * sin(rad1)).toFloat()
                ),
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Second pointer: Pressure (sweeps clockwise based on sensor bounds)
            val pressPercent = ((press - 950f) / 100f).coerceIn(0f, 1f)
            val pointer2Angle = (pressPercent * 360f) - 90f
            val rad2 = Math.toRadians(pointer2Angle.toDouble())
            drawLine(
                color = textCol.copy(alpha = 0.6f),
                start = center,
                end = Offset(
                    (center.x + (r * 0.55f) * cos(rad2)).toFloat(),
                    (center.y + (r * 0.55f) * sin(rad2)).toFloat()
                ),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawCircle(color = accent, radius = 5.dp.toPx(), center = center)
        }

        Box(modifier = Modifier.padding(top = 110.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%.0f %s", alt, altUnit),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textCol
                )
                Text(
                    text = String.format("%.1f %s", press, pressUnit),
                    fontSize = 11.sp,
                    color = textCol.copy(alpha = 0.5f)
                )
            }
        }
    }
}

// --------------------------------------------------
// 7. FLOATING DIGITS
// --------------------------------------------------
@Composable
fun FloatingDigitsStyle(
    alt: Float, altUnit: String,
    press: Float, pressUnit: String,
    vSpeed: Float, accent: Color, textCol: Color
) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        val altStr = String.format("%.0f", alt)

        // Triple layered numeric visual blocks to convey dynamic vertical depth
        Box(modifier = Modifier.offset(x = 10.dp, y = (-20).dp)) {
            Text(
                text = altStr,
                fontSize = 100.sp,
                color = textCol.copy(alpha = 0.04f),
                fontWeight = FontWeight.Black
            )
        }
        Box(modifier = Modifier.offset(x = (-10).dp, y = 20.dp)) {
            Text(
                text = altStr,
                fontSize = 105.sp,
                color = textCol.copy(alpha = 0.03f),
                fontWeight = FontWeight.Black
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format("%.0f", alt),
                fontSize = 68.sp,
                fontWeight = FontWeight.Bold,
                color = textCol
            )
            Text(
                text = "ALTITUDE (${altUnit.lowercase()})",
                fontSize = 11.sp,
                color = accent,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(18.dp))
            VerticalSpeedIndicator(vSpeed, accent, textCol)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = String.format("%.2f %s", press, pressUnit),
                fontSize = 14.sp,
                color = textCol.copy(alpha = 0.6f)
            )
        }
    }
}

// --------------------------------------------------
// 8. DOT MATRIX
// --------------------------------------------------
@Composable
fun DotMatrixStyle(
    alt: Float, altUnit: String,
    press: Float, pressUnit: String,
    vSpeed: Float, accent: Color, textCol: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val displayAltitude = String.format("%03d", alt.toInt().coerceIn(0, 999))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
            Canvas(modifier = Modifier.width(260.dp).height(115.dp)) {
                val dotsHorizontal = 30
                val dotsVertical = 12
                val dotRadius = 2.dp.toPx()
                val gapX = size.width / dotsHorizontal
                val gapY = size.height / dotsVertical

                for (col in 0 until dotsHorizontal) {
                    for (row in 0 until dotsVertical) {
                        val cx = col * gapX + gapX / 2f
                        val cy = row * gapY + gapY / 2f

                        // Map dot matrix configurations
                        val isLit = matrixLitMap(col, row, displayAltitude)
                        drawCircle(
                            color = if (isLit) accent else textCol.copy(alpha = 0.06f),
                            radius = dotRadius,
                            center = Offset(cx, cy)
                        )
                    }
                }
            }
        }
        Text(
            text = "ALTITUDE (${altUnit.lowercase()})",
            fontSize = 11.sp,
            color = textCol.copy(alpha = 0.6f),
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        VerticalSpeedIndicator(vSpeed, accent, textCol)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = String.format("%.1f %s", press, pressUnit),
            fontSize = 13.sp,
            color = accent,
            letterSpacing = 1.sp
        )
    }
}

// --------------------------------------------------
// 9. LINE INDICATOR
// --------------------------------------------------
@Composable
fun LineIndicatorStyle(
    alt: Float, altUnit: String,
    press: Float, pressUnit: String,
    vSpeed: Float, accent: Color, textCol: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
    ) {
        Text(
            text = String.format("%.0f", alt),
            fontSize = 62.sp,
            fontWeight = FontWeight.Bold,
            color = textCol
        )
        Text(
            text = altUnit.lowercase(),
            fontSize = 11.sp,
            color = textCol.copy(alpha = 0.5f),
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(18.dp))

        // Sliding cursor ribbon line representing alt displacement
        Canvas(modifier = Modifier.fillMaxWidth().height(16.dp)) {
            val w = size.width
            val h = size.height

            // Track bar
            drawRoundRect(
                color = textCol.copy(alpha = 0.07f),
                size = size,
                cornerRadius = CornerRadius(h / 2f)
            )

            // Accent sliding pulse representing altitude offset percentage (mapped from level 0 to 500m)
            val factor = (alt / 500f).coerceIn(0f, 1f)
            val indicatorX = w * factor
            drawCircle(
                color = accent,
                radius = 6.dp.toPx(),
                center = Offset(indicatorX, h / 2f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        VerticalSpeedIndicator(vSpeed, accent, textCol)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = String.format("%.1f %s", press, pressUnit),
            fontSize = 13.sp,
            color = textCol.copy(alpha = 0.6f)
        )
    }
}

// --------------------------------------------------
// 10. ULTRA MINIMAL
// --------------------------------------------------
@Composable
fun UltraMinimalStyle(
    alt: Float, altUnit: String,
    press: Float, pressUnit: String,
    vSpeed: Float, accent: Color, textCol: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = String.format("%.0f", alt),
                fontSize = 82.sp,
                fontWeight = FontWeight.Normal,
                color = textCol,
                fontFamily = FontFamily.Monospace,
                letterSpacing = (-4).sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Tiniest vertical climb rate bar
                Canvas(modifier = Modifier.size(6.dp)) {
                    drawRect(color = if (kotlin.math.abs(vSpeed) > 0.15f) accent else textCol.copy(alpha = 0.3f))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${altUnit.lowercase()} | ${String.format("%.1f %s", press, pressUnit)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Light,
                    color = textCol.copy(alpha = 0.4f),
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

private fun matrixLitMap(col: Int, row: Int, speedText: String): Boolean {
    val digitWidth = 7
    val spacing = 3
    
    val d1 = speedText[0] - '0'
    val d2 = speedText[1] - '0'
    val d3 = speedText[2] - '0'

    return when {
        col in 0 until digitWidth -> isDigitDotLit(col, row, d1)
        col in (digitWidth + spacing) until (digitWidth * 2 + spacing) -> isDigitDotLit(col - (digitWidth + spacing), row, d2)
        col in (digitWidth * 2 + spacing * 2) until (digitWidth * 3 + spacing * 2) -> isDigitDotLit(col - (digitWidth * 2 + spacing * 2), row, d3)
        else -> false
    }
}

private fun isDigitDotLit(x: Int, y: Int, digit: Int): Boolean {
    if (x == 0 || x == 6) {
        if (y in 1..10) {
            return when (digit) {
                0 -> true
                1 -> x == 6
                2 -> (y in 6..10 && x == 0) || (y in 1..5 && x == 6)
                3 -> x == 6
                4 -> (y in 1..5 && x == 0) || x == 6
                5 -> (y in 1..5 && x == 0) || (y in 6..10 && x == 6)
                6 -> (y in 1..10 && x == 0) || (y in 6..10 && x == 6)
                7 -> x == 6
                8 -> true
                9 -> (y in 1..5 && x == 0) || x == 6
                else -> false
            }
        }
    }
    if (y == 0 || y == 11 || y == 5 || y == 6) {
        if (x in 1..5) {
            return when (digit) {
                0 -> y == 0 || y == 11
                1 -> x == 3
                2 -> true
                3 -> true
                4 -> y == 5 || y == 6
                5 -> true
                6 -> true
                7 -> y == 0
                8 -> true
                9 -> true
                else -> false
            }
        }
    }
    return false
}
