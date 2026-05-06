package com.mmd.core.domain.mapper

import com.mmd.core.domain.model.DailyTask
import com.mmd.core.domain.model.TaskExecution
import com.mmd.core.domain.model.WeeklyPlan
import com.mmd.core.simulation.DailyTaskDraft
import com.mmd.core.simulation.DayType
import com.mmd.core.simulation.ExerciseType
import com.mmd.core.simulation.SimulationResult
import com.mmd.core.simulation.TaskExecutionDraft
import java.time.LocalDate

/**
 * 시뮬레이터 출력(SimulationResult)을 도메인 모델 트리(List<WeeklyPlan>)로 변환.
 *
 * - 날짜는 [planStartDate]를 week 1의 시작일(월요일 가정)로 두고 계산.
 * - DailyTask.summary는 한국어 텍스트로 생성. (Phase 6에서 res/strings로 이전 가능)
 * - 모든 도메인 모델의 id는 0 (Room이 auto-generate).
 *
 * @param planStartDate week 1이 시작하는 날짜. 월요일이라 가정.
 */
fun SimulationResult.toDomainPlans(planStartDate: LocalDate): List<WeeklyPlan> =
    weeklyPlans.map { weeklyDraft ->
        val weekStart = planStartDate.plusWeeks((weeklyDraft.weekNumber - 1).toLong())
        val weekEnd = weekStart.plusDays(6)

        val dailyTasks = weeklyDraft.dailyTasks.map { dailyDraft ->
            // weekStart가 월요일이라 가정 → DayOfWeek.value (1=MON, 7=SUN)을 0..6 오프셋으로 변환
            val date = weekStart.plusDays((dailyDraft.dayOfWeek.value - 1).toLong())

            DailyTask(
                id = 0,
                date = date,
                dayType = dailyDraft.dayType,
                intensity = dailyDraft.intensity,
                summary = generateSummary(dailyDraft),
                executions = dailyDraft.executions.map { it.toDomain() },
                isCompleted = false,
            )
        }

        WeeklyPlan(
            id = 0,
            weekNumber = weeklyDraft.weekNumber,
            startDate = weekStart,
            endDate = weekEnd,
            phase = weeklyDraft.phase,
            targetMaxReps = weeklyDraft.targetMaxReps,
            totalVolume = weeklyDraft.totalVolume,
            dailyTasks = dailyTasks,
        )
    }

private fun TaskExecutionDraft.toDomain(): TaskExecution = TaskExecution(
    id = 0,
    exerciseType = exerciseType,
    targetSets = targetSets,
    targetReps = targetReps,
    restSeconds = restSeconds,
    records = emptyList(),
)

private fun generateSummary(draft: DailyTaskDraft): String {
    if (draft.dayType == DayType.REST || draft.executions.isEmpty()) return "휴식"
    return draft.executions.joinToString(", ") { exec ->
        val name = exerciseDisplayName(exec.exerciseType)
        val unit = if (exec.exerciseType == ExerciseType.DEAD_HANG) "초" else "회"
        "$name ${exec.targetReps}$unit×${exec.targetSets}"
    }
}

private fun exerciseDisplayName(type: ExerciseType): String = when (type) {
    ExerciseType.PULLUP -> "풀업"
    ExerciseType.ASSISTED_PULLUP -> "어시스트 풀업"
    ExerciseType.NEGATIVE -> "네거티브"
    ExerciseType.AUSTRALIAN_PULLUP -> "호주 풀업"
    ExerciseType.DEAD_HANG -> "매달리기"
}
