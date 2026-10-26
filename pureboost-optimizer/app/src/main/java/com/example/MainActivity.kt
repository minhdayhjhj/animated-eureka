package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.PureBoostUiState
import com.example.ui.PureBoostViewModel
import com.example.ui.screens.AutoScheduleScreen
import com.example.ui.screens.CoolerBatteryScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.JunkCleanerScreen
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OledBlack
import com.example.ui.theme.OledCardBorder
import com.example.ui.theme.OledSurface
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PureBoostApp()
            }
        }
    }
}

private data class NavTabItem(
    val index: Int,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun PureBoostApp(viewModel: PureBoostViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val logs by viewModel.optimizationLogs.collectAsStateWithLifecycle()

    // Mandatory BackHandler for secondary tabs to return to Dashboard (Tab 0)
    BackHandler(enabled = uiState.selectedTab != 0) {
        viewModel.selectTab(0)
    }

    val navItems = listOf(
        NavTabItem(
            index = 0,
            labelRes = R.string.nav_dashboard,
            selectedIcon = Icons.Filled.Dashboard,
            unselectedIcon = Icons.Outlined.Dashboard,
            testTag = "nav_tab_dashboard"
        ),
        NavTabItem(
            index = 1,
            labelRes = R.string.nav_cleaner,
            selectedIcon = Icons.Filled.CleaningServices,
            unselectedIcon = Icons.Outlined.CleaningServices,
            testTag = "nav_tab_cleaner"
        ),
        NavTabItem(
            index = 2,
            labelRes = R.string.nav_cooler,
            selectedIcon = Icons.Filled.Thermostat,
            unselectedIcon = Icons.Outlined.Thermostat,
            testTag = "nav_tab_cooler"
        ),
        NavTabItem(
            index = 3,
            labelRes = R.string.nav_auto,
            selectedIcon = Icons.Filled.Schedule,
            unselectedIcon = Icons.Outlined.Schedule,
            testTag = "nav_tab_auto"
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = OledBlack,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            PureBoostTopBar(uiState = uiState)
        },
        bottomBar = {
            NavigationBar(
                containerColor = OledSurface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                windowInsets = WindowInsets.navigationBars
            ) {
                navItems.forEach { item ->
                    val selected = uiState.selectedTab == item.index
                    NavigationBarItem(
                        selected = selected,
                        onClick = { viewModel.selectTab(item.index) },
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = stringResource(item.labelRes)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(item.labelRes),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF021C13),
                            selectedTextColor = EmeraldBright,
                            indicatorColor = EmeraldPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Interactive Status Feedback Banner
            AnimatedVisibility(
                visible = uiState.statusBannerMessageVi != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                uiState.statusBannerMessageVi?.let { message ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("status_feedback_banner"),
                        color = EmeraldContainer,
                        border = BorderStroke(1.dp, EmeraldPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Thông báo trạng thái",
                                    tint = EmeraldBright,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = EmeraldBright
                                )
                            }
                            IconButton(
                                onClick = { viewModel.dismissBanner() },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Đóng thông báo",
                                    tint = EmeraldBright,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Screen Body
            when (uiState.selectedTab) {
                0 -> DashboardScreen(
                    uiState = uiState,
                    onBoostNow = { viewModel.executeDeepBoostNow() },
                    onSelectMode = { viewModel.setPerformanceMode(it) },
                    onNavigateToTab = { viewModel.selectTab(it) }
                )
                1 -> JunkCleanerScreen(
                    uiState = uiState,
                    onToggleJunkCategory = { viewModel.toggleJunkSelection(it) },
                    onCleanSelected = { viewModel.cleanSelectedJunk() },
                    onRescan = { viewModel.rescanJunkAndStorage() }
                )
                2 -> CoolerBatteryScreen(
                    uiState = uiState,
                    onToggleAppSelection = { viewModel.toggleRunningAppSelection(it) },
                    onCoolDownNow = { viewModel.coolDownAndStopSelectedApps() },
                    onRefreshApps = { viewModel.refreshRunningApps() }
                )
                3 -> AutoScheduleScreen(
                    uiState = uiState,
                    logs = logs,
                    onToggleAutoClean = { viewModel.setAutoCleanEnabled(it) },
                    onSelectIntervalHours = { viewModel.setAutoCleanIntervalHours(it) },
                    onRunAutoCleanNow = { viewModel.executePeriodicAutoClean(isManualTrigger = true) },
                    onToggleBatteryAlert = { viewModel.setBatteryAlertEnabled(it) },
                    onChangeTempThreshold = { viewModel.setTempAlertThreshold(it) },
                    onTestBatteryAlert = { viewModel.triggerBatteryDrainAlertCheck() },
                    onClearHistory = { viewModel.clearOptimizationHistory() }
                )
            }
        }
    }
}

@Composable
private fun PureBoostTopBar(uiState: PureBoostUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = OledBlack
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(EmeraldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = stringResource(R.string.app_name),
                        tint = EmeraldBright,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.app_subtitle),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            // Live Temperature & Mode Chip in Top Bar
            Surface(
                color = OledSurface,
                border = BorderStroke(1.dp, OledCardBorder),
                shape = CircleShape
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.performanceMode.titleVi,
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldBright,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${String.format("%.1f", uiState.telemetry.batteryTempCelsius)}°C",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
