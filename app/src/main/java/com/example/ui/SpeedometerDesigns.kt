package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpeedometerDisplay(
    animatedSpeed: Float,
    unitLabel: String,
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
            0 -> PureDigital(animatedSpeed, unitLabel, accentColor, textColor)
            1 -> ThinRing(animatedSpeed, unitLabel, accentColor, textColor)
            2 -> SplitArc(animatedSpeed, unitLabel, accentColor, textColor)
            3 -> AnalogNeedle(animatedSpeed, unitLabel, accentColor, textColor)
            4 -> LinearScale(animatedSpeed, unitLabel, accentColor, textColor)
            5 -> VerticalScale(animatedSpeed, unitLabel, accentColor, textColor)
            6 -> FloatingDigits(animatedSpeed, unitLabel, accentColor, textColor)
            7 -> EdgeGauge(animatedSpeed, unitLabel, accentColor, textColor)
            8 -> DotMatrixSpeedometer(animatedSpeed, unitLabel, accentColor, textColor)
            9 -> OledMinimal(animatedSpeed, unitLabel, accentColor, textColor)
            10 -> PrecisionDial(animatedSpeed, unitLabel, accentColor, textColor)
            11 -> CompassStyle(animatedSpeed, unitLabel, accentColor, textColor)
            12 -> ConcentricRings(animatedSpeed, unitLabel, accentColor, textColor)
            13 -> WaveMeter(animatedSpeed, unitLabel, accentColor, textColor)
            14 -> GlassRing(animatedSpeed, unitLabel, accentColor, textColor)
            15 -> TeslaInspired(animatedSpeed, unitLabel, accentColor, textColor)
            16 -> NothingInspired(animatedSpeed, unitLabel, accentColor, textColor)
            17 -> FocusMode(animatedSpeed, unitLabel, accentColor, textColor)
            18 -> AdaptiveGauge(animatedSpeed, unitLabel, accentColor, textColor)
            19 -> UltraMinimal(animatedSpeed, unitLabel, accentColor, textColor)
            20 -> CyberpunkNeon(animatedSpeed, unitLabel, accentColor, textColor)
            21 -> Chronograph(animatedSpeed, unitLabel, accentColor, textColor)
            22 -> BarChartHorizontal(animatedSpeed, unitLabel, accentColor, textColor)
            23 -> DoubleNeedle(animatedSpeed, unitLabel, accentColor, textColor)
            24 -> SectorRadar(animatedSpeed, unitLabel, accentColor, textColor)
            25 -> RetroLedSegment(animatedSpeed, unitLabel, accentColor, textColor)
            26 -> FuturisticRibbon(animatedSpeed, unitLabel, accentColor, textColor)
            27 -> LiquidCore(animatedSpeed, unitLabel, accentColor, textColor)
            28 -> TypographyFocus(animatedSpeed, unitLabel, accentColor, textColor)
            29 -> MatrixRain(animatedSpeed, unitLabel, accentColor, textColor)
            else -> PureDigital(animatedSpeed, unitLabel, accentColor, textColor)
        }
    }
}

// --------------------------------------------------
// 1. PURE DIGITAL
// --------------------------------------------------
@Composable
fun PureDigital(speed: Float, unit: String, accent: Color, textCol: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val speedStr = String.format("%.1f", speed)
        Text(
            text = speedStr,
            fontSize = 72.sp,
            fontWeight = FontWeight.Bold,
            color = textCol,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = unit.uppercase(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = accent,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Progress bar scaling from center
        Canvas(modifier = Modifier.width(180.dp).height(4.dp)) {
            val progress = (speed / 160f).coerceIn(0f, 1f)
            val w = size.width
            val h = size.height
            // Track
            drawRoundRect(
                color = textCol.copy(alpha = 0.15f),
                size = size,
                cornerRadius = CornerRadius(h / 2)
            )
            // Accent bar growing from center
            val barW = w * progress
            drawRoundRect(
                color = accent,
                topLeft = Offset((w - barW) / 2f, 0f),
                size = Size(barW, h),
                cornerRadius = CornerRadius(h / 2)
            )
        }
    }
}

// --------------------------------------------------
// 2. THIN RING
// --------------------------------------------------
@Composable
fun ThinRing(speed: Float, unit: String, accent: Color, textCol: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 2.dp.toPx()
            val activeStroke = 4.dp.toPx()
            val radius = size.minDimension / 2f - 20.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)

            // Outer subtle track
            drawCircle(
                color = textCol.copy(alpha = 0.08f),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Active Arc
            val sweepAngle = (speed / 160f).coerceIn(0f, 1f) * 360f
            drawArc(
                color = accent,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = activeStroke, cap = StrokeCap.Round)
            )
        }
        SpeedValueCentered(speed, unit, textCol, accent)
    }
}

// --------------------------------------------------
// 3. SPLIT ARC
// --------------------------------------------------
@Composable
fun SplitArc(speed: Float, unit: String, accent: Color, textCol: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f - 24.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)
            val stroke = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            val progress = (speed / 160f).coerceIn(0f, 1f)

            // Left track
            drawArc(
                color = textCol.copy(alpha = 0.1f),
                startAngle = 120f,
                sweepAngle = 120f,
                useCenter = false,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2, r * 2),
                style = stroke
            )
            // Left progress
            drawArc(
                color = accent,
                startAngle = 120f,
                sweepAngle = 120f * progress,
                useCenter = false,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2, r * 2),
                style = stroke
            )

            // Right track
            drawArc(
                color = textCol.copy(alpha = 0.1f),
                startAngle = -60f,
                sweepAngle = 120f,
                useCenter = false,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2, r * 2),
                style = stroke
            )
            // Right progress
            drawArc(
                color = accent,
                startAngle = 60f,
                sweepAngle = -120f * progress,
                useCenter = false,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2, r * 2),
                style = stroke
            )
        }
        SpeedValueCentered(speed, unit, textCol, accent)
    }
}

