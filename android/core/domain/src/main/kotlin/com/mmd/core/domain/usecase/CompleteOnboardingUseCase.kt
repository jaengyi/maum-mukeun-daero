package com.mmd.core.domain.usecase

import com.mmd.core.domain.mapper.toDomainPlans
import com.mmd.core.domain.model.Goal
import com.mmd.core.domain.model.GoalCategory
import com.mmd.core.domain.model.UserProfile
import com.mmd.core.domain.repository.GoalRepository
import com.mmd.core.domain.repository.PlanRepository
import com.mmd.core.domain.repository.UserProfileRepository
import com.mmd.core.simulation.PullupSimulator
import com.mmd.core.simulation.SimulationInput
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

/**
 * 온보딩 완료 처리: profile 저장 + Goal 생성 + 시뮬레이터 실행 + 12주 계획 트리 저장.
 *
 * @return 새로 생성된 goalId
 */
class CompleteOnboardingUseCase @Inject constructor(
    private val userProfileRepo: UserProfileRepository,
    private val goalRepo: GoalRepository,
    private val planRepo: PlanRepository,
    private val simulator: PullupSimulator,
) {
    suspend operator fun invoke(
        profile: UserProfile,
        simInput: SimulationInput,
        planStartDate: LocalDate = defaultPlanStartDate(),
    ): Long {
        userProfileRepo.upsert(profile)

        val goal = Goal(
            id = 0,
            category = GoalCategory.PULLUP,
            targetValue = simInput.targetReps,
            initialMaxReps = simInput.currentMaxPullups,
            initialDeadHangSec = simInput.currentDeadHangSeconds,
            availableDays = simInput.availableDaysOfWeek,
            isActive = true,
            startedAt = Instant.now(),
            completedAt = null,
        )
        val goalId = goalRepo.create(goal)

        val result = simulator.generatePlan(simInput)
        val plans = result.toDomainPlans(planStartDate)
        planRepo.savePlanForGoal(goalId, plans)

        return goalId
    }

    private fun defaultPlanStartDate(): LocalDate =
        LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))
}
