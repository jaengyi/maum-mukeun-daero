package com.mmd.core.simulation

import java.time.DayOfWeek

/**
 * 시뮬레이터 출력. 도메인 모델(WeeklyPlan/Milestone 등)과 분리된 *Draft 타입.
 * data 레이어에서 도메인 모델로 매핑하면서 summary 텍스트 등을 생성한다.
 */
data class SimulationResult(
    val totalWeeks: Int,
    val weeklyPlans: List<WeeklyPlanDraft>,
    val expectedMilestones: List<MilestoneDraft>,
    val notes: List<String>,
)

data class WeeklyPlanDraft(
    val weekNumber: Int,
    val phase: TrainingPhase,
    val targetMaxReps: Int,                 // 이 주에 도달할 풀업 1회 가능 추정치
    val totalVolume: Int,                   // 주간 총 환산 횟수
    val dailyTasks: List<DailyTaskDraft>,
)

data class DailyTaskDraft(
    val dayOfWeek: DayOfWeek,
    val dayType: DayType,
    val intensity: Intensity,
    val executions: List<TaskExecutionDraft>,
)

data class TaskExecutionDraft(
    val exerciseType: ExerciseType,
    val targetSets: Int,
    val targetReps: Int,                    // DEAD_HANG의 경우 초
    val restSeconds: Int,
)

data class MilestoneDraft(
    val code: String,                       // "FIRST_PULLUP", "PULLUP_5", "PULLUP_10" 등
    val expectedWeek: Int,
)
