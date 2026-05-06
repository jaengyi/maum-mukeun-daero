package com.mmd.core.domain.repository

import com.mmd.core.domain.model.DailyTask
import com.mmd.core.domain.model.WeeklyPlan
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface PlanRepository {
    fun observeTodayTask(date: LocalDate): Flow<DailyTask?>
    fun observeWeeklyPlans(goalId: Long): Flow<List<WeeklyPlan>>

    /** 시뮬레이션 결과로 12주 계획을 통째로 저장 (WeeklyPlan + DailyTask + TaskExecution 트랜잭션) */
    suspend fun savePlanForGoal(goalId: Long, plans: List<WeeklyPlan>)

    /** 동적 재조정 — fromWeek부터 끝까지 교체 (Phase 5) */
    suspend fun replacePlanFromWeek(goalId: Long, fromWeek: Int, newPlans: List<WeeklyPlan>)
}
