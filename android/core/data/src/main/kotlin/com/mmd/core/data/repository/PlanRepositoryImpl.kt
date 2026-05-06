package com.mmd.core.data.repository

import androidx.room.withTransaction
import com.mmd.core.data.mapper.toDomain
import com.mmd.core.data.mapper.toEntity
import com.mmd.core.database.MmdDatabase
import com.mmd.core.database.dao.PlanDao
import com.mmd.core.domain.model.DailyTask
import com.mmd.core.domain.model.WeeklyPlan
import com.mmd.core.domain.repository.PlanRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class PlanRepositoryImpl @Inject constructor(
    private val db: MmdDatabase,
    private val dao: PlanDao,
) : PlanRepository {

    override fun observeTodayTask(date: LocalDate): Flow<DailyTask?> =
        dao.observeTodayTask(date.toString()).map { it?.toDomain() }

    override fun observeWeeklyPlans(goalId: Long): Flow<List<WeeklyPlan>> =
        dao.observeWeeklyPlans(goalId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun savePlanForGoal(goalId: Long, plans: List<WeeklyPlan>) {
        db.withTransaction {
            insertPlanTree(goalId = goalId, plans = plans, isAdjusted = false)
        }
    }

    override suspend fun replacePlanFromWeek(
        goalId: Long,
        fromWeek: Int,
        newPlans: List<WeeklyPlan>,
    ) {
        db.withTransaction {
            // FK CASCADE로 자식(DailyTask, TaskExecution, SetRecord)도 삭제됨
            dao.deletePlansFromWeek(goalId, fromWeek)
            insertPlanTree(goalId = goalId, plans = newPlans, isAdjusted = true)
        }
    }

    private suspend fun insertPlanTree(
        goalId: Long,
        plans: List<WeeklyPlan>,
        isAdjusted: Boolean,
    ) {
        val weekIds = dao.insertWeeklyPlans(plans.map { it.toEntity(goalId, isAdjusted) })

        plans.zip(weekIds).forEach { (plan, weekId) ->
            val taskIds = dao.insertDailyTasks(plan.dailyTasks.map { it.toEntity(weekId) })

            plan.dailyTasks.zip(taskIds).forEach { (task, taskId) ->
                if (task.executions.isNotEmpty()) {
                    dao.insertTaskExecutions(
                        task.executions.mapIndexed { index, exec -> exec.toEntity(taskId, index) },
                    )
                }
            }
        }
    }
}
