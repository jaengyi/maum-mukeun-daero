package com.mmd.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "milestone",
    indices = [Index(value = ["code"], unique = true)],
)
data class MilestoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,              // "FIRST_PULLUP", "PULLUP_5", "PULLUP_10", "STREAK_7", "STREAK_30" 등
    val title: String,
    val description: String,
    val achievedAt: Long?,         // null이면 미달성
)
