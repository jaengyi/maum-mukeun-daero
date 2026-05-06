package com.mmd.feature.tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmd.core.domain.model.DailyTask
import com.mmd.core.domain.model.Goal
import com.mmd.core.domain.model.GrassCell
import com.mmd.core.domain.model.WeeklyPlan
import com.mmd.core.domain.repository.GoalRepository
import com.mmd.core.domain.repository.GrassRepository
import com.mmd.core.domain.repository.PlanRepository
import com.mmd.core.simulation.DayType
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val planRepo: PlanRepository,
    private val goalRepo: GoalRepository,
    private val grassRepo: GrassRepository,
) : ViewModel() {

    private val today: LocalDate = LocalDate.now()
    private val grassFrom: LocalDate = today.minusDays(27)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState =
        goalRepo.observeActive()
            .flatMapLatest { goal -> if (goal == null) flowOf(TrackerUiState.NoActiveGoal) else combineFlows(goal) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = TrackerUiState.Loading,
            )

    private fun combineFlows(goal: Goal): Flow<TrackerUiState> = combine(
        planRepo.observeTodayTask(today),
        planRepo.observeWeeklyPlans(goal.id),
        grassRepo.observeRange(grassFrom, today),
    ) { task, plans, grass ->
        buildState(task, plans, grass)
    }

    private fun buildState(
        task: DailyTask?,
        plans: List<WeeklyPlan>,
        grass: List<GrassCell>,
    ): TrackerUiState {
        if (plans.isEmpty()) return TrackerUiState.Loading

        val planStart = plans.first().startDate
        val planEnd = plans.last().endDate

        return when {
            today.isBefore(planStart) -> TrackerUiState.CountdownToStart(
                daysUntil = ChronoUnit.DAYS.between(today, planStart).toInt(),
                firstWorkoutDate = planStart,
                recentGrass = grass,
            )
            today.isAfter(planEnd) -> TrackerUiState.PlanEnded(recentGrass = grass)
            task != null && task.dayType == DayType.WORKOUT -> {
                val current = plans.find { today in it.startDate..it.endDate }
                TrackerUiState.WorkoutDay(
                    task = task,
                    weekNumber = current?.weekNumber ?: 1,
                    totalWeeks = plans.size,
                    recentGrass = grass,
                )
            }
            task != null && task.dayType == DayType.REST -> TrackerUiState.RestDay(
                message = "오늘은 회복일이에요. 근육이 자라는 시간이에요.",
                recentGrass = grass,
            )
            else -> TrackerUiState.RestDay(
                // task == null but plan in range — today's day-of-week not in availableDays
                message = "오늘은 자유 날이에요. 가벼운 스트레칭은 어떠세요?",
                recentGrass = grass,
            )
        }
    }
}
