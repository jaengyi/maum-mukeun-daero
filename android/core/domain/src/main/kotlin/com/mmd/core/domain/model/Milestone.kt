package com.mmd.core.domain.model

import java.time.Instant

data class Milestone(
    val id: Long,
    val code: String,              // "FIRST_PULLUP", "PULLUP_5", "PULLUP_10", "STREAK_7", "STREAK_30" 등
    val title: String,
    val description: String,
    val achievedAt: Instant?,
)
