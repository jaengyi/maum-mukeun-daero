package com.mmd.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_task",
    foreignKeys = [
        ForeignKey(
            entity = WeeklyPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["weeklyPlanId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("weeklyPlanId"), Index(value = ["date"], unique = true)],
)
data class DailyTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weeklyPlanId: Long,
    val date: String,              // YYYY-MM-DD (UNIQUE)
    val dayType: String,           // "WORKOUT" / "REST"
    val intensityLevel: String,    // "LIGHT" / "MODERATE" / "HARD" / "REST"
    val summary: String,
    val isCompleted: Boolean,
    val completedAt: Long?,
)
