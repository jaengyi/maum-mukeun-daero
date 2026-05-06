package com.mmd.core.data.mapper

import com.mmd.core.database.entity.TaskExecutionEntity
import com.mmd.core.domain.model.SetRecord
import com.mmd.core.domain.model.TaskExecution
import com.mmd.core.simulation.ExerciseType

internal fun TaskExecutionEntity.toDomain(records: List<SetRecord> = emptyList()): TaskExecution = TaskExecution(
    id = id,
    exerciseType = ExerciseType.valueOf(exerciseType),
    targetSets = targetSets,
    targetReps = targetReps,
    restSeconds = restSeconds,
    records = records,
)

internal fun TaskExecution.toEntity(dailyTaskId: Long, orderInTask: Int): TaskExecutionEntity = TaskExecutionEntity(
    id = id,
    dailyTaskId = dailyTaskId,
    exerciseType = exerciseType.name,
    targetSets = targetSets,
    targetReps = targetReps,
    restSeconds = restSeconds,
    orderInTask = orderInTask,
)
