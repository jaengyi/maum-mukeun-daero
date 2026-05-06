package com.mmd.core.domain.model

import java.time.DayOfWeek
import java.time.Instant

data class Goal(
    val id: Long,
    val category: GoalCategory,
    val targetValue: Int,
    val initialMaxReps: Int,
    val initialDeadHangSec: Int,
    val availableDays: Set<DayOfWeek>,
    val isActive: Boolean,
    val startedAt: Instant,
    val completedAt: Instant?,
)
