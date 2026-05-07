package com.mmd.feature.tracker.workout

import com.mmd.core.domain.model.DailyTask

data class WorkoutUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val task: DailyTask? = null,                              // executions 포함
    val recordCountByExec: Map<Long, Int> = emptyMap(),
    val currentExecutionIndex: Int = 0,
    val currentSetNumber: Int = 1,
    val repsInput: Int = 0,
    val restRemainingSeconds: Int? = null,                    // null = 입력 중, non-null = 휴식 중
    val isCompleted: Boolean = false,
)

sealed interface WorkoutEvent {
    data object DecreaseReps : WorkoutEvent
    data object IncreaseReps : WorkoutEvent
    data object SetCompleted : WorkoutEvent
    data object SkipRest : WorkoutEvent
}
