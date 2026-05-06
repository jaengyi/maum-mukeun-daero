package com.mmd.core.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.mmd.core.database.DatabaseTestRule
import com.mmd.core.database.entity.DailyTaskEntity
import com.mmd.core.database.entity.GoalEntity
import com.mmd.core.database.entity.SetRecordEntity
import com.mmd.core.database.entity.TaskExecutionEntity
import com.mmd.core.database.entity.WeeklyPlanEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorkoutDaoTest : DatabaseTestRule() {

    private val workoutDao get() = db.workoutDao()
    private val planDao get() = db.planDao()
    private val goalDao get() = db.goalDao()

    private suspend fun seedTaskWithExecution(date: String): Pair<Long, Long> {
        val goalId = goalDao.insert(
            GoalEntity(
                category = "PULLUP", targetValue = 10, targetUnit = "REPS",
                initialMaxReps = 0, initialDeadHangSec = 30, availableDays = "MON",
                isActive = true, startedAt = 0L, completedAt = null, archivedAt = null,
            ),
        )
        val weekIds = planDao.insertWeeklyPlans(
            listOf(
                WeeklyPlanEntity(
                    goalId = goalId, weekNumber = 1, startDate = date, endDate = date,
                    phase = "FOUNDATION", targetMaxReps = 1, totalVolume = 30,
                    isAdjusted = false, notes = null,
                ),
            ),
        )
        val taskIds = planDao.insertDailyTasks(
            listOf(
                DailyTaskEntity(
                    weeklyPlanId = weekIds[0], date = date, dayType = "WORKOUT",
                    intensityLevel = "MODERATE", summary = "test", isCompleted = false,
                    completedAt = null,
                ),
            ),
        )
        val execIds = planDao.insertTaskExecutions(
            listOf(
                TaskExecutionEntity(
                    dailyTaskId = taskIds[0], exerciseType = "PULLUP",
                    targetSets = 3, targetReps = 5, restSeconds = 90, orderInTask = 0,
                ),
            ),
        )
        return taskIds[0] to execIds[0]
    }

    @Test
    fun `insertSetRecord returns generated id`() = runTest {
        val (_, execId) = seedTaskWithExecution(date = "2026-05-06")
        val id = workoutDao.insertSetRecord(
            SetRecordEntity(
                taskExecutionId = execId, setNumber = 1,
                actualReps = 5, actualSeconds = null, recordedAt = 0L,
            ),
        )
        assertEquals(1L, id)
    }

    @Test
    fun `markTaskCompleted updates isCompleted and completedAt`() = runTest {
        val date = "2026-05-06"
        val (taskId, _) = seedTaskWithExecution(date = date)
        workoutDao.markTaskCompleted(taskId, done = true, ts = 9999L)

        planDao.observeTodayTask(date).test {
            val task = awaitItem()!!
            assertTrue(task.isCompleted)
            assertEquals(9999L, task.completedAt)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `totalRepsOnDate aggregates sets across executions of the day`() = runTest {
        val date = "2026-05-06"
        val (_, execId) = seedTaskWithExecution(date = date)

        listOf(5, 4, 3).forEachIndexed { i, reps ->
            workoutDao.insertSetRecord(
                SetRecordEntity(
                    taskExecutionId = execId, setNumber = i + 1,
                    actualReps = reps, actualSeconds = null, recordedAt = 0L,
                ),
            )
        }

        assertEquals(12, workoutDao.totalRepsOnDate(date))
    }
}
