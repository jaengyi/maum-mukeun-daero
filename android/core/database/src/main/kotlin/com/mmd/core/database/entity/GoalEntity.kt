package com.mmd.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,         // "PULLUP"
    val targetValue: Int,
    val targetUnit: String,       // "REPS"
    val initialMaxReps: Int,
    val initialDeadHangSec: Int,
    val availableDays: String,    // "MON,WED,FRI"
    val isActive: Boolean,
    val startedAt: Long,
    val completedAt: Long?,
    val archivedAt: Long?,
)
