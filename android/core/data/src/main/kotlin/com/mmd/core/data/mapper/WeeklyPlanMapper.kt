package com.mmd.core.data.mapper

import com.mmd.core.database.entity.WeeklyPlanEntity
import com.mmd.core.domain.model.DailyTask
import com.mmd.core.domain.model.WeeklyPlan
import com.mmd.core.simulation.TrainingPhase
import java.time.LocalDate

/**
 * 도메인 WeeklyPlan은 dailyTasks를 포함하지만 entity는 별도 테이블. 호출부에서 자식 리스트 주입.
 */
internal fun WeeklyPlanEntity.toDomain(dailyTasks: List<DailyTask> = emptyList()): WeeklyPlan = WeeklyPlan(
    id = id,
    weekNumber = weekNumber,
    startDate = LocalDate.parse(startDate),
    endDate = LocalDate.parse(endDate),
    phase = TrainingPhase.valueOf(phase),
    targetMaxReps = targetMaxReps,
    totalVolume = totalVolume,
    dailyTasks = dailyTasks,
)

internal fun WeeklyPlan.toEntity(goalId: Long, isAdjusted: Boolean = false): WeeklyPlanEntity = WeeklyPlanEntity(
    id = id,
    goalId = goalId,
    weekNumber = weekNumber,
    startDate = startDate.toString(),
    endDate = endDate.toString(),
    phase = phase.name,
    targetMaxReps = targetMaxReps,
    totalVolume = totalVolume,
    isAdjusted = isAdjusted,
    notes = null,
)
