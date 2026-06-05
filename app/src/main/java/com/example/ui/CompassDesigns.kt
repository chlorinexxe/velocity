package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
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
fun CompassDisplay(
    heading: Float,
    styleIndex: Int,
    accentColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val animatedHeading by animateFloatAsState(
        targetValue = heading,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "animatedHeading"
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (styleIndex) {
            0 -> MinimalCard(animatedHeading, accentColor, textColor)
            1 -> ClassicRose(animatedHeading, accentColor, textColor)
            2 -> RadarSweeper(animatedHeading, accentColor, textColor)
            3 -> RotatingRing(animatedHeading, accentColor, textColor)
            4 -> ModernOled(animatedHeading, accentColor, textColor)
            5 -> TacticalGps(animatedHeading, accentColor, textColor)
            6 -> BubbleFloat(animatedHeading, accentColor, textColor)
            7 -> HorizonTape(animatedHeading, accentColor, textColor)
            8 -> FuturisticAero(animatedHeading, accentColor, textColor)
            9 -> DotMatrixCompass(animatedHeading, accentColor, textColor)
            10 -> GlassOrb(animatedHeading, accentColor, textColor)
            11 -> SteampunkBrass(animatedHeading, accentColor, textColor)
            12 -> SplitDial(animatedHeading, accentColor, textColor)
            13 -> DigitalScope(animatedHeading, accentColor, textColor)
            14 -> DualNeedleCompass(animatedHeading, accentColor, textColor)
            15 -> PolyGlow(animatedHeading, accentColor, textColor)
            16 -> CyberRing(animatedHeading, accentColor, textColor)
            17 -> AbstractFocus(animatedHeading, accentColor, textColor)
            18 -> LiquidOrbit(animatedHeading, accentColor, textColor)
            19 -> Constellation(animatedHeading, accentColor, textColor)
            20 -> SonicRadar(animatedHeading, accentColor, textColor)
            21 -> HexaPulse(animatedHeading, accentColor, textColor)
            22 -> RetroTape(animatedHeading, accentColor, textColor)
            23 -> MinimalCross(animatedHeading, accentColor, textColor)
            24 -> AuraCompass(animatedHeading, accentColor, textColor)
            25 -> GridMap(animatedHeading, accentColor, textColor)
            26 -> SolarSystem(animatedHeading, accentColor, textColor)
            27 -> MatrixRainHeading(animatedHeading, accentColor, textColor)
            28 -> SilhouetteHorizon(animatedHeading, accentColor, textColor)
            29 -> TeslaCompass(animatedHeading, accentColor, textColor)
            else -> MinimalCard(animatedHeading, accentColor, textColor)
        }
    }
}

@Composable
fun CompassTextCentered(heading: Float, textColor: Color, accent: Color) {
    val bearing = (heading + 360f) % 360f
    val direction = when {
        bearing >= 337.5f || bearing < 22.5f -> "N"
        bearing >= 22.5f && bearing < 67.5f -> "NE"
        bearing >= 67.5f && bearing < 112.5f -> "E"
        bearing >= 112.5f && bearing < 157.5f -> "SE"
        bearing >= 157.5f && bearing < 202.5f -> "S"
        bearing >= 202.5f && bearing < 247.5f -> "SW"
        bearing >= 247.5f && bearing < 292.5f -> "W"
        else -> "NW"
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = String.format("%.0f°", bearing),
            fontSize = 52.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Text(
            text = direction,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = accent,
            letterSpacing = 2.sp
        )
    }
}

