package com.mmd.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grass_cell")
data class GrassCellEntity(
    @PrimaryKey val date: String,  // YYYY-MM-DD
    val intensityLevel: Int,       // 0..4 (잔디 색 단계)
    val totalReps: Int,
    val isWorkoutDay: Boolean,
    val isCompleted: Boolean,
    val updatedAt: Long,
)