// --------------------------------------------------
// 4. ANALOG NEEDLE
// --------------------------------------------------
@Composable
fun AnalogNeedle(speed: Float, unit: String, accent: Color, textCol: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f - 30.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)
            val minAngle = 135f
            val maxAngle = 405f
            val totalAngle = maxAngle - minAngle
            val progress = (speed / 160f).coerceIn(0f, 1f)
            val targetAngle = minAngle + totalAngle * progress

            // Draw Dial markings
            for (i in 0..10) {
                val markAngle = minAngle + (totalAngle / 10f) * i
                val rad = Math.toRadians(markAngle.toDouble())
                val start = Offset(
                    (center.x + (r - 10.dp.toPx()) * cos(rad)).toFloat(),
                    (center.y + (r - 10.dp.toPx()) * sin(rad)).toFloat()
                )
                val end = Offset(
                    (center.x + r * cos(rad)).toFloat(),
                    (center.y + r * sin(rad)).toFloat()
                )
                drawOnTickMark(i, start, end, progress, accent, textCol)
            }

            // Draw Gauge Arc
            drawArc(
                color = textCol.copy(alpha = 0.1f),
                startAngle = minAngle,
                sweepAngle = totalAngle,
                useCenter = false,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2, r * 2),
                style = Stroke(width = 2.dp.toPx())
            )

            // Draw active arc segment
            drawArc(
                color = accent.copy(alpha = 0.3f),
                startAngle = minAngle,
                sweepAngle = totalAngle * progress,
                useCenter = false,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2, r * 2),
                style = Stroke(width = 6.dp.toPx())
            )

            // Draw Needle
            val needleRad = Math.toRadians(targetAngle.toDouble())
            val needleTip = Offset(
                (center.x + (r - 5.dp.toPx()) * cos(needleRad)).toFloat(),
                (center.y + (r - 5.dp.toPx()) * sin(needleRad)).toFloat()
            )
            drawLine(
                color = accent,
                start = center,
                end = needleTip,
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Needle Cap center circle
            drawCircle(color = textCol, radius = 6.dp.toPx(), center = center)
            drawCircle(color = accent, radius = 3.dp.toPx(), center = center)
        }

        Box(modifier = Modifier.padding(top = 110.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%.0f", speed),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = textCol
                )
                Text(
                    text = unit.uppercase(),
                    fontSize = 11.sp,
                    color = accent,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

private fun DrawScope.drawOnTickMark(index: Int, start: Offset, end: Offset, progress: Float, accent: Color, textCol: Color) {
    val markThreshold = progress * 10
    val isPassed = index <= markThreshold
    drawLine(
        color = if (isPassed) accent else textCol.copy(alpha = 0.25f),
        start = start,
        end = end,
        strokeWidth = if (isPassed) 2.5.dp.toPx() else 1.5.dp.toPx()
    )
}

// --------------------------------------------------
// 5. LINEAR SCALE (Horizontal Slider style)
// --------------------------------------------------
@Composable
fun LinearScale(speed: Float, unit: String, accent: Color, textCol: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
    ) {
        SpeedValueCentered(speed, unit, textCol, accent)
        Spacer(modifier = Modifier.height(30.dp))

        Canvas(modifier = Modifier.fillMaxWidth().height(48.dp)) {
            val w = size.width
            val h = size.height
            val midX = w / 2f

            // Background baseline
            drawLine(
                color = textCol.copy(alpha = 0.1f),
                start = Offset(0f, h),
                end = Offset(w, h),
                strokeWidth = 1.dp.toPx()
            )

            // Draw Ruler Marks based on active speed offset
            val offsetLimit = 50 // amount of units on screen left & right
            val speedInt = speed.toInt()

            for (i in (speedInt - offsetLimit)..(speedInt + offsetLimit)) {
                if (i < 0 || i > 250) continue
                // Position relative to screen center
                val delta = i - speed
                val xPos = midX + delta * 8.dp.toPx() // spacing of 8dp per speed unit
                if (xPos in 0f..w) {
                    val isMajor = i % 10 == 0
                    val isFive = i % 5 == 0 && !isMajor
                    val markH = when {
                        isMajor -> h * 0.7f
                        isFive -> h * 0.45f
                        else -> h * 0.25f
                    }

                    drawLine(
                        color = if (i == speedInt) accent else textCol.copy(alpha = if (isMajor) 0.5f else 0.2f),
                        start = Offset(xPos, h),
                        end = Offset(xPos, h - markH),
                        strokeWidth = if (i == speedInt) 2.5.dp.toPx() else 1.dp.toPx()
                    )
                }
            }

            // Central needle pointer marking the true exact float reading
            drawLine(
                color = accent,
                start = Offset(midX, h + 8.dp.toPx()),
                end = Offset(midX, h - h * 0.9f),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

// --------------------------------------------------
// 6. VERTICAL SCALE
// --------------------------------------------------
@Composable
fun VerticalScale(speed: Float, unit: String, accent: Color, textCol: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight().padding(horizontal = 30.dp)
    ) {
        // Left Centered Speed Value
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpeedValueCentered(speed, unit, textCol, accent)
        }

        // Right Vertical Slate Scale
        Canvas(modifier = Modifier.width(60.dp).fillMaxHeight(0.6f)) {
            val w = size.width
            val h = size.height
            val midY = h / 2f

            // Baseline vertical
            drawLine(
                color = textCol.copy(alpha = 0.1f),
                start = Offset(0f, 0f),
                end = Offset(0f, h),
                strokeWidth = 1.dp.toPx()
            )

            // Tick spacing: 8dp per km/h or mph unit
            val offsetLimit = 35
            val speedInt = speed.toInt()

            for (i in (speedInt - offsetLimit)..(speedInt + offsetLimit)) {
                if (i < 0 || i > 250) continue
                val delta = i - speed
                // Note: negative in delta is up physically, positive is down
                val yPos = midY - delta * 8.dp.toPx()

                if (yPos in 0f..h) {
                    val isMajor = i % 10 == 0
                    val isFive = i % 5 == 0 && !isMajor
                    val markW = when {
                        isMajor -> w * 0.6f
                        isFive -> w * 0.4f
                        else -> w * 0.2f
                    }
                    drawLine(
                        color = if (i == speedInt) accent else textCol.copy(alpha = if (isMajor) 0.5f else 0.2f),
                        start = Offset(0f, yPos),
                        end = Offset(markW, yPos),
                        strokeWidth = if (i == speedInt) 2.5.dp.toPx() else 1.dp.toPx()
                    )
                }
            }

            // Central pointer indicator
            val path = Path().apply {
                moveTo(-4.dp.toPx(), midY)
                lineTo(-12.dp.toPx(), midY - 6.dp.toPx())
                lineTo(-12.dp.toPx(), midY + 6.dp.toPx())
                close()
            }
            drawPath(path = path, color = accent)
        }
    }
}

// --------------------------------------------------
// 7. FLOATING DIGITS
// --------------------------------------------------
@Composable
fun FloatingDigits(speed: Float, unit: String, accent: Color, textCol: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        val speedStr = String.format("%.0f", speed)
        
        // Background large ghost digits creating a layered depth effect
        Box(modifier = Modifier.offset(x = 12.dp, y = (-24).dp)) {
            Text(
                text = speedStr,
                fontSize = 110.sp,
                fontWeight = FontWeight.Black,
                color = textCol.copy(alpha = 0.05f),
                fontFamily = FontFamily.SansSerif
            )
        }
        Box(modifier = Modifier.offset(x = (-12).dp, y = 24.dp)) {
            Text(
                text = speedStr,
                fontSize = 115.sp,
                fontWeight = FontWeight.Black,
                color = textCol.copy(alpha = 0.04f),
                fontFamily = FontFamily.SansSerif
            )
        }

        // Active Forefront Digital Value
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format("%.1f", speed),
                fontSize = 76.sp,
                fontWeight = FontWeight.Bold,
                color = textCol,
                letterSpacing = (-1).sp
            )
            Text(
                text = unit.uppercase(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = accent,
                letterSpacing = 3.sp
            )
        }
    }
}

// --------------------------------------------------
// 8. EDGE GAUGE
// --------------------------------------------------
@Composable
fun EdgeGauge(speed: Float, unit: String, accent: Color, textCol: Color) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Ambient arc spanning along the top edge of screen
        Canvas(modifier = Modifier.fillMaxWidth().height(140.dp).align(Alignment.TopCenter)) {
            val w = size.width
            val h = size.height
            val progress = (speed / 160f).coerceIn(0f, 1f)

            val startPoint = Offset(-20.dp.toPx(), shadowOffset)
            val controlPoint = Offset(w / 2f, h * 1.3f)
            val endPoint = Offset(w + 20.dp.toPx(), shadowOffset)

            // Draw clean background grey arc
            val pathTrack = Path().apply {
                moveTo(startPoint.x, startPoint.y)
                quadraticTo(controlPoint.x, controlPoint.y, endPoint.x, endPoint.y)
            }
            drawPath(
                path = pathTrack,
                color = textCol.copy(alpha = 0.08f),
                style = Stroke(width = 5.dp.toPx())
            )

            // Dynamic glow underneath
            val gradientBrush = Brush.radialGradient(
                colors = listOf(accent.copy(alpha = 0.22f * progress), Color.Transparent),
                center = Offset(w / 2f, h * 0.4f),
                radius = w * 0.6f
            )
            drawRect(brush = gradientBrush, size = Size(w, h))
        }

        // Concentrated Value
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpeedValueCentered(speed, unit, textCol, accent)
        }
    }
}
private const val shadowOffset = -10f