@Composable
fun MinimalCard(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f - 20.dp.toPx()
            
            drawCircle(
                color = textColor.copy(alpha = 0.05f),
                radius = radius
            )
            
            rotate(degrees = -heading, pivot = center) {
                // Draw magnetic North indicator triangle
                val pointerPath = Path().apply {
                    moveTo(center.x, center.y - radius + 10.dp.toPx())
                    lineTo(center.x - 12.dp.toPx(), center.y - radius + 30.dp.toPx())
                    lineTo(center.x + 12.dp.toPx(), center.y - radius + 30.dp.toPx())
                    close()
                }
                drawPath(path = pointerPath, color = accentColor)
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun ClassicRose(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 20.dp.toPx()
            
            drawCircle(color = textColor.copy(alpha = 0.15f), radius = r, style = Stroke(width = 1.dp.toPx()))
            
            rotate(degrees = -heading, pivot = center) {
                // Compass Rose points
                val cardinalPoints = listOf(0f to "N", 90f to "E", 180f to "S", 270f to "W")
                cardinalPoints.forEach { (angle, tag) ->
                    val rad = Math.toRadians(angle.toDouble())
                    val tickLen = r - 15.dp.toPx()
                    
                    drawLine(
                        color = if (tag == "N") accentColor else textColor.copy(alpha = 0.6f),
                        start = center,
                        end = Offset(
                            (center.x + tickLen * cos(rad)).toFloat(),
                            (center.y + tickLen * sin(rad)).toFloat()
                        ),
                        strokeWidth = if (tag == "N") 3.dp.toPx() else 1.5.dp.toPx()
                    )
                }
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun RadarSweeper(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        val sweepAngle by rememberInfiniteTransition(label = "").animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(3500, easing = LinearEasing)),
            label = ""
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f - 20.dp.toPx()
            
            drawCircle(color = textColor.copy(alpha = 0.08f), radius = radius)
            
            // Spinning sweeps
            rotate(degrees = sweepAngle, pivot = center) {
                drawArc(
                    brush = Brush.sweepGradient(listOf(Color.Transparent, accentColor.copy(alpha = 0.4f))),
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
            }
            
            // Small pointer reflecting North
            rotate(degrees = -heading, pivot = center) {
                drawCircle(
                    color = accentColor,
                    radius = 5.dp.toPx(),
                    center = Offset(center.x, center.y - radius + 10.dp.toPx())
                )
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun RotatingRing(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 20.dp.toPx()
            
            rotate(degrees = -heading, pivot = center) {
                for (i in 0 until 12) {
                    val angle = i * 30f
                    val rad = Math.toRadians(angle.toDouble())
                    val isMajor = i % 3 == 0
                    val col = if (isMajor) accentColor else textColor.copy(alpha = 0.3f)
                    drawLine(
                        color = col,
                        start = Offset(
                            (center.x + (r - 12.dp.toPx()) * cos(rad)).toFloat(),
                            (center.y + (r - 12.dp.toPx()) * sin(rad)).toFloat()
                        ),
                        end = Offset(
                            (center.x + r * cos(rad)).toFloat(),
                            (center.y + r * sin(rad)).toFloat()
                        ),
                        strokeWidth = if (isMajor) 2.5.dp.toPx() else 1.dp.toPx()
                    )
                }
            }
            
            // Stationary index peak pointer at 12 o'clock
            val pointerPath = Path().apply {
                moveTo(center.x, center.y - r - 5.dp.toPx())
                lineTo(center.x - 8.dp.toPx(), center.y - r - 15.dp.toPx())
                lineTo(center.x + 8.dp.toPx(), center.y - r - 15.dp.toPx())
                close()
            }
            drawPath(path = pointerPath, color = accentColor)
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun ModernOled(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 30.dp.toPx()
            
            // Sleek dynamic arc aligned with direction
            drawArc(
                color = textColor.copy(alpha = 0.1f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2, r * 2),
                style = Stroke(width = 1.dp.toPx())
            )
            
            // Highlighting quadrant angle arc
            drawArc(
                color = accentColor,
                startAngle = -90f,
                sweepAngle = -heading,
                useCenter = false,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2, r * 2),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun TacticalGps(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 15.dp.toPx()
            
            // Tactical crosshair layout
            drawLine(color = textColor.copy(alpha = 0.1f), start = Offset(center.x - r, center.y), end = Offset(center.x + r, center.y))
            drawLine(color = textColor.copy(alpha = 0.1f), start = Offset(center.x, center.y - r), end = Offset(center.x, center.y + r))
            
            // Dynamic tactical ring
            drawCircle(
                color = accentColor,
                radius = r / 2f,
                style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 15f)))
            )
            
            rotate(degrees = -heading, pivot = center) {
                drawLine(
                    color = accentColor,
                    start = center,
                    end = Offset(center.x, center.y - r),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun BubbleFloat(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 30.dp.toPx()
            
            drawCircle(color = textColor.copy(alpha = 0.05f), radius = r)
            
            // Simulated inertia balance bubble showing North orientation float
            val rad = Math.toRadians((-heading - 90f).toDouble())
            val bubbleOffset = r - 20.dp.toPx()
            val bubbleX = center.x + bubbleOffset * cos(rad).toFloat()
            val bubbleY = center.y + bubbleOffset * sin(rad).toFloat()
            
            drawCircle(color = accentColor, radius = 10.dp.toPx(), center = Offset(bubbleX, bubbleY))
            drawCircle(color = textColor, radius = 4.dp.toPx(), center = Offset(bubbleX - 2.dp.toPx(), bubbleY - 2.dp.toPx()))
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun HorizonTape(heading: Float, accentColor: Color, textColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(20.dp)
    ) {
        val bearing = (heading + 360f) % 360f
        Text(
            text = String.format("%03.0f°", bearing),
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
            val w = size.width
            val center = Offset(w / 2f, size.height / 2f)
            
            // Horizon tape ticks relative to current heading
            val scalePxPerDeg = w / 180f
            for (i in -9..9) {
                val tickHeading = (bearing - (bearing % 10f)) + (i * 10f)
                val diff = tickHeading - bearing
                val finalX = center.x + (diff * scalePxPerDeg)
                
                if (finalX in 0f..w) {
                    val active = (tickHeading % 90f == 0f)
                    val label = when (((tickHeading + 360f) % 360f).toInt()) {
                        0 -> "N"
                        90 -> "E"
                        180 -> "S"
                        270 -> "W"
                        else -> String.format("%.0f", (tickHeading + 360f) % 360f)
                    }
                    
                    drawLine(
                        color = if (active) accentColor else textColor.copy(alpha = 0.4f),
                        start = Offset(finalX, 0f),
                        end = Offset(finalX, if (active) 15.dp.toPx() else 8.dp.toPx()),
                        strokeWidth = if (active) 2.5.dp.toPx() else 1.dp.toPx()
                    )
                }
            }
            
            // Target needle pin
            drawLine(
                color = accentColor,
                start = Offset(center.x, 0f),
                end = Offset(center.x, 30.dp.toPx()),
                strokeWidth = 3.dp.toPx()
            )
        }
    }
}

@Composable
fun FuturisticAero(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 30.dp.toPx()
            
            // Circle with telemetry dash marks
            drawCircle(
                color = textColor.copy(alpha = 0.05f),
                radius = r
            )
            
            rotate(degrees = -heading, pivot = center) {
                // Crosshairs
                drawLine(color = accentColor.copy(alpha = 0.15f), start = Offset(center.x - r, center.y), end = Offset(center.x + r, center.y))
                drawLine(color = accentColor.copy(alpha = 0.15f), start = Offset(center.x, center.y - r), end = Offset(center.x, center.y + r))
                
                // Vector marks
                val arrowY = center.y - r
                val capPath = Path().apply {
                    moveTo(center.x, arrowY)
                    lineTo(center.x - 10.dp.toPx(), arrowY + 12.dp.toPx())
                    lineTo(center.x + 10.dp.toPx(), arrowY + 12.dp.toPx())
                    close()
                }
                drawPath(path = capPath, color = accentColor)
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun DotMatrixCompass(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f - 20.dp.toPx()
            
            // LED ticks
            for (dot in 0 until 36) {
                val angle = dot * 10f
                val dotRad = Math.toRadians(angle.toDouble())
                
                // North dots light up with accent color
                val isNorthSect = (angle - heading + 360f) % 360f in 340f..360f || (angle - heading + 360f) % 360f in 0f..20f
                val col = if (isNorthSect) accentColor else textColor.copy(alpha = 0.1f)
                
                drawCircle(
                    color = col,
                    radius = if (isNorthSect) 4.dp.toPx() else 2.dp.toPx(),
                    center = Offset(
                        (center.x + radius * cos(dotRad)).toFloat(),
                        (center.y + radius * sin(dotRad)).toFloat()
                    )
                )
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun GlassOrb(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 25.dp.toPx()
            
            // Glass backing
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(textColor.copy(alpha = 0.08f), Color.Transparent),
                    center = center,
                    radius = r
                ),
                radius = r
            )
            
            // Rotating glossy orbital dot
            val rad = Math.toRadians((-heading - 90f).toDouble())
            val dotX = center.x + r * cos(rad).toFloat()
            val dotY = center.y + r * sin(rad).toFloat()
            
            drawCircle(
                color = accentColor,
                radius = 8.dp.toPx(),
                center = Offset(dotX, dotY)
            )
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun SteampunkBrass(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 30.dp.toPx()
            
            // Solid mechanical ring
            drawCircle(
                color = accentColor.copy(alpha = 0.4f),
                radius = r,
                style = Stroke(width = 4.dp.toPx())
            )
            
            rotate(degrees = -heading, pivot = center) {
                // Ticking mechanical gear teeth
                for (side in 0 until 12) {
                    val angle = side * 30f
                    val rad = Math.toRadians(angle.toDouble())
                    drawLine(
                        color = accentColor,
                        start = Offset(
                            (center.x + (r - 12.dp.toPx()) * cos(rad)).toFloat(),
                            (center.y + (r - 12.dp.toPx()) * sin(rad)).toFloat()
                        ),
                        end = Offset(
                            (center.x + r * cos(rad)).toFloat(),
                            (center.y + r * sin(rad)).toFloat()
                        ),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun SplitDial(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r1 = size.minDimension / 2f - 15.dp.toPx()
            val r2 = size.minDimension / 2f - 35.dp.toPx()
            
            // Outer cw ring
            rotate(degrees = -heading, pivot = center) {
                drawCircle(
                    color = accentColor.copy(alpha = 0.3f),
                    radius = r1,
                    style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 20f)))
                )
            }
            // Inner ccw ring
            rotate(degrees = heading, pivot = center) {
                drawCircle(
                    color = textColor.copy(alpha = 0.2f),
                    radius = r2,
                    style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 15f)))
                )
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun DigitalScope(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 20.dp.toPx()
            
            drawCircle(
                color = textColor.copy(alpha = 0.15f),
                radius = r,
                style = Stroke(width = 1.dp.toPx())
            )
            
            drawRect(
                color = accentColor.copy(alpha = 0.05f),
                topLeft = Offset(center.x - 10.dp.toPx(), center.y - r),
                size = Size(20.dp.toPx(), r * 2)
            )
            
            rotate(degrees = -heading, pivot = center) {
                drawLine(color = accentColor, start = Offset(center.x, center.y - r), end = Offset(center.x, center.y - r + 20.dp.toPx()), strokeWidth = 2.5.dp.toPx())
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun DualNeedleCompass(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 20.dp.toPx()
            
            drawCircle(color = textColor.copy(alpha = 0.05f), radius = r)
            
            rotate(degrees = -heading, pivot = center) {
                // North needle (red/accent)
                drawLine(
                    color = accentColor,
                    start = center,
                    end = Offset(center.x, center.y - r + 10.dp.toPx()),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // South needle (hollow/semi-dark)
                drawLine(
                    color = textColor.copy(alpha = 0.3f),
                    start = center,
                    end = Offset(center.x, center.y + r - 10.dp.toPx()),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            drawCircle(color = accentColor, radius = 5.dp.toPx(), center = center)
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun PolyGlow(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseR = size.minDimension / 2f - 30.dp.toPx()
            
            rotate(degrees = -heading, pivot = center) {
                val polyPath = Path()
                val points = 5
                for (p in 0 until points) {
                    val angle = p * 360f / points - 90f
                    val rad = Math.toRadians(angle.toDouble())
                    val r = baseR * (if (p == 0) 1.2f else 0.8f)
                    val pX = center.x + r * cos(rad).toFloat()
                    val pY = center.y + r * sin(rad).toFloat()
                    if (p == 0) polyPath.moveTo(pX, pY) else polyPath.lineTo(pX, pY)
                }
                polyPath.close()
                drawPath(path = polyPath, color = accentColor.copy(alpha = 0.15f))
                drawPath(path = polyPath, color = accentColor, style = Stroke(width = 2.dp.toPx()))
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun CyberRing(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 25.dp.toPx()
            
            drawArc(
                color = textColor.copy(alpha = 0.05f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2, r * 2),
                style = Stroke(width = 12.dp.toPx())
            )
            
            rotate(degrees = -heading, pivot = center) {
                drawArc(
                    color = accentColor,
                    startAngle = -105f,
                    sweepAngle = 30f,
                    useCenter = false,
                    topLeft = Offset(center.x - r, center.y - r),
                    size = Size(r * 2, r * 2),
                    style = Stroke(width = 12.dp.toPx())
                )
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun AbstractFocus(heading: Float, accentColor: Color, textColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
    ) {
        val bearing = (heading + 360f) % 360f
        val verbose = when {
            bearing >= 337.5f || bearing < 22.5f -> "MAGNETIC NORTH"
            bearing >= 22.5f && bearing < 67.5f -> "NORTHEAST DIRECTION"
            bearing >= 67.5f && bearing < 112.5f -> "EASTWARD BOUND"
            bearing >= 112.5f && bearing < 157.5f -> "SOUTHEAST QUADRANT"
            bearing >= 157.5f && bearing < 202.5f -> "MAGNETIC SOUTH"
            bearing >= 202.5f && bearing < 247.5f -> "SOUTHWEST QUADRANT"
            bearing >= 247.5f && bearing < 292.5f -> "WESTWARD BOUND"
            else -> "NORTHWEST DIRECTION"
        }
        Text(
            text = verbose,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            letterSpacing = 4.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = String.format("%.1f°", bearing),
            fontSize = 72.sp,
            fontWeight = FontWeight.Black,
            color = textColor
        )
        Text(
            text = "AZIMUTH TELEMETRY",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor.copy(alpha = 0.3f),
            letterSpacing = 2.sp
        )
    }
}

@Composable
fun LiquidOrbit(heading: Float, accentColor: Color, textColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val shift by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Reverse),
        label = ""
    )
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 30.dp.toPx()
            
            val liquidPath = Path()
            val steps = 24
            for (step in 0..steps) {
                val angle = step * 360f / steps
                val rad = Math.toRadians((angle - heading - 90f).toDouble())
                // expand North quadrant bubble
                val factor = 1f + (sin(rad + shift).toFloat() * 0.12f)
                val dr = r * factor
                val pX = center.x + dr * cos(rad).toFloat()
                val pY = center.y + dr * sin(rad).toFloat()
                if (step == 0) liquidPath.moveTo(pX, pY) else liquidPath.lineTo(pX, pY)
            }
            liquidPath.close()
            drawPath(path = liquidPath, color = accentColor.copy(alpha = 0.15f))
            drawPath(path = liquidPath, color = accentColor, style = Stroke(width = 2.dp.toPx()))
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun Constellation(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 25.dp.toPx()
            
            // Constellation outline stars
            rotate(degrees = -heading, pivot = center) {
                val pointingAngles = listOf(0f, 135f, 180f, 225f)
                pointingAngles.forEach { angle ->
                    val rad = Math.toRadians((angle - 90f).toDouble())
                    val dist = if (angle == 0f) r else r * 0.6f
                    val sx = center.x + dist * cos(rad).toFloat()
                    val sy = center.y + dist * sin(rad).toFloat()
                    
                    drawCircle(color = accentColor, radius = 4.dp.toPx(), center = Offset(sx, sy))
                    drawLine(color = accentColor.copy(alpha = 0.2f), start = center, end = Offset(sx, sy))
                }
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun SonicRadar(heading: Float, accentColor: Color, textColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing)),
        label = ""
    )
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseR = size.minDimension / 2f - 15.dp.toPx()
            
            drawCircle(
                color = accentColor.copy(alpha = 1f - progress),
                radius = baseR * progress,
                style = Stroke(width = 2.dp.toPx())
            )
            
            rotate(degrees = -heading, pivot = center) {
                drawLine(color = accentColor, start = center, end = Offset(center.x, center.y - baseR), strokeWidth = 2.5.dp.toPx())
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun HexaPulse(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseR = size.minDimension / 2f - 30.dp.toPx()
            
            rotate(degrees = -heading, pivot = center) {
                val hexPath = Path()
                for (s in 0..5) {
                    val angle = s * 60f + 30f
                    val rad = Math.toRadians(angle.toDouble())
                    val ratio = if (s == 0 || s == 1) 1.2f else 0.8f
                    val px = center.x + baseR * ratio * cos(rad).toFloat()
                    val py = center.y + baseR * ratio * sin(rad).toFloat()
                    if (s == 0) hexPath.moveTo(px, py) else hexPath.lineTo(px, py)
                }
                hexPath.close()
                drawPath(path = hexPath, color = accentColor.copy(alpha = 0.12f))
                drawPath(path = hexPath, color = accentColor, style = Stroke(width = 1.5.dp.toPx()))
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun RetroTape(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 30.dp.toPx()
            
            drawCircle(color = textColor.copy(alpha = 0.05f), radius = r)
            
            rotate(degrees = -heading, pivot = center) {
                // North/East/South/West retro lines
                for (angle in listOf(0f, 90f, 180f, 270f)) {
                    val rad = Math.toRadians((angle - 90f).toDouble())
                    drawLine(
                        color = if (angle == 0f) accentColor else textColor.copy(alpha = 0.5f),
                        start = Offset(
                            (center.x + (r - 20.dp.toPx()) * cos(rad)).toFloat(),
                            (center.y + (r - 20.dp.toPx()) * sin(rad)).toFloat()
                        ),
                        end = Offset(
                            (center.x + r * cos(rad)).toFloat(),
                            (center.y + r * sin(rad)).toFloat()
                        ),
                        strokeWidth = if (angle == 0f) 4.dp.toPx() else 2.dp.toPx()
                    )
                }
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun MinimalCross(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 20.dp.toPx()
            
            rotate(degrees = -heading, pivot = center) {
                drawLine(color = accentColor, start = Offset(center.x, center.y - r), end = Offset(center.x, center.y + r), strokeWidth = 1.dp.toPx())
                drawLine(color = textColor.copy(alpha = 0.15f), start = Offset(center.x - r, center.y), end = Offset(center.x + r, center.y), strokeWidth = 1.dp.toPx())
                
                drawCircle(color = accentColor, radius = 5.dp.toPx(), center = Offset(center.x, center.y - r))
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun AuraCompass(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 20.dp.toPx()
            
            val quadrantColor = when {
                heading < 90f -> accentColor
                heading < 180f -> Color.Cyan
                heading < 270f -> Color.Magenta
                else -> Color.Yellow
            }
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(quadrantColor.copy(alpha = 0.25f), Color.Transparent),
                    center = center,
                    radius = r
                ),
                radius = r
            )
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun GridMap(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 25.dp.toPx()
            
            drawCircle(color = textColor.copy(alpha = 0.05f), radius = r)
            
            rotate(degrees = -heading, pivot = center) {
                // Diagonal topo gridlines rotating
                for (i in -2..2) {
                    drawLine(
                        color = textColor.copy(alpha = 0.08f),
                        start = Offset(center.x - r, center.y + i * 30.dp.toPx()),
                        end = Offset(center.x + r, center.y + i * 30.dp.toPx())
                    )
                }
            }
            
            // Stationary arrow
            val pPath = Path().apply {
                moveTo(center.x, center.y - r)
                lineTo(center.x - 8.dp.toPx(), center.y - r + 15.dp.toPx())
                lineTo(center.x + 8.dp.toPx(), center.y - r + 15.dp.toPx())
                close()
            }
            drawPath(path = pPath, color = accentColor)
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun SolarSystem(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r1 = size.minDimension / 2f - 15.dp.toPx()
            val r2 = size.minDimension / 2f - 35.dp.toPx()
            
            drawCircle(color = textColor.copy(alpha = 0.05f), radius = r1, style = Stroke(width = 1.dp.toPx()))
            drawCircle(color = textColor.copy(alpha = 0.05f), radius = r2, style = Stroke(width = 1.dp.toPx()))
            
            rotate(degrees = -heading, pivot = center) {
                val rad = Math.toRadians(-90.0)
                drawCircle(color = accentColor, radius = 6.dp.toPx(), center = Offset((center.x + r1 * cos(rad)).toFloat(), (center.y + r1 * sin(rad)).toFloat()))
                drawCircle(color = textColor.copy(alpha = 0.4f), radius = 4.dp.toPx(), center = Offset((center.x + r2 * cos(rad)).toFloat(), (center.y + r2 * sin(rad)).toFloat()))
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun MatrixRainHeading(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        val shift by rememberInfiniteTransition(label = "").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
            label = ""
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 30.dp.toPx()
            
            rotate(degrees = -heading, pivot = center) {
                val flowY = center.y - r + (shift * r * 2f)
                drawLine(color = accentColor.copy(alpha = 0.25f), start = Offset(center.x, center.y - r), end = Offset(center.x, center.y + r), strokeWidth = 1.dp.toPx())
                drawCircle(color = accentColor, radius = 5.dp.toPx(), center = Offset(center.x, flowY))
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun SilhouetteHorizon(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 25.dp.toPx()
            
            drawCircle(color = textColor.copy(alpha = 0.05f), radius = r)
            
            rotate(degrees = -heading, pivot = center) {
                // Vector background mountain peaks
                val peakPath = Path().apply {
                    moveTo(center.x - 30.dp.toPx(), center.y + 15.dp.toPx())
                    lineTo(center.x - 10.dp.toPx(), center.y - 10.dp.toPx())
                    lineTo(center.x + 10.dp.toPx(), center.y + 5.dp.toPx())
                    lineTo(center.x + 30.dp.toPx(), center.y - 15.dp.toPx())
                    lineTo(center.x + 50.dp.toPx(), center.y + 15.dp.toPx())
                }
                drawPath(path = peakPath, color = accentColor, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}

@Composable
fun TeslaCompass(heading: Float, accentColor: Color, textColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 20.dp.toPx()
            
            drawCircle(color = textColor.copy(alpha = 0.05f), radius = r)
            
            rotate(degrees = -heading, pivot = center) {
                // Sparking electrical path towards N
                val sparkPath = Path().apply {
                    moveTo(center.x, center.y)
                    lineTo(center.x - 10.dp.toPx(), center.y - r / 3f)
                    lineTo(center.x + 10.dp.toPx(), center.y - 2f * r / 3f)
                    lineTo(center.x, center.y - r)
                }
                drawPath(path = sparkPath, color = accentColor, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
            }
        }
        CompassTextCentered(heading, textColor, accentColor)
    }
}
