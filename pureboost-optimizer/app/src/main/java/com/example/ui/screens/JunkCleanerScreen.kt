package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.JunkCategory
import com.example.domain.model.StorageOverview
import com.example.ui.PureBoostUiState
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OledCardBorder
import com.example.ui.theme.OledSurface
import com.example.ui.theme.TextSecondary

@Composable
fun JunkCleanerScreen(
    uiState: PureBoostUiState,
    onToggleJunkCategory: (String) -> Unit,
    onCleanSelected: () -> Unit,
    onRescan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expandedMap = remember { mutableStateMapOf<String, Boolean>() }
    val selectedJunkMb = uiState.junkCategories
        .filter { it.isSelected && !it.isCleaned }
        .sumOf { it.sizeMb.toDouble() }
        .toFloat()

    val totalUncleanedMb = uiState.junkCategories
        .filter { !it.isCleaned }
        .sumOf { it.sizeMb.toDouble() }
        .toFloat()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("junk_cleaner_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Top Junk Summary Banner & Action Bar
        item {
            JunkScannerHeaderCard(
                totalUncleanedMb = totalUncleanedMb,
                selectedJunkMb = selectedJunkMb,
                isScanning = uiState.isScanningJunk,
                isCleaning = uiState.isCleaningJunk,
                onCleanSelected = onCleanSelected,
                onRescan = onRescan
            )
        }

        // 2. Detailed Storage Breakdown by File Category
        item {
            uiState.storageOverview?.let { storage ->
                StorageCategoryBreakdownCard(storage = storage)
            }
        }

        // 3. Section Header for Deep Junk Categories
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FolderSpecial,
                        contentDescription = stringResource(R.string.junk_categories_title),
                        tint = EmeraldBright,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.junk_categories_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "${uiState.junkCategories.count { !it.isCleaned }} nhóm cần dọn",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldBright
                )
            }
        }

        // 4. Individual Expandable Junk Category Cards
        items(uiState.junkCategories, key = { it.id }) { category ->
            val isExpanded = expandedMap[category.id] ?: false
            JunkCategoryItemCard(
                category = category,
                isExpanded = isExpanded,
                onToggleCheck = { onToggleJunkCategory(category.id) },
                onToggleExpand = { expandedMap[category.id] = !isExpanded }
            )
        }
    }
}

@Composable
private fun JunkScannerHeaderCard(
    totalUncleanedMb: Float,
    selectedJunkMb: Float,
    isScanning: Boolean,
    isCleaning: Boolean,
    onCleanSelected: () -> Unit,
    onRescan: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("junk_scanner_header_card"),
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
                        text = "DUNG LƯỢNG RÁC & BỘ NHỚ ĐỆM PHÁT HIỆN",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldBright
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (totalUncleanedMb > 1f) {
                            "${String.format("%.1f", totalUncleanedMb)} MB"
                        } else {
                            "0.0 MB • Đã Sạch"
                        },
                        style = MaterialTheme.typography.displayMedium,
                        color = if (totalUncleanedMb > 200f) AmberWarning else EmeraldBright
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (totalUncleanedMb > 1f) {
                            "Đã chọn ${String.format("%.1f", selectedJunkMb)} MB để giải phóng an toàn"
                        } else {
                            "Bộ nhớ đệm, tệp tạm .tmp và APK thừa đã được dọn sạch hoàn toàn"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(EmeraldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (isScanning || isCleaning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(30.dp),
                            color = EmeraldBright,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (totalUncleanedMb > 1f) {
                                Icons.Default.DeleteSweep
                            } else {
                                Icons.Default.CheckCircle
                            },
                            contentDescription = "Biểu tượng dọn rác",
                            tint = EmeraldBright,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onCleanSelected,
                    enabled = selectedJunkMb > 0f && !isCleaning && !isScanning,
                    modifier = Modifier
                        .weight(1.4f)
                        .height(48.dp)
                        .testTag("clean_selected_junk_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color(0xFF021C13)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CleaningServices,
                        contentDescription = stringResource(R.string.btn_clean_now),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCleaning) "Đang Dọn…" else "Dọn Sạch Ngay",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onRescan,
                    enabled = !isScanning && !isCleaning,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("rescan_junk_button"),
                    border = BorderStroke(1.dp, OledCardBorder),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.btn_scan_junk),
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
private fun StorageCategoryBreakdownCard(storage: StorageOverview) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("storage_breakdown_card"),
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
                        imageVector = Icons.Default.Storage,
                        contentDescription = stringResource(R.string.storage_breakdown_title),
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.storage_breakdown_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Trống ${String.format("%.1f", storage.freeGb)} GB",
                    style = MaterialTheme.typography.labelMedium,
                    color = EmeraldBright
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Đã dùng ${String.format("%.1f", storage.usedGb)} GB / ${String.format("%.1f", storage.totalGb)} GB (${storage.usedPercent.toInt()}%)",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Multi-segment stacked storage bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF13241E))
            ) {
                storage.categories.forEach { slice ->
                    Box(
                        modifier = Modifier
                            .weight(slice.fractionOfTotal.coerceAtLeast(0.03f))
                            .fillMaxHeight()
                            .background(Color(slice.colorHex))
                    )
                }
                val freeFraction = ((storage.freeGb / storage.totalGb)).coerceIn(0.05f, 0.7f)
                Box(
                    modifier = Modifier
                        .weight(freeFraction)
                        .fillMaxHeight()
                        .background(Color(0xFF13241E))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Detailed Category Rows
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                storage.categories.forEach { slice ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(slice.colorHex))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = slice.titleVi,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${slice.subtitleVi} • ~${slice.fileCountEstimate} tệp",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${String.format("%.2f", slice.sizeGb)} GB",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(slice.colorHex),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun JunkCategoryItemCard(
    category: JunkCategory,
    isExpanded: Boolean,
    onToggleCheck: () -> Unit,
    onToggleExpand: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onToggleExpand)
            .testTag("junk_category_card_${category.id}"),
        colors = CardDefaults.cardColors(containerColor = OledSurface),
        border = BorderStroke(
            1.dp,
            if (category.isCleaned) EmeraldPrimary.copy(alpha = 0.35f) else OledCardBorder
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = category.isSelected && !category.isCleaned,
                    onCheckedChange = { onToggleCheck() },
                    enabled = !category.isCleaned,
                    colors = CheckboxDefaults.colors(
                        checkedColor = EmeraldPrimary,
                        checkmarkColor = Color(0xFF021C13)
                    ),
                    modifier = Modifier.testTag("junk_checkbox_${category.id}")
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp)
                ) {
                    Text(
                        text = category.titleVi,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = category.descriptionVi,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (category.isCleaned) {
                            "Đã sạch"
                        } else {
                            "${String.format("%.1f", category.sizeMb)} MB"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = if (category.isCleaned) EmeraldBright else AmberWarning,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Xem đường dẫn tệp",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF060D0A))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        color = EmeraldContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = category.badgeVi,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldBright
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    category.samplePaths.forEach { path ->
                        Text(
                            text = "• $path",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}
