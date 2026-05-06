package com.mmd.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_condition")
data class DailyConditionEntity(
    @PrimaryKey val date: String,  // YYYY-MM-DD
    val conditionScore: Int,       // 1..5
    val weightKg: Float?,
    val note: String?,
    val recordedAt: Long,
)
