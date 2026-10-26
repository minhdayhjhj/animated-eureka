package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.PerformanceMode
import com.example.ui.PureBoostUiState
import com.example.ui.components.RealTimeTelemetryChart
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OledCardBorder
import com.example.ui.theme.OledSurface
import com.example.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    uiState: PureBoostUiState,
    onBoostNow: () -> Unit,
    onSelectMode: (PerformanceMode) -> Unit,
    onNavigateToTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val telemetry = uiState.telemetry
    val uncleanedJunkMb = uiState.junkCategories
        .filter { !it.isCleaned }
        .sumOf { it.sizeMb.toDouble() }
        .toFloat()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero OLED Cyber Core Card + System Health Gauge
        item {
            HeroHealthCenterCard(
                healthScore = telemetry.healthScore,
                ramUsedMb = telemetry.ramUsedMb,
                ramTotalMb = telemetry.ramTotalMb,
                ramPercent = telemetry.ramUsagePercent,
                tempCelsius = telemetry.batteryTempCelsius,
                currentMode = uiState.performanceMode,
                isBoosting = uiState.isBoosting,
                boostProgressText = uiState.boostProgressTextVi,
                onBoostClick = onBoostNow
            )
        }

        // 2. Three Manual Performance Modes Switcher: Tiết kiệm, Cân bằng, Max Mode
        item {
            PerformanceModeSelectorCard(
                selectedMode = uiState.performanceMode,
                onSelectMode = onSelectMode
            )
        }

        // 3. Real-Time Telemetry Chart (RAM %, CPU Load %, Battery Temp °C)
        item {
            RealTimeTelemetryChart(
                history = uiState.telemetryHistory,
                currentRamPercent = telemetry.ramUsagePercent,
                currentCpuPercent = telemetry.cpuLoadPercent,
                currentTempCelsius = telemetry.batteryTempCelsius
            )
        }

        // 4. Quick Telemetry & Action Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickDiagnosticCard(
                        title = "Dọn Rác & Cache",
                        value = if (uncleanedJunkMb > 1f) {
                            "${String.format("%.0f", uncleanedJunkMb)} MB"
                        } else {
                            "Sạch 100%"
                        },
                        subtitle = if (uncleanedJunkMb > 1f) "Chạm để quét & xóa sạch" else "Hệ thống sạch như mới",
                        icon = Icons.Default.CleaningServices,
                        accentColor = if (uncleanedJunkMb > 250f) AmberWarning else EmeraldPrimary,
                        onClick = { onNavigateToTab(1) },
                        testTag = "quick_card_cleaner",
                        modifier = Modifier.weight(1f)
                    )
                    QuickDiagnosticCard(
                        title = "Ứng Dụng Ngầm",
                        value = "${uiState.runningApps.size} tiến trình",
                        subtitle = "Nhiệt độ: ${String.format("%.1f", telemetry.batteryTempCelsius)}°C",
                        icon = Icons.Default.Thermostat,
                        accentColor = if (telemetry.batteryTempCelsius >= 38.5f) AmberWarning else CyberCyan,
                        onClick = { onNavigateToTab(2) },
                        testTag = "quick_card_cooler",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickDiagnosticCard(
                        title = "Pin & Điện Áp",
                        value = "${telemetry.batteryLevel}% • ${String.format("%.1f", telemetry.estimatedHoursRemaining)}h",
                        subtitle = "${telemetry.batteryVoltageMv} mV • ${telemetry.batteryHealthVi}",
                        icon = Icons.Default.BatteryChargingFull,
                        accentColor = EmeraldBright,
                        onClick = { onNavigateToTab(2) },
                        testTag = "quick_card_battery",
                        modifier = Modifier.weight(1f)
                    )
                    QuickDiagnosticCard(
                        title = "Dọn Đệm Định Kỳ",
                        value = if (uiState.autoCleanEnabled) "Mỗi ${uiState.intervalHours} giờ" else "Đang tắt",
                        subtitle = "Đã thu hồi +${uiState.totalFreedRamMb}MB RAM",
                        icon = Icons.Default.Schedule,
                        accentColor = CyberCyan,
                        onClick = { onNavigateToTab(3) },
                        testTag = "quick_card_auto_schedule",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroHealthCenterCard(
    healthScore: Int,
    ramUsedMb: Long,
    ramTotalMb: Long,
    ramPercent: Float,
    tempCelsius: Float,
    currentMode: PerformanceMode,
    isBoosting: Boolean,
    boostProgressText: String,
    onBoostClick: () -> Unit
) {
    val animatedScoreFraction by animateFloatAsState(
        targetValue = (healthScore / 100f).coerceIn(0.1f, 1f),
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "health_arc"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_health_card"),
        colors = CardDefaults.cardColors(containerColor = OledSurface),
        border = BorderStroke(1.dp, OledCardBorder),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Subtle decorative vector circuit banner at the top
            Image(
                painter = painterResource(id = R.drawable.ic_cyber_core_banner),
                contentDescription = "Biểu đồ vi mạch OLED",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Status Pill Badge
                Surface(
                    color = EmeraldContainer.copy(alpha = 0.92f),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.45f)),
                    shape = CircleShape
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Trạng thái bảo vệ",
                            tint = EmeraldBright,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentMode.badgeVi,
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldBright,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Circular Health Score Gauge + Live Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Left Metric: RAM
                    MetricColumnItem(
                        label = "BỘ NHỚ RAM",
                        primaryValue = "${ramPercent.toInt()}%",
                        subValue = "${ramUsedMb}/${ramTotalMb} MB",
                        accent = EmeraldBright
                    )

                    // Center Circular Gauge
                    Box(
                        modifier = Modifier.size(138.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokePx = 11.dp.toPx()
                            drawArc(
                                color = Color(0xFF142820),
                                startAngle = 135f,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = Stroke(width = strokePx, cap = StrokeCap.Round)
                            )
                            drawArc(
                                brush = Brush.sweepGradient(
                                    listOf(EmeraldPrimary, CyberCyan, EmeraldBright)
                                ),
                                startAngle = 135f,
                                sweepAngle = 270f * animatedScoreFraction,
                                useCenter = false,
                                style = Stroke(width = strokePx, cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$healthScore",
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (healthScore >= 88) "SẠCH NHƯ MỚI" else "CẦN TỐI ƯU",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (healthScore >= 88) EmeraldBright else AmberWarning,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Right Metric: Temperature
                    MetricColumnItem(
                        label = "NHIỆT ĐỘ MÁY",
                        primaryValue = "${String.format("%.1f", tempCelsius)}°C",
                        subValue = if (tempCelsius < 37.5f) "Mát ổn định" else "Hơi ấm",
                        accent = if (tempCelsius < 38.5f) CyberCyan else AmberWarning
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(visible = isBoosting) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = EmeraldBright,
                            trackColor = EmeraldContainer
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = boostProgressText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = EmeraldBright,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Primary One-Tap Deep Boost Button
                Button(
                    onClick = onBoostClick,
                    enabled = !isBoosting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("one_tap_boost_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color(0xFF021B12)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = stringResource(R.string.btn_boost_now),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBoosting) {
                            stringResource(R.string.btn_boosting)
                        } else {
                            stringResource(R.string.btn_boost_now)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricColumnItem(
    label: String,
    primaryValue: String,
    subValue: String,
    accent: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = primaryValue,
            style = MaterialTheme.typography.headlineLarge,
            color = accent
        )
        Text(
            text = subValue,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun PerformanceModeSelectorCard(
    selectedMode: PerformanceMode,
    onSelectMode: (PerformanceMode) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("performance_mode_card"),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Chế độ hiệu năng",
                        tint = EmeraldBright,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Chế Độ Tối Ưu & Kiểm Soát Nhiệt",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "+${selectedMode.batterySavingBonusPercent}% Pin",
                    style = MaterialTheme.typography.labelMedium,
                    color = EmeraldBright
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3 Manual Mode Buttons: Tiết kiệm, Cân bằng, Max Mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PerformanceMode.entries.forEach { mode ->
                    val isSelected = mode == selectedMode
                    val containerColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            if (mode == PerformanceMode.MAX_MODE) EmeraldPrimary else CyberCyan
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        label = "mode_bg"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFF021C13) else MaterialTheme.colorScheme.onSurface,
                        label = "mode_text"
                    )

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSelectMode(mode) }
                            .testTag("mode_button_${mode.id.lowercase()}"),
                        color = containerColor,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) EmeraldBright else OledCardBorder
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (mode) {
                                    PerformanceMode.SAVER -> Icons.Default.BatteryChargingFull
                                    PerformanceMode.BALANCED -> Icons.Default.Memory
                                    PerformanceMode.MAX_MODE -> Icons.Default.AutoFixHigh
                                },
                                contentDescription = mode.titleVi,
                                tint = textColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = mode.titleVi,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = selectedMode.descriptionVi,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun QuickDiagnosticCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = OledSurface),
        border = BorderStroke(1.dp, OledCardBorder),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}
