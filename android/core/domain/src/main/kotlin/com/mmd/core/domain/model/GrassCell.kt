package com.mmd.core.domain.model

import java.time.LocalDate

data class GrassCell(
    val date: LocalDate,
    val intensityLevel: Int,       // 0..4 (잔디 색 단계)
    val totalReps: Int,
    val isWorkoutDay: Boolean,
    val isCompleted: Boolean,
)