// --------------------------------------------------
// 9. DOT MATRIX SPEEDOMETER (Futuristic digital board)
// --------------------------------------------------
@Composable
fun DotMatrixSpeedometer(speed: Float, unit: String, accent: Color, textCol: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val displaySpeed = String.format("%03d", speed.toInt().coerceIn(0, 999))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
            // High fidelity matrix canvas background
            Canvas(modifier = Modifier.width(260.dp).height(115.dp)) {
                val dotsHorizontal = 30
                val dotsVertical = 12
                val dotRadius = 2.dp.toPx()
                val gapX = size.width / dotsHorizontal
                val gapY = size.height / dotsVertical

                for (col in 0 until dotsHorizontal) {
                    for (row in 0 until dotsVertical) {
                        // Light up specific index arrays or build custom mesh
                        val cx = col * gapX + gapX / 2f
                        val cy = row * gapY + gapY / 2f

                        // High fidelity pattern matrix logic
                        val isLit = matrixLitMap(col, row, displaySpeed)
                        drawCircle(
                            color = if (isLit) accent else textCol.copy(alpha = 0.07f),
                            radius = dotRadius,
                            center = Offset(cx, cy)
                        )
                    }
                }
            }
        }
        Text(
            text = unit.uppercase(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = accent,
            letterSpacing = 4.sp
        )
    }
}

