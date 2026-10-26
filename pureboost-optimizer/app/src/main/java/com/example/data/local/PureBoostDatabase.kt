package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "optimization_logs")
data class OptimizationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: String, // BOOST, CLEAN_JUNK, COOL_DOWN, AUTO_CLEAN
    val titleVi: String,
    val detailVi: String,
    val freedRamMb: Int = 0,
    val cleanedJunkMb: Float = 0f,
    val closedAppsCount: Int = 0,
    val tempDropCelsius: Float = 0f
)

@Entity(tableName = "schedule_config")
data class ScheduleConfigEntity(
    @PrimaryKey val id: Int = 1,
    val autoCleanEnabled: Boolean = true,
    val intervalHours: Int = 6, // 1, 6, or 12 hours
    val batteryAlertEnabled: Boolean = true,
    val tempAlertThresholdCelsius: Float = 38.5f,
    val selectedModeId: String = "MAX_MODE",
    val lastAutoCleanTimestamp: Long = System.currentTimeMillis(),
    val totalFreedRamMb: Long = 0L,
    val totalCleanedJunkMb: Float = 0f
)

@Dao
interface PureBoostDao {
    @Query("SELECT * FROM optimization_logs ORDER BY timestamp DESC LIMIT 50")
    fun getAllLogs(): Flow<List<OptimizationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: OptimizationLogEntity)

    @Query("DELETE FROM optimization_logs")
    suspend fun clearAllLogs()

    @Query("SELECT * FROM schedule_config WHERE id = 1")
    fun getScheduleConfig(): Flow<ScheduleConfigEntity?>

    @Query("SELECT * FROM schedule_config WHERE id = 1")
    suspend fun getScheduleConfigOnce(): ScheduleConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertScheduleConfig(config: ScheduleConfigEntity)
}

@Database(
    entities = [OptimizationLogEntity::class, ScheduleConfigEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PureBoostDatabase : RoomDatabase() {
    abstract fun pureBoostDao(): PureBoostDao

    companion object {
        @Volatile
        private var INSTANCE: PureBoostDatabase? = null

        fun getInstance(context: Context): PureBoostDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PureBoostDatabase::class.java,
                    "pureboost_vn_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class PureBoostRepository(private val dao: PureBoostDao) {
    val logsFlow: Flow<List<OptimizationLogEntity>> = dao.getAllLogs()
    val scheduleConfigFlow: Flow<ScheduleConfigEntity?> = dao.getScheduleConfig()

    suspend fun getOrInitConfig(): ScheduleConfigEntity {
        val current = dao.getScheduleConfigOnce()
        if (current != null) return current
        val initial = ScheduleConfigEntity()
        dao.upsertScheduleConfig(initial)
        return initial
    }

    suspend fun saveConfig(config: ScheduleConfigEntity) {
        dao.upsertScheduleConfig(config)
    }

    suspend fun recordOptimization(log: OptimizationLogEntity) {
        dao.insertLog(log)
        val config = getOrInitConfig()
        dao.upsertScheduleConfig(
            config.copy(
                totalFreedRamMb = config.totalFreedRamMb + log.freedRamMb,
                totalCleanedJunkMb = config.totalCleanedJunkMb + log.cleanedJunkMb,
                lastAutoCleanTimestamp = if (log.actionType == "AUTO_CLEAN") {
                    System.currentTimeMillis()
                } else {
                    config.lastAutoCleanTimestamp
                }
            )
        )
    }

    suspend fun clearLogs() {
        dao.clearAllLogs()
    }
}
