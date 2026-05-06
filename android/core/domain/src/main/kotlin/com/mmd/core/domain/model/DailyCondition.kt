package com.mmd.core.domain.model

import java.time.Instant
import java.time.LocalDate

data class DailyCondition(
    val date: LocalDate,
    val conditionScore: Int,       // 1..5
    val weightKg: Float?,
    val note: String?,
    val recordedAt: Instant,
)
