package com.example.domain.model

enum class PerformanceMode(
    val id: String,
    val titleVi: String,
    val badgeVi: String,
    val descriptionVi: String,
    val cpuTargetFactor: Float,
    val thermalLimitCelsius: Float,
    val batterySavingBonusPercent: Int
) {
    SAVER(
        id = "SAVER",
        titleVi = "Tiết kiệm",
        badgeVi = "SIÊU TIẾT KIỆM PIN",
        descriptionVi = "Giảm xung nhịp tác vụ ngầm, đóng băng đồng bộ phụ và kéo dài tối đa thời lượng pin trên nền OLED.",
        cpuTargetFactor = 0.65f,
        thermalLimitCelsius = 36.5f,
        batterySavingBonusPercent = 28
    ),
    BALANCED(
        id = "BALANCED",
        titleVi = "Cân bằng",
        badgeVi = "CÂN BẰNG TỐI ƯU",
        descriptionVi = "Tự động điều phối RAM và luồng xử lý, giữ máy mát mẻ và ổn định cho nhu cầu sử dụng hàng ngày.",
        cpuTargetFactor = 0.82f,
        thermalLimitCelsius = 38.5f,
        batterySavingBonusPercent = 14
    ),
    MAX_MODE(
        id = "MAX_MODE",
        titleVi = "Max Mode",
        badgeVi = "MƯỢT TỐI ĐA • MÁT MÁY",
        descriptionVi = "Giải phóng sâu bộ nhớ hệ thống, ưu tiên 100% tài nguyên cho ứng dụng đang mở kết hợp lá chắn kiểm soát nhiệt chống nóng máy.",
        cpuTargetFactor = 0.58f,
        thermalLimitCelsius = 37.5f,
        batterySavingBonusPercent = 18
    )
}

data class TelemetryPoint(
    val timestampMs: Long,
    val ramUsagePercent: Float,
    val cpuLoadPercent: Float,
    val batteryTempCelsius: Float
)

data class DeviceTelemetry(
    val ramUsedMb: Long,
    val ramTotalMb: Long,
    val ramAvailableMb: Long,
    val ramUsagePercent: Float,
    val cpuLoadPercent: Float,
    val batteryLevel: Int,
    val batteryTempCelsius: Float,
    val batteryVoltageMv: Int,
    val isCharging: Boolean,
    val batteryHealthVi: String,
    val healthScore: Int,
    val estimatedHoursRemaining: Float
)

data class StorageCategorySlice(
    val id: String,
    val titleVi: String,
    val subtitleVi: String,
    val sizeGb: Float,
    val fractionOfTotal: Float,
    val fileCountEstimate: Int,
    val colorHex: Long
)

data class StorageOverview(
    val totalGb: Float,
    val usedGb: Float,
    val freeGb: Float,
    val usedPercent: Float,
    val categories: List<StorageCategorySlice>
)

data class JunkCategory(
    val id: String,
    val titleVi: String,
    val descriptionVi: String,
    val samplePaths: List<String>,
    val sizeMb: Float,
    val isSelected: Boolean = true,
    val isCleaned: Boolean = false,
    val badgeVi: String = "An toàn để xóa"
)

data class RunningAppItem(
    val packageName: String,
    val appName: String,
    val isSystemProcess: Boolean,
    val memoryUsedMb: Int,
    val cpuUsagePercent: Float,
    val batteryDrainTagVi: String,
    val thermalRiskVi: String,
    val isHighDrain: Boolean,
    val isSelected: Boolean = true
)
