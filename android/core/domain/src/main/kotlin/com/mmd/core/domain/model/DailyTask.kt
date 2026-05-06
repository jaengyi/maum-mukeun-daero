package com.mmd.core.domain.model

import com.mmd.core.simulation.DayType
import com.mmd.core.simulation.Intensity
import java.time.LocalDate

data class DailyTask(
    val id: Long,
    val date: LocalDate,
    val dayType: DayType,
    val intensity: Intensity,
    val summary: String,
    val executions: List<TaskExecution>,
    val isCompleted: Boolean,
)
