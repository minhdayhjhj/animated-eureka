package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.TelemetryPoint
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OledCardBorder
import com.example.ui.theme.OledSurface
import com.example.ui.theme.TextSecondary

@Composable
fun RealTimeTelemetryChart(
    history: List<TelemetryPoint>,
    currentRamPercent: Float,
    currentCpuPercent: Float,
    currentTempCelsius: Float,
    modifier: Modifier = Modifier
) {
    // 0 = RAM & CPU Dual Curve, 1 = Nhiệt độ Pin (°C)
    var selectedChartMode by remember { mutableIntStateOf(0) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("realtime_telemetry_chart_card"),
        colors = CardDefaults.cardColors(containerColor = OledSurface),
        border = BorderStroke(1.dp, OledCardBorder),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(EmeraldBright)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BIỂU ĐỒ HIỆU NĂNG THỜI GIAN THỰC",
                            style = MaterialTheme.typography.labelMedium,
                            color = EmeraldBright
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (selectedChartMode == 0) {
                            "Biến thiên RAM (${currentRamPercent.toInt()}%) & Tải CPU (${currentCpuPercent.toInt()}%)"
                        } else {
                            "Giám sát Nhiệt độ Pin (${String.format("%.1f", currentTempCelsius)}°C)"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Mode Switcher Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ChartMetricChip(
                        label = "RAM / CPU",
                        selected = selectedChartMode == 0,
                        onClick = { selectedChartMode = 0 },
                        tag = "chart_mode_ram_cpu"
                    )
                    ChartMetricChip(
                        label = "Nhiệt độ",
                        selected = selectedChartMode == 1,
                        onClick = { selectedChartMode = 1 },
                        tag = "chart_mode_thermal"
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // High-contrast 60fps OLED Canvas Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF060C0A))
                    .padding(horizontal = 8.dp, vertical = 10.dp)
            ) {
                if (history.size >= 2) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height

                        // Horizontal subtle grid lines
                        val gridSteps = 4
                        for (i in 0..gridSteps) {
                            val y = height * (i.toFloat() / gridSteps)
                            drawLine(
                                color = Color(0xFF152922),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        if (selectedChartMode == 0) {
                            // Draw RAM Curve (0..100%) in EmeraldPrimary
                            drawSmoothMetricCurve(
                                points = history.map { it.ramUsagePercent.coerceIn(0f, 100f) / 100f },
                                lineColor = EmeraldPrimary,
                                fillTopColor = EmeraldPrimary.copy(alpha = 0.30f)
                            )
                            // Draw CPU Load Curve (0..100%) in CyberCyan
                            drawSmoothMetricCurve(
                                points = history.map { it.cpuLoadPercent.coerceIn(0f, 100f) / 100f },
                                lineColor = CyberCyan,
                                fillTopColor = CyberCyan.copy(alpha = 0.16f)
                            )
                        } else {
                            // Draw Temperature Curve mapped between 25°C and 48°C
                            val normalizedTemps = history.map {
                                ((it.batteryTempCelsius - 25f) / 23f).coerceIn(0.05f, 0.98f)
                            }
                            val lineColor = if (currentTempCelsius >= 38.5f) AmberWarning else EmeraldBright
                            drawSmoothMetricCurve(
                                points = normalizedTemps,
                                lineColor = lineColor,
                                fillTopColor = lineColor.copy(alpha = 0.28f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend & Real-time telemetry readout footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedChartMode == 0) {
                    LegendDotItem(
                        color = EmeraldPrimary,
                        label = "Bộ nhớ RAM: ${String.format("%.1f", currentRamPercent)}%"
                    )
                    LegendDotItem(
                        color = CyberCyan,
                        label = "Tải hệ thống: ${String.format("%.1f", currentCpuPercent)}%"
                    )
                } else {
                    val minTemp = history.minOfOrNull { it.batteryTempCelsius } ?: currentTempCelsius
                    val maxTemp = history.maxOfOrNull { it.batteryTempCelsius } ?: currentTempCelsius
                    LegendDotItem(
                        color = if (currentTempCelsius >= 38.5f) AmberWarning else EmeraldBright,
                        label = "Hiện tại: ${String.format("%.1f", currentTempCelsius)}°C"
                    )
                    Text(
                        text = "Thấp: ${String.format("%.1f", minTemp)}°C • Cao: ${String.format("%.1f", maxTemp)}°C",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSmoothMetricCurve(
    points: List<Float>,
    lineColor: Color,
    fillTopColor: Color
) {
    if (points.size < 2) return
    val w = size.width
    val h = size.height
    val stepX = w / (points.size - 1).coerceAtLeast(1)

    val strokePath = Path()
    val fillPath = Path()

    points.forEachIndexed { index, normalizedValue ->
        val x = index * stepX
        val y = h - (normalizedValue * h)
        if (index == 0) {
            strokePath.moveTo(x, y)
            fillPath.moveTo(x, h)
            fillPath.lineTo(x, y)
        } else {
            val prevX = (index - 1) * stepX
            val prevY = h - (points[index - 1] * h)
            val controlX = (prevX + x) / 2f
            strokePath.cubicTo(controlX, prevY, controlX, y, x, y)
            fillPath.cubicTo(controlX, prevY, controlX, y, x, y)
        }
    }

    fillPath.lineTo(w, h)
    fillPath.close()

    drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(
            colors = listOf(fillTopColor, Color.Transparent),
            startY = 0f,
            endY = h
        )
    )

    drawPath(
        path = strokePath,
        color = lineColor,
        style = Stroke(
            width = 2.6.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // Highlight latest point with a glowing dot
    val lastX = w
    val lastY = h - (points.last() * h)
    drawCircle(
        color = lineColor.copy(alpha = 0.35f),
        radius = 7.dp.toPx(),
        center = Offset(lastX, lastY)
    )
    drawCircle(
        color = lineColor,
        radius = 3.5.dp.toPx(),
        center = Offset(lastX, lastY)
    )
}

@Composable
private fun ChartMetricChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) EmeraldPrimary else Color.Transparent,
        label = "chip_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) Color(0xFF032015) else TextSecondary,
        label = "chip_fg"
    )
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(9.dp))
            .clickable(onClick = onClick)
            .testTag(tag),
        color = bgColor,
        shape = RoundedCornerShape(9.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
private fun LegendDotItem(
    color: Color,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