// Mini mesh lit maps to approximate simple glowing numerical figures for ultimate retro vibe
private fun matrixLitMap(col: Int, row: Int, speedText: String): Boolean {
    // Break 30 columns into 3 cells of 8 columns with spacing of 3 cols
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
    // Standard primitive segment map to give dot matrix letters look
    // Digits are 7 columns (0..6) wide and 12 rows (0..11) high
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

// --------------------------------------------------
// 10. OLED MINIMAL (Ultra raw battery saving)
// --------------------------------------------------
@Composable
fun OledMinimal(speed: Float, unit: String, accent: Color, textCol: Color) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = String.format("%.0f", speed),
            fontSize = 90.sp,
            fontWeight = FontWeight.Normal,
            color = textCol,
            fontFamily = FontFamily.Monospace,
            letterSpacing = (-4).sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .padding(1.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = accent)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = unit.lowercase(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = textCol.copy(alpha = 0.5f),
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

// --------------------------------------------------
// 11. PRECISION DIAL
// --------------------------------------------------
@Composable
fun PrecisionDial(speed: Float, unit: String, accent: Color, textCol: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        val rotationAngle = speed * 1.5f // dynamic spinning ring bezel
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f - 20.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)

            rotate(rotationAngle, pivot = center) {
                // outer dial tick increments
                for (i in 0 until 60) {
                    val angle = i * 6f
                    val rad = Math.toRadians(angle.toDouble())
                    val tickLen = if (i % 5 == 0) 12.dp.toPx() else 5.dp.toPx()
                    val thickness = if (i % 5 == 0) 2.dp.toPx() else 1.dp.toPx()
                    val start = Offset(
                        (center.x + (r - tickLen) * cos(rad)).toFloat(),
                        (center.y + (r - tickLen) * sin(rad)).toFloat()
                    )
                    val end = Offset(
                        (center.x + r * cos(rad)).toFloat(),
                        (center.y + r * sin(rad)).toFloat()
                    )
                    drawLine(
                        color = if (i % 5 == 0) accent else textCol.copy(alpha = 0.2f),
                        start = start,
                        end = end,
                        strokeWidth = thickness
                    )
                }
            }

            // Dial casing circular guide
            drawCircle(
                color = textCol.copy(alpha = 0.05f),
                radius = r - 13.dp.toPx(),
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }
        SpeedValueCentered(speed, unit, textCol, accent)
    }
}

// --------------------------------------------------
// 12. COMPASS STYLE
// --------------------------------------------------
@Composable
fun CompassStyle(speed: Float, unit: String, accent: Color, textCol: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f - 30.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)
            val rotationAngle = speed * 0.8f

            // Clean background compass cardinal ticks
            rotate(rotationAngle, pivot = center) {
                drawCircle(
                    color = textCol.copy(alpha = 0.05f),
                    radius = r,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )

                val directions = listOf("N", "E", "S", "W")
                directions.forEachIndexed { idx, dir ->
                    val angle = idx * 90f
                    val rad = Math.toRadians(angle.toDouble())
                    val markX = (center.x + (r - 18.dp.toPx()) * cos(rad)).toFloat()
                    val markY = (center.y + (r - 18.dp.toPx()) * sin(rad)).toFloat()

                    // Cardinal anchor indicators
                    drawCircle(
                        color = if (dir == "N") accent else textCol.copy(alpha = 0.3f),
                        radius = if (dir == "N") 3.dp.toPx() else 1.5.dp.toPx(),
                        center = Offset(markX, markY)
                    )
                }
            }

            // Needle indicating active heading pointer
            val pointerPath = Path().apply {
                moveTo(center.x, center.y - r - 6.dp.toPx())
                lineTo(center.x - 7.dp.toPx(), center.y - r + 8.dp.toPx())
                lineTo(center.x + 7.dp.toPx(), center.y - r + 8.dp.toPx())
                close()
            }
            drawPath(path = pointerPath, color = accent)
        }
        SpeedValueCentered(speed, unit, textCol, accent)
    }
}

// --------------------------------------------------
// 13. CONCENTRIC RINGS
// --------------------------------------------------
@Composable
fun ConcentricRings(speed: Float, unit: String, accent: Color, textCol: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseR = size.minDimension / 2f - 40.dp.toPx()

            // 1. Outer Ring (Baseline Speed progress arc)
            val p1 = (speed / 160f).coerceIn(0f, 1f)
            drawCircle(
                color = textCol.copy(alpha = 0.05f),
                radius = baseR,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )
            drawArc(
                color = accent,
                startAngle = -90f,
                sweepAngle = p1 * 360f,
                useCenter = false,
                topLeft = Offset(center.x - baseR, center.y - baseR),
                size = Size(baseR * 2, baseR * 2),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // 2. Middle Ring (Velocity Ratio delta arc)
            val baseR2 = baseR - 16.dp.toPx()
            val p2 = (speed / 90f).coerceIn(0f, 1f)
            drawCircle(
                color = textCol.copy(alpha = 0.03f),
                radius = baseR2,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
            drawArc(
                color = textCol.copy(alpha = 0.6f),
                startAngle = 180f,
                sweepAngle = p2 * 360f,
                useCenter = false,
                topLeft = Offset(center.x - baseR2, center.y - baseR2),
                size = Size(baseR2 * 2, baseR2 * 2),
                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
            )

            // 3. Inner minimal dashed boundary
            val baseR3 = baseR2 - 16.dp.toPx()
            drawCircle(
                color = textCol.copy(alpha = 0.15f),
                radius = baseR3,
                center = center,
                style = Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 15f), 0f)
                )
            )
        }
        SpeedValueCentered(speed, unit, textCol, accent)
    }
}

