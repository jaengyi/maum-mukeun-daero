package com.mmd.core.simulation

import java.time.DayOfWeek

data class SimulationInput(
    val heightCm: Int,
    val weightKg: Float,
    val age: Int,
    val gender: Gender,
    val currentMaxPullups: Int,
    val currentDeadHangSeconds: Int,
    val availableDaysOfWeek: Set<DayOfWeek>,
    val targetReps: Int = 10,
)
