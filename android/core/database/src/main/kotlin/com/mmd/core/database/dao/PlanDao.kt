package com.mmd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.mmd.core.database.entity.DailyTaskEntity
import com.mmd.core.database.entity.TaskExecutionEntity
import com.mmd.core.database.entity.WeeklyPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    @Insert
    suspend fun insertWeeklyPlans(plans: List<WeeklyPlanEntity>): List<Long>

    @Insert
    suspend fun insertDailyTasks(tasks: List<DailyTaskEntity>): List<Long>

    @Insert
    suspend fun insertTaskExecutions(execs: List<TaskExecutionEntity>): List<Long>

    @Query("SELECT * FROM daily_task WHERE date = :date LIMIT 1")
    fun observeTodayTask(date: String): Flow<DailyTaskEntity?>

    @Query(
        """
        SELECT * FROM weekly_plan
        WHERE goalId = :goalId
        ORDER BY weekNumber ASC
    """,
    )
    fun observeWeeklyPlans(goalId: Long): Flow<List<WeeklyPlanEntity>>

    @Query("DELETE FROM weekly_plan WHERE goalId = :goalId AND weekNumber >= :fromWeek")
    suspend fun deletePlansFromWeek(goalId: Long, fromWeek: Int)

    /**
     * 동적 재조정(Phase 5) — fromWeek 이후 주차를 모두 삭제하고 새 계획으로 교체.
     * DailyTask/TaskExecution은 외래키 CASCADE로 함께 삭제됨.
     */
    @Transaction
    suspend fun replacePlanFromWeek(goalId: Long, fromWeek: Int, newPlans: List<WeeklyPlanEntity>) {
        deletePlansFromWeek(goalId, fromWeek)
        insertWeeklyPlans(newPlans)
    }
}