// --------------------------------------------------
// 14. WAVE METER (Liquid flow dynamics)
// --------------------------------------------------
@Composable
fun WaveMeter(speed: Float, unit: String, accent: Color, textCol: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val phaseOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f - 20.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)
            val progress = (speed / 160f).coerceIn(0f, 1f)

            // Dynamic water level height inside circle
            val waveHeightY = center.y + r - (r * 2 * progress)

            // Base outline circle container
            drawCircle(
                color = textCol.copy(alpha = 0.12f),
                radius = r,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Build Wave Path clipped inside circular circle bounds
            val wavePath = Path()
            val clipPath = Path().apply {
                addOval(Rect(center.x - r, center.y - r, center.x + r, center.y + r))
            }

            // Math coordinates to outline wave
            var first = true
            for (x in (center.x - r).toInt()..(center.x + r).toInt()) {
                val waveAmplitude = 6.dp.toPx() * sin(progress * 1.5f).coerceAtLeast(0.1f)
                val relativeX = x - (center.x - r)
                // wave frequency
                val k = 0.045f
                val y = waveHeightY + waveAmplitude * sin(k * relativeX + phaseOffset)
                
                if (first) {
                    wavePath.moveTo(x.toFloat(), y)
                    first = false
                } else {
                    wavePath.lineTo(x.toFloat(), y)
                }
            }
            wavePath.lineTo(center.x + r, center.y + r)
            wavePath.lineTo(center.x - r, center.y + r)
            wavePath.close()

            // Draw fluid graphics clipped inside dial ring
            drawContext.canvas.save()
            drawContext.canvas.clipPath(clipPath)
            drawPath(
                path = wavePath,
                brush = Brush.verticalGradient(
                    colors = listOf(accent.copy(alpha = 0.8f), accent.copy(alpha = 0.2f)),
                    startY = waveHeightY - 10.dp.toPx(),
                    endY = center.y + r
                )
            )
            drawContext.canvas.restore()
        }
        SpeedValueCentered(speed, unit, textCol, accent)
    }
}

// --------------------------------------------------
// 15. GLASS RING (Glossy Translucent aesthetic)
// --------------------------------------------------
@Composable
fun GlassRing(speed: Float, unit: String, accent: Color, textCol: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f - 24.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)

            // Glossy gradient ring
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(textCol.copy(alpha = 0.15f), Color.Transparent, textCol.copy(alpha = 0.05f)),
                    start = Offset(center.x - r, center.y - r),
                    end = Offset(center.x + r, center.y + r)
                ),
                radius = r,
                center = center,
                style = Stroke(width = 16.dp.toPx())
            )

            // Inner neat circle ring
            drawCircle(
                color = textCol.copy(alpha = 0.12f),
                radius = r - 8.dp.toPx(),
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // Bright sweeping active accent arc inside the frame
            val sweep = (speed / 160f).coerceIn(0f, 1f) * 360f
            drawArc(
                color = accent,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(center.x - r + 4.dp.toPx(), center.y - r + 4.dp.toPx()),
                size = Size((r - 4.dp.toPx()) * 2, (r - 4.dp.toPx()) * 2),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        SpeedValueCentered(speed, unit, textCol, accent)
    }
}

// --------------------------------------------------
// 16. TESLA INSPIRED
// --------------------------------------------------
@Composable
fun TeslaInspired(speed: Float, unit: String, accent: Color, textCol: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Flat capsule indicator
        Canvas(modifier = Modifier.width(240.dp).height(12.dp)) {
            val w = size.width
            val h = size.height
            val progress = (speed / 160f).coerceIn(0f, 1f)

            // Rail track
            drawRoundRect(
                color = textCol.copy(alpha = 0.08f),
                size = size,
                cornerRadius = CornerRadius(h / 2f)
            )

            // Linear gradient active segment
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(accent.copy(alpha = 0.5f), accent),
                    startX = 0f,
                    endX = w * progress
                ),
                size = Size(w * progress, h),
                cornerRadius = CornerRadius(h / 2f)
            )

            // Tick graduation overlays
            for (i in 1..4) {
                val tickX = w * (i / 5f)
                drawLine(
                    color = textCol.copy(alpha = 0.25f),
                    start = Offset(tickX, 0f),
                    end = Offset(tickX, h),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        SpeedValueCentered(speed, unit, textCol, accent)
    }
}

// --------------------------------------------------
// 17. NOTHING INSPIRED (Dot-matrix typography & stark geometry)
// --------------------------------------------------
@Composable
fun NothingInspired(speed: Float, unit: String, accent: Color, textCol: Color) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .width(220.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sharp dot-matrix display brackets
        Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
            val w = size.width
            val h = size.height
            val bracketLen = 20.dp.toPx()
            val bracketStroke = 1.5.dp.toPx()

            // Draw bounding corners
            // Top Left
            drawLine(textCol, Offset(0f, 0f), Offset(bracketLen, 0f), bracketStroke)
            drawLine(textCol, Offset(0f, 0f), Offset(0f, bracketLen), bracketStroke)
            // Top Right
            drawLine(textCol, Offset(w, 0f), Offset(w - bracketLen, 0f), bracketStroke)
            drawLine(textCol, Offset(w, 0f), Offset(w, bracketLen), bracketStroke)
            // Bottom Left
            drawLine(textCol, Offset(0f, h), Offset(bracketLen, h), bracketStroke)
            drawLine(textCol, Offset(0f, h), Offset(0f, h - bracketLen), bracketStroke)
            // Bottom Right
            drawLine(textCol, Offset(w, h), Offset(w - bracketLen, h), bracketStroke)
            drawLine(textCol, Offset(w, h), Offset(w, h - bracketLen), bracketStroke)

            // Dynamic vertical bar inside margins
            val fillPercent = (speed / 160f).coerceIn(0f, 1f)
            val dotCount = 18
            val dotSpacing = h / dotCount
            for (i in 0 until dotCount) {
                val y = i * dotSpacing + dotSpacing / 2f
                val activeRatio = (dotCount - i).toFloat() / dotCount
                val isLit = fillPercent >= activeRatio
                drawCircle(
                    color = if (isLit) accent else textCol.copy(alpha = 0.1f),
                    radius = 2.5.dp.toPx(),
                    center = Offset(14.dp.toPx(), y)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = String.format("%.0f", speed),
            fontSize = 58.sp,
            fontWeight = FontWeight.Bold,
            color = textCol,
            fontFamily = FontFamily.Monospace,
            letterSpacing = (-1).sp
        )
        Text(
            text = unit.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = textCol.copy(alpha = 0.6f),
            fontFamily = FontFamily.Monospace,
            letterSpacing = 3.sp
        )
    }
}

