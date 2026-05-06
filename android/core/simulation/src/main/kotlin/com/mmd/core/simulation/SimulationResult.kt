package com.mmd.core.simulation

/**
 * 시뮬레이터 출력. data 레이어에서 도메인 모델(WeeklyPlan/DailyTask/TaskExecution/Milestone)로 매핑.
 */
data class SimulationResult(
    val totalWeeks: Int,
    val weeklyPlans: List<SimulatedWeeklyPlan>,
    val expectedMilestones: List<SimulatedMilestone>,
)

data class SimulatedWeeklyPlan(
    val weekNumber: Int,
    val phase: TrainingPhase,
    val targetMaxReps: Int,
    val totalVolume: Int,
    val dailyTasks: List<SimulatedDailyTask>,
)

data class SimulatedDailyTask(
    val dayOffset: Int,           // 주차 시작일로부터 0..6
    val dayType: DayType,
    val intensity: Intensity,
    val summary: String,
    val executions: List<SimulatedExecution>,
)

data class SimulatedExecution(
    val exerciseType: ExerciseType,
    val targetSets: Int,
    val targetReps: Int,           // DEAD_HANG의 경우 초
    val restSeconds: Int,
    val orderInTask: Int,
)

data class SimulatedMilestone(
    val code: String,              // "FIRST_PULLUP", "PULLUP_5", "PULLUP_10" 등
    val expectedWeek: Int,
)
