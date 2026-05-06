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
    val intensityPreference: IntensityPreference = IntensityPreference.NORMAL,
) {
    init {
        require(heightCm in 100..250) { "heightCm must be in 100..250, was $heightCm" }
        require(weightKg in 30f..200f) { "weightKg must be in 30..200, was $weightKg" }
        require(age in 10..80) { "age must be in 10..80, was $age" }
        require(currentMaxPullups in 0..30) { "currentMaxPullups must be in 0..30, was $currentMaxPullups" }
        require(currentDeadHangSeconds in 0..120) { "currentDeadHangSeconds must be in 0..120, was $currentDeadHangSeconds" }
        require(availableDaysOfWeek.isNotEmpty()) { "availableDaysOfWeek must not be empty" }
        require(targetReps in 1..30) { "targetReps must be in 1..30, was $targetReps" }
    }
}
