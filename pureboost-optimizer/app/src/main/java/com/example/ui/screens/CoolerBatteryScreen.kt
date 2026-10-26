package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.RunningAppItem
import com.example.ui.PureBoostUiState
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.CrimsonDanger
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OledCardBorder
import com.example.ui.theme.OledSurface
import com.example.ui.theme.TextSecondary

@Composable
fun CoolerBatteryScreen(
    uiState: PureBoostUiState,
    onToggleAppSelection: (String) -> Unit,
    onCoolDownNow: () -> Unit,
    onRefreshApps: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val telemetry = uiState.telemetry
    val selectedApps = uiState.runningApps.filter { it.isSelected }
    val selectedRamMb = selectedApps.sumOf { it.memoryUsedMb }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("cooler_battery_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Thermal & Battery Diagnostic Header Card
        item {
            BatteryThermalDiagnosticCard(
                tempCelsius = telemetry.batteryTempCelsius,
                batteryLevel = telemetry.batteryLevel,
                voltageMv = telemetry.batteryVoltageMv,
                isCharging = telemetry.isCharging,
                healthVi = telemetry.batteryHealthVi,
                estimatedHours = telemetry.estimatedHoursRemaining,
                selectedAppsCount = selectedApps.size,
                selectedRamMb = selectedRamMb,
                isCoolingDown = uiState.isCoolingDown,
                onCoolDownNow = onCoolDownNow,
                onRefreshApps = onRefreshApps
            )
        }

        // 2. Section Header for Background Draining Apps
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BatteryAlert,
                        contentDescription = stringResource(R.string.background_apps_title),
                        tint = AmberWarning,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.background_apps_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "${uiState.runningApps.size} đang chạy",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldBright
                )
            }
        }

        // 3. Empty State or List of Background Apps
        if (uiState.runningApps.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("all_apps_cooled_card"),
                    colors = CardDefaults.cardColors(containerColor = OledSurface),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.45f)),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Đã đóng toàn bộ ứng dụng ngầm",
                            tint = EmeraldBright,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Không Có Ứng Dụng Ngầm Gây Hao Pin!",
                            style = MaterialTheme.typography.titleMedium,
                            color = EmeraldBright
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Toàn bộ tiến trình ngầm tiêu tốn năng lượng đã được đóng. Điện thoại đang ở trạng thái mát mẻ và tiết kiệm pin tối đa.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(uiState.runningApps, key = { it.packageName }) { appItem ->
                RunningAppDrainCard(
                    app = appItem,
                    onToggleSelect = { onToggleAppSelection(appItem.packageName) },
                    onOpenSystemInfo = {
                        try {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", appItem.packageName, null)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun BatteryThermalDiagnosticCard(
    tempCelsius: Float,
    batteryLevel: Int,
    voltageMv: Int,
    isCharging: Boolean,
    healthVi: String,
    estimatedHours: Float,
    selectedAppsCount: Int,
    selectedRamMb: Int,
    isCoolingDown: Boolean,
    onCoolDownNow: () -> Unit,
    onRefreshApps: () -> Unit
) {
    val isWarm = tempCelsius >= 38.0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("battery_thermal_card"),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "NHIỆT ĐỘ CPU / PIN HIỆN TẠI",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isWarm) AmberWarning else CyberCyan
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${String.format("%.1f", tempCelsius)}°C",
                        style = MaterialTheme.typography.displayMedium,
                        color = if (isWarm) AmberWarning else EmeraldBright
                    )
                    Text(
                        text = healthVi,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(if (isWarm) AmberContainer else EmeraldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isWarm) Icons.Default.Thermostat else Icons.Default.AcUnit,
                        contentDescription = "Trạng thái nhiệt độ",
                        tint = if (isWarm) AmberWarning else EmeraldBright,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Telemetry Pills: Dung lượng Pin, Điện áp, Thời gian ước tính
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BatteryStatPill(
                    label = if (isCharging) "Đang sạc" else "Mức Pin",
                    value = "$batteryLevel%",
                    color = EmeraldBright,
                    modifier = Modifier.weight(1f)
                )
                BatteryStatPill(
                    label = "Điện áp",
                    value = "$voltageMv mV",
                    color = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
                BatteryStatPill(
                    label = "Thời lượng",
                    value = "~${String.format("%.1f", estimatedHours)} giờ",
                    color = EmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onCoolDownNow,
                    enabled = selectedAppsCount > 0 && !isCoolingDown,
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp)
                        .testTag("cool_down_now_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color(0xFF021C13)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AcUnit,
                        contentDescription = stringResource(R.string.btn_cool_down),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCoolingDown) {
                            "Đang Hạ Nhiệt…"
                        } else if (selectedAppsCount > 0) {
                            "Đóng $selectedAppsCount App (-${selectedRamMb}MB)"
                        } else {
                            "Máy Đã Mát"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onRefreshApps,
                    modifier = Modifier
                        .weight(0.9f)
                        .height(48.dp)
                        .testTag("refresh_running_apps_button"),
                    border = BorderStroke(1.dp, OledCardBorder),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Quét lại ứng dụng ngầm",
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Quét Lại",
                        style = MaterialTheme.typography.labelLarge,
                        color = CyberCyan
                    )
                }
            }
        }
    }
}

@Composable
private fun BatteryStatPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF060D0B),
        border = BorderStroke(1.dp, OledCardBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RunningAppDrainCard(
    app: RunningAppItem,
    onToggleSelect: () -> Unit,
    onOpenSystemInfo: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onToggleSelect)
            .testTag("running_app_card_${app.packageName}"),
        colors = CardDefaults.cardColors(containerColor = OledSurface),
        border = BorderStroke(
            1.dp,
            if (app.isHighDrain) AmberWarning.copy(alpha = 0.4f) else OledCardBorder
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = app.isSelected,
                onCheckedChange = { onToggleSelect() },
                colors = CheckboxDefaults.colors(
                    checkedColor = EmeraldPrimary,
                    checkmarkColor = Color(0xFF021C13)
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (app.isHighDrain) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = "Cảnh báo hao pin",
                            tint = AmberWarning,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${app.batteryDrainTagVi} • ${app.thermalRiskVi}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (app.isHighDrain) AmberWarning else TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = if (app.isHighDrain) CrimsonContainer else EmeraldContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${app.memoryUsedMb} MB RAM",
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (app.isHighDrain) CrimsonDanger else EmeraldBright
                        )
                    }
                    Surface(
                        color = Color(0xFF0B2228),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "CPU ${String.format("%.1f", app.cpuUsagePercent)}%",
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan
                        )
                    }
                }
            }

            IconButton(
                onClick = onOpenSystemInfo,
                modifier = Modifier.testTag("open_app_settings_${app.packageName}")
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Mở cài đặt hệ thống để buộc dừng sâu",
                    tint = TextSecondary
                )
            }
        }
    }
}
