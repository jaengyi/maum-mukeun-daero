package com.mmd.core.domain.model

import com.mmd.core.simulation.TrainingPhase
import java.time.LocalDate

data class WeeklyPlan(
    val id: Long,
    val weekNumber: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val phase: TrainingPhase,
    val targetMaxReps: Int,
    val totalVolume: Int,
    val dailyTasks: List<DailyTask>,
)
