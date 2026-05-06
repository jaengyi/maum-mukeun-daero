package com.mmd.core.domain.model

import com.mmd.core.simulation.ExerciseType

data class TaskExecution(
    val id: Long,
    val exerciseType: ExerciseType,
    val targetSets: Int,
    val targetReps: Int,           // DEAD_HANG의 경우 초
    val restSeconds: Int,
    val records: List<SetRecord>,
)
