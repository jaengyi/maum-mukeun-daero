package com.mmd.feature.tracker

import com.mmd.core.domain.model.DailyTask
import com.mmd.core.domain.model.GrassCell
import java.time.LocalDate

sealed interface TrackerUiState {
    data object Loading : TrackerUiState

    data object NoActiveGoal : TrackerUiState

    data class CountdownToStart(
        val daysUntil: Int,
        val firstWorkoutDate: LocalDate,
        val recentGrass: List<GrassCell>,
    ) : TrackerUiState

    data class WorkoutDay(
        val task: DailyTask,
        val weekNumber: Int,
        val totalWeeks: Int,
        val recentGrass: List<GrassCell>,
    ) : TrackerUiState

    data class RestDay(
        val message: String,
        val recentGrass: List<GrassCell>,
    ) : TrackerUiState

    data class PlanEnded(
        val recentGrass: List<GrassCell>,
    ) : TrackerUiState
}
