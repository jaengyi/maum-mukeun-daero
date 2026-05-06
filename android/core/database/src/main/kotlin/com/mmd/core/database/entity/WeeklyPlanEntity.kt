package com.mmd.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weekly_plan",
    foreignKeys = [
        ForeignKey(
            entity = GoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("goalId"), Index("startDate")],
)
data class WeeklyPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalId: Long,
    val weekNumber: Int,
    val startDate: String,         // YYYY-MM-DD
    val endDate: String,
    val phase: String,             // "FOUNDATION" / "BUILD" / "PEAK" / "MAINTAIN"
    val targetMaxReps: Int,
    val totalVolume: Int,
    val isAdjusted: Boolean,
    val notes: String?,
)
