package com.mmd.core.data.mapper

import com.mmd.core.database.entity.DailyTaskEntity
import com.mmd.core.domain.model.DailyTask
import com.mmd.core.domain.model.TaskExecution
import com.mmd.core.simulation.DayType
import com.mmd.core.simulation.Intensity
import java.time.LocalDate

internal fun DailyTaskEntity.toDomain(executions: List<TaskExecution> = emptyList()): DailyTask = DailyTask(
    id = id,
    date = LocalDate.parse(date),
    dayType = DayType.valueOf(dayType),
    intensity = Intensity.valueOf(intensityLevel),
    summary = summary,
    executions = executions,
    isCompleted = isCompleted,
)

/** completedAt은 markTaskCompleted 별도 처리. 신규 저장 시 null. */
internal fun DailyTask.toEntity(weeklyPlanId: Long): DailyTaskEntity = DailyTaskEntity(
    id = id,
    weeklyPlanId = weeklyPlanId,
    date = date.toString(),
    dayType = dayType.name,
    intensityLevel = intensity.name,
    summary = summary,
    isCompleted = isCompleted,
    completedAt = null,
)