// --------------------------------------------------
// 18. FOCUS MODE (Ultra Tranquil, fades into shadows)
// --------------------------------------------------
@Composable
fun FocusMode(speed: Float, unit: String, accent: Color, textCol: Color) {
    val dynamicAlpha = (speed / 30f).coerceIn(0.1f, 1f) // Fades with complete stationary values
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = String.format("%.0f", speed),
            fontSize = 110.sp,
            fontWeight = FontWeight.Light,
            color = textCol.copy(alpha = dynamicAlpha),
            letterSpacing = (-6).sp
        )
        Text(
            text = unit.lowercase(),
            fontSize = 15.sp,
            fontWeight = FontWeight.Light,
            color = accent.copy(alpha = dynamicAlpha),
            letterSpacing = 1.sp
        )
    }
}

// --------------------------------------------------
// 19. ADAPTIVE GAUGE (Morphing polygon geometry)
// --------------------------------------------------
@Composable
fun AdaptiveGauge(speed: Float, unit: String, accent: Color, textCol: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseR = size.minDimension / 2f - 30.dp.toPx()
            val progress = (speed / 160f).coerceIn(0f, 1f)
            val sides = 6 // Hexagon outline

            val pathTrack = Path()
            val pathFill = Path()

            for (i in 0..sides) {
                val angle = (360f / sides) * i
                val rad = Math.toRadians(angle.toDouble())
                // Base static coordinate
                val sx = (center.x + baseR * cos(rad)).toFloat()
                val sy = (center.y + baseR * sin(rad)).toFloat()

                // Morphing coordinate expanding proportional to speed
                val expansionFactor = 1.0f + (progress * 0.15f)
                val fx = (center.x + (baseR * expansionFactor) * cos(rad)).toFloat()
                val fy = (center.y + (baseR * expansionFactor) * sin(rad)).toFloat()

                if (i == 0) {
                    pathTrack.moveTo(sx, sy)
                    pathFill.moveTo(fx, fy)
                } else {
                    pathTrack.lineTo(sx, sy)
                    pathFill.lineTo(fx, fy)
                }
            }

            // Draw base structural matrix
            drawPath(
                path = pathTrack,
                color = textCol.copy(alpha = 0.08f),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Draw morphing gauge envelope line
            drawPath(
                path = pathFill,
                color = accent,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw minor hubs at vertices
            for (i in 0 until sides) {
                val angle = (360f / sides) * i
                val rad = Math.toRadians(angle.toDouble())
                val expansionFactor = 1.0f + (progress * 0.15f)
                val fx = (center.x + (baseR * expansionFactor) * cos(rad)).toFloat()
                val fy = (center.y + (baseR * expansionFactor) * sin(rad)).toFloat()

                drawCircle(color = accent, radius = 3.dp.toPx(), center = Offset(fx, fy))
            }
        }
        SpeedValueCentered(speed, unit, textCol, accent)
    }
}

// --------------------------------------------------
// 20. ULTRA MINIMAL (Orbit boundary point)
// --------------------------------------------------
@Composable
fun UltraMinimal(speed: Float, unit: String, accent: Color, textCol: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbit")
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        // The orbiting dot speeds up dynamically proportional to the speed reading!
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (1000f + (160f - speed).coerceAtLeast(0f) * 20f).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbitAngle"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f - 40.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)

            // Orbital trace ring guide
            drawCircle(
                color = textCol.copy(alpha = 0.04f),
                radius = r,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // Planet dot traveler
            val rad = Math.toRadians(orbitAngle.toDouble())
            val dotX = (center.x + r * cos(rad)).toFloat()
            val dotY = (center.y + r * sin(rad)).toFloat()

            drawCircle(
                color = accent,
                radius = 5.dp.toPx(),
                center = Offset(dotX, dotY)
            )

            // Subtle second slower shadow dot to make orbit trail smooth
            val shadowRad = Math.toRadians((orbitAngle - 15f).toDouble())
            drawCircle(
                color = accent.copy(alpha = 0.4f),
                radius = 3.5.dp.toPx(),
                center = Offset(
                    (center.x + r * cos(shadowRad)).toFloat(),
                    (center.y + r * sin(shadowRad)).toFloat()
                )
            )
        }
        SpeedValueCentered(speed, unit, textCol, accent)
    }
}

// Helper block for centered digits layouts
@Composable
fun SpeedValueCentered(speed: Float, unit: String, textCol: Color, accent: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = String.format("%.1f", speed),
            fontSize = 58.sp,
            fontWeight = FontWeight.Bold,
            color = textCol,
            letterSpacing = (-1).sp
        )
        Text(
            text = unit.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = accent,
            letterSpacing = 2.sp
        )
    }
}

