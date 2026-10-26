package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.local.OptimizationLogEntity
import com.example.ui.PureBoostUiState
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OledCardBorder
import com.example.ui.theme.OledSurface
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AutoScheduleScreen(
    uiState: PureBoostUiState,
    logs: List<OptimizationLogEntity>,
    onToggleAutoClean: (Boolean) -> Unit,
    onSelectIntervalHours: (Int) -> Unit,
    onRunAutoCleanNow: () -> Unit,
    onToggleBatteryAlert: (Boolean) -> Unit,
    onChangeTempThreshold: (Float) -> Unit,
    onTestBatteryAlert: () -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        onTestBatteryAlert()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("auto_schedule_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Periodic Auto Cache Cleanup Card (Fixed Schedules: 1h, 6h, 12h)
        item {
            PeriodicAutoCleanCard(
                enabled = uiState.autoCleanEnabled,
                selectedHours = uiState.intervalHours,
                lastRunTimestamp = uiState.lastAutoCleanTimestamp,
                totalFreedRamMb = uiState.totalFreedRamMb,
                totalCleanedJunkMb = uiState.totalCleanedJunkMb,
                onToggleEnabled = onToggleAutoClean,
                onSelectHours = onSelectIntervalHours,
                onRunNow = onRunAutoCleanNow
            )
        }

        // 2. Smart Battery Drain & Thermal Notification Alert Card
        item {
            SmartBatteryAlertCard(
                enabled = uiState.batteryAlertEnabled,
                tempThresholdCelsius = uiState.tempThresholdCelsius,
                onToggleEnabled = onToggleBatteryAlert,
                onChangeThreshold = onChangeTempThreshold,
                onTestAlert = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val granted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                        if (!granted) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            onTestBatteryAlert()
                        }
                    } else {
                        onTestBatteryAlert()
                    }
                }
            )
        }

        // 3. Optimization History Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = stringResource(R.string.history_logs_title),
                        tint = EmeraldBright,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.history_logs_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (logs.isNotEmpty()) {
                    IconButton(
                        onClick = onClearHistory,
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Xóa lịch sử tối ưu",
                            tint = TextSecondary
                        )
                    }
                }
            }
        }

        // 4. Persistent Room Database Log Items
        if (logs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = OledSurface),
                    border = BorderStroke(1.dp, OledCardBorder),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Chưa có nhật ký tối ưu nào. Hãy nhấn 'Tối Ưu & Tăng Tốc Ngay' hoặc 'Chạy Chu Kỳ Dọn Đệm Ngay'.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        } else {
            items(logs, key = { it.id }) { logItem ->
                OptimizationLogCard(log = logItem)
            }
        }
    }
}

@Composable
private fun PeriodicAutoCleanCard(
    enabled: Boolean,
    selectedHours: Int,
    lastRunTimestamp: Long,
    totalFreedRamMb: Long,
    totalCleanedJunkMb: Float,
    onToggleEnabled: (Boolean) -> Unit,
    onSelectHours: (Int) -> Unit,
    onRunNow: () -> Unit
) {
    val formatter = SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault())
    val lastRunFormatted = formatter.format(Date(lastRunTimestamp))
    val nextRunFormatted = formatter.format(Date(lastRunTimestamp + selectedHours * 3600_000L))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("periodic_auto_clean_card"),
        colors = CardDefaults.cardColors(containerColor = OledSurface),
        border = BorderStroke(1.dp, OledCardBorder),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = stringResource(R.string.auto_schedule_title),
                        tint = EmeraldBright,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.auto_schedule_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tự động xóa cache & giải phóng RAM theo lịch cố định",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                Switch(
                    checked = enabled,
                    onCheckedChange = onToggleEnabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF021C13),
                        checkedTrackColor = EmeraldPrimary
                    ),
                    modifier = Modifier.testTag("switch_auto_clean")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Fixed Interval Options confirmed by user: Mỗi 1 giờ, Mỗi 6 giờ, Mỗi 12 giờ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(1, 6, 12).forEach { hours ->
                    val isSelected = selectedHours == hours
                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        label = "interval_bg"
                    )
                    val fgColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFF021C13) else MaterialTheme.colorScheme.onSurface,
                        label = "interval_fg"
                    )

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectHours(hours) }
                            .testTag("interval_button_${hours}h"),
                        color = bgColor,
                        border = BorderStroke(1.dp, if (isSelected) EmeraldBright else OledCardBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Autorenew,
                                contentDescription = "Mỗi $hours giờ",
                                tint = fgColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Mỗi $hours giờ",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = fgColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF060D0B),
                border = BorderStroke(1.dp, OledCardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Lần dọn gần nhất: $lastRunFormatted",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (enabled) "Chu kỳ kế tiếp: $nextRunFormatted" else "Lịch tự động đang tạm dừng",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldBright
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "+$totalFreedRamMb MB RAM",
                            style = MaterialTheme.typography.labelMedium,
                            color = EmeraldBright,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sạch ${String.format("%.0f", totalCleanedJunkMb)} MB rác",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onRunNow,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("run_auto_clean_now_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = Color(0xFF021C13)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = stringResource(R.string.btn_run_auto_now),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.btn_run_auto_now),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SmartBatteryAlertCard(
    enabled: Boolean,
    tempThresholdCelsius: Float,
    onToggleEnabled: (Boolean) -> Unit,
    onChangeThreshold: (Float) -> Unit,
    onTestAlert: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("smart_battery_alert_card"),
        colors = CardDefaults.cardColors(containerColor = OledSurface),
        border = BorderStroke(1.dp, OledCardBorder),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = stringResource(R.string.smart_alert_title),
                        tint = AmberWarning,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.smart_alert_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Cảnh báo ngay khi ứng dụng ngầm gây sụt pin hoặc nóng máy",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                Switch(
                    checked = enabled,
                    onCheckedChange = onToggleEnabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF021C13),
                        checkedTrackColor = EmeraldPrimary
                    ),
                    modifier = Modifier.testTag("switch_battery_alert")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ngưỡng nhiệt độ kích hoạt cảnh báo:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = "${String.format("%.1f", tempThresholdCelsius)}°C",
                    style = MaterialTheme.typography.labelMedium,
                    color = AmberWarning,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = tempThresholdCelsius,
                onValueChange = onChangeThreshold,
                valueRange = 36.0f..43.0f,
                steps = 13,
                colors = SliderDefaults.colors(
                    thumbColor = AmberWarning,
                    activeTrackColor = AmberWarning
                ),
                modifier = Modifier.testTag("slider_temp_threshold")
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedButton(
                onClick = onTestAlert,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("test_battery_alert_button"),
                border = BorderStroke(1.dp, AmberWarning.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = stringResource(R.string.btn_test_alert),
                    tint = AmberWarning,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.btn_test_alert),
                    style = MaterialTheme.typography.labelLarge,
                    color = AmberWarning,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun OptimizationLogCard(log: OptimizationLogEntity) {
    val formatter = SimpleDateFormat("HH:mm:ss • dd/MM/yyyy", Locale.getDefault())
    val timeStr = formatter.format(Date(log.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = OledSurface),
        border = BorderStroke(1.dp, OledCardBorder),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = log.titleVi,
                    style = MaterialTheme.typography.titleMedium,
                    color = EmeraldBright
                )
                Surface(
                    color = EmeraldContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = timeStr,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = log.detailVi,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