@Composable
fun CyberpunkNeon(speed: Float, unit: String, accent: Color, textCol: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val progress = (speed / 160f).coerceIn(0f, 1f)
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f - 30.dp.toPx()
            
            drawArc(
                color = textCol.copy(alpha = 0.05f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
            val glowColor = if (accent == Color.Red) Color(0xFFFF007F) else accent
            drawArc(
                color = glowColor.copy(alpha = 0.25f),
                startAngle = 135f,
                sweepAngle = 270f * progress,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = glowColor,
                startAngle = 135f,
                sweepAngle = 270f * progress,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
            
            for (i in 0..10) {
                val angle = 135f + (i * 27f)
                val rad = Math.toRadians(angle.toDouble())
                val startDist = radius + 8.dp.toPx()
                val endDist = radius + 15.dp.toPx()
                val active = (i / 10f) <= progress
                drawLine(
                    color = if (active) glowColor else textCol.copy(alpha = 0.2f),
                    start = Offset(
                        (center.x + startDist * cos(rad)).toFloat(),
                        (center.y + startDist * sin(rad)).toFloat()
                    ),
                    end = Offset(
                        (center.x + endDist * cos(rad)).toFloat(),
                        (center.y + endDist * sin(rad)).toFloat()
                    ),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
        SpeedValueCentered(speed, unit, textCol, accent)
    }
}

@Composable
fun Chronograph(speed: Float, unit: String, accent: Color, textCol: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f - 20.dp.toPx()
            
            drawCircle(
                color = textCol.copy(alpha = 0.15f),
                radius = radius,
                style = Stroke(width = 3.dp.toPx())
            )
            
            for (i in 0 until 60) {
                val angle = i * 6f
                val rad = Math.toRadians(angle.toDouble())
                val tickLength = if (i % 5 == 0) 10.dp.toPx() else 5.dp.toPx()
                val tickW = if (i % 5 == 0) 2.dp.toPx() else 1.dp.toPx()
                val col = if (i % 5 == 0) accent else textCol.copy(alpha = 0.3f)
                drawLine(
                    color = col,
                    start = Offset(
                        (center.x + (radius - tickLength) * cos(rad)).toFloat(),
                        (center.y + (radius - tickLength) * sin(rad)).toFloat()
                    ),
                    end = Offset(
                        (center.x + radius * cos(rad)).toFloat(),
                        (center.y + radius * sin(rad)).toFloat()
                    ),
                    strokeWidth = tickW
                )
            }
            
            val speedAngle = -90f + (speed / 160f).coerceIn(0f, 1f) * 270f
            val needleRad = Math.toRadians(speedAngle.toDouble())
            val needleLen = radius - 15.dp.toPx()
            drawLine(
                color = accent,
                start = center,
                end = Offset(
                    (center.x + needleLen * cos(needleRad)).toFloat(),
                    (center.y + needleLen * sin(needleRad)).toFloat()
                ),
                strokeWidth = 3.5.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(color = accent, radius = 6.dp.toPx(), center = center)
            drawCircle(color = textCol, radius = 2.dp.toPx(), center = center)
        }
        
        Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%.0f", speed),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = textCol
                )
                Text(
                    text = unit.uppercase(),
                    fontSize = 10.sp,
                    color = accent,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BarChartHorizontal(speed: Float, unit: String, accent: Color, textCol: Color) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 40.dp)
    ) {
        Text(
            text = "VELOCITY",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = accent,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = String.format("%.1f", speed),
            fontSize = 62.sp,
            fontWeight = FontWeight.Black,
            color = textCol
        )
        Text(
            text = unit.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textCol.copy(alpha = 0.4f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        
        val progress = (speed / 160f).coerceIn(0f, 1f)
        for (row in 0 until 4) {
            val limit = (row + 1) / 4f
            val widthFactor = if (progress >= limit) 1f else if (progress < limit - 0.25f) 0f else (progress - (limit - 0.25f)) / 0.25f
            Canvas(modifier = Modifier.fillMaxWidth().height(12.dp).padding(vertical = 2.dp)) {
                drawRoundRect(
                    color = textCol.copy(alpha = 0.05f),
                    size = size,
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
                if (widthFactor > 0f) {
                    drawRoundRect(
                        brush = Brush.horizontalGradient(listOf(accent.copy(alpha = 0.6f), accent)),
                        size = Size(size.width * widthFactor, size.height),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                }
            }
        }
    }
}

@Composable
fun DoubleNeedle(speed: Float, unit: String, accent: Color, textCol: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f - 25.dp.toPx()
            
            drawArc(
                color = textCol.copy(alpha = 0.1f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 15f)))
            )
            
            val angleLeft = 180f + (speed / 160f).coerceIn(0f, 1f) * 90f
            val angleRight = 360f - (speed / 160f).coerceIn(0f, 1f) * 90f
            
            val radL = Math.toRadians(angleLeft.toDouble())
            val radR = Math.toRadians(angleRight.toDouble())
            
            drawLine(
                color = accent,
                start = center,
                end = Offset(
                    (center.x + radius * cos(radL)).toFloat(),
                    (center.y + radius * sin(radL)).toFloat()
                ),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = textCol.copy(alpha = 0.4f),
                start = center,
                end = Offset(
                    (center.x + radius * cos(radR)).toFloat(),
                    (center.y + radius * sin(radR)).toFloat()
                ),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(color = accent, radius = 5.dp.toPx(), center = center)
        }
        SpeedValueCentered(speed, unit, textCol, accent)
    }
}

@Composable
fun SectorRadar(speed: Float, unit: String, accent: Color, textCol: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
        val radarSweepAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "radarSweepAngle"
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f - 30.dp.toPx()
            val progress = (speed / 160f).coerceIn(0f, 1f)
            
            for (r in listOf(0.3f, 0.6f, 1.0f)) {
                drawCircle(
                    color = textCol.copy(alpha = 0.08f),
                    radius = radius * r,
                    style = Stroke(width = 1.dp.toPx())
                )
            }
            
            drawLine(color = textCol.copy(alpha = 0.05f), start = Offset(center.x - radius, center.y), end = Offset(center.x + radius, center.y), strokeWidth = 1.dp.toPx())
            drawLine(color = textCol.copy(alpha = 0.05f), start = Offset(center.x, center.y - radius), end = Offset(center.x, center.y + radius), strokeWidth = 1.dp.toPx())
            
            rotate(degrees = radarSweepAngle, pivot = center) {
                drawArc(
                    brush = Brush.sweepGradient(listOf(Color.Transparent, accent.copy(alpha = 0.2f), accent.copy(alpha = 0.5f))),
                    startAngle = 0f,
                    sweepAngle = 90f,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
            }
            
            drawArc(
                color = accent,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(center.x - radius - 5.dp.toPx(), center.y - radius - 5.dp.toPx()),
                size = Size((radius + 5.dp.toPx()) * 2, (radius + 5.dp.toPx()) * 2),
                style = Stroke(width = 3.dp.toPx())
            )
        }
        SpeedValueCentered(speed, unit, textCol, accent)
    }
}

@Composable
fun RetroLedSegment(speed: Float, unit: String, accent: Color, textCol: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
    ) {
        val speedStr = String.format("%03.0f", speed)
        Row(horizontalArrangement = Arrangement.Center) {
            speedStr.forEach { char ->
                Box(modifier = Modifier.padding(horizontal = 4.dp)) {
                    Text(
                        text = "8",
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Black,
                        color = textCol.copy(alpha = 0.04f),
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = char.toString(),
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Black,
                        color = accent,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
        Text(
            text = unit.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textCol.copy(alpha = 0.4f),
            letterSpacing = 4.sp
        )
    }
}

@Composable
fun FuturisticRibbon(speed: Float, unit: String, accent: Color, textCol: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f - 40.dp.toPx()
            val progress = (speed / 160f).coerceIn(0f, 1f)
            
            val trackPath = Path().apply {
                moveTo(center.x - radius, center.y + radius)
                cubicTo(
                    center.x - radius / 2, center.y - radius,
                    center.x + radius / 2, center.y + radius,
                    center.x + radius, center.y - radius
                )
            }
            drawPath(
                path = trackPath,
                color = textCol.copy(alpha = 0.1f),
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
            
            drawPath(
                path = trackPath,
                color = accent.copy(alpha = 0.5f),
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
            
            drawPath(
                path = trackPath,
                color = accent,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
            
            val posX = center.x - radius + (radius * 2f * progress)
            val posY = center.y + radius - (radius * 2f * progress)
            drawCircle(
                color = textCol,
                radius = 7.dp.toPx(),
                center = Offset(posX, posY)
            )
            drawCircle(
                color = accent,
                radius = 4.dp.toPx(),
                center = Offset(posX, posY)
            )
        }
        SpeedValueCentered(speed, unit, textCol, accent)
    }
}

@Composable
fun LiquidCore(speed: Float, unit: String, accent: Color, textCol: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "LiquidWobble")
    val wobbleWave by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wobbleWave"
    )
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = size.minDimension / 2f - 40.dp.toPx()
            val progress = (speed / 160f).coerceIn(0f, 1f)
            val currentRadius = 30.dp.toPx() + (maxRadius - 30.dp.toPx()) * progress
            
            drawCircle(
                color = textCol.copy(alpha = 0.05f),
                radius = maxRadius,
                style = Stroke(width = 2.dp.toPx())
            )
            
            val liquidPath = Path()
            val steps = 30
            for (i in 0..steps) {
                val angle = (i * 360f / steps)
                val rad = Math.toRadians(angle.toDouble())
                val waveFactor = 1f + (sin(rad * 4f + wobbleWave).toFloat() * 0.08f * progress)
                val r = currentRadius * waveFactor
                val px = center.x + r * cos(rad).toFloat()
                val py = center.y + r * sin(rad).toFloat()
                if (i == 0) {
                    liquidPath.moveTo(px, py)
                } else {
                    liquidPath.lineTo(px, py)
                }
            }
            liquidPath.close()
            
            drawPath(
                path = liquidPath,
                brush = Brush.radialGradient(
                    colors = listOf(accent, accent.copy(alpha = 0.4f), Color.Transparent),
                    center = center,
                    radius = currentRadius + 10.dp.toPx()
                )
            )
            
            drawPath(
                path = liquidPath,
                color = accent,
                style = Stroke(width = 2.5.dp.toPx())
            )
        }
        SpeedValueCentered(speed, unit, textCol, accent)
    }
}

@Composable
fun TypographyFocus(speed: Float, unit: String, accent: Color, textCol: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
    ) {
        val label = when {
            speed < 0.5f -> "IDLE"
            speed < 30f -> "CRUISING"
            speed < 80f -> "HASTE"
            speed < 120f -> "VELOCITY"
            else -> "SUPERSONIC"
        }
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = accent,
            letterSpacing = 4.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = String.format("%.1f", speed),
            fontSize = 76.sp,
            fontWeight = FontWeight.Normal,
            color = textCol,
            letterSpacing = (-2).sp
        )
        Text(
            text = unit.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textCol.copy(alpha = 0.4f),
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun MatrixRain(speed: Float, unit: String, accent: Color, textCol: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        val infiniteTransition = rememberInfiniteTransition(label = "MatrixFlow")
        val shiftFactor by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shiftFactor"
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f - 20.dp.toPx()
            val progress = (speed / 160f).coerceIn(0f, 1f)
            
            drawCircle(
                color = textCol.copy(alpha = 0.05f),
                radius = radius,
                style = Stroke(width = 1.dp.toPx())
            )
            
            val cols = 12
            for (col in 0 until cols) {
                val posX = center.x - radius + (col * (2f * radius / cols))
                val relativeColOffset = (col * 137f) % 1f
                val flowY = center.y - radius + (((shiftFactor + relativeColOffset) % 1f) * 2f * radius)
                
                val isActiveCol = col / cols.toFloat() <= progress
                val colCol = if (isActiveCol) accent.copy(alpha = 0.4f) else textCol.copy(alpha = 0.08f)
                
                drawCircle(
                    color = colCol,
                    radius = (2.dp.toPx() + 3.dp.toPx() * progress),
                    center = Offset(posX, flowY)
                )
                drawLine(
                    color = colCol.copy(alpha = 0.15f),
                    start = Offset(posX, center.y - radius),
                    end = Offset(posX, center.y + radius),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
        SpeedValueCentered(speed, unit, textCol, accent)
    }
}
