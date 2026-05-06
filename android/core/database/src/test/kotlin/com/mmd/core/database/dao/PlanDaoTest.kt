package com.mmd.core.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.mmd.core.database.DatabaseTestRule
import com.mmd.core.database.entity.DailyTaskEntity
import com.mmd.core.database.entity.GoalEntity
import com.mmd.core.database.entity.WeeklyPlanEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlanDaoTest : DatabaseTestRule() {

    private val planDao get() = db.planDao()
    private val goalDao get() = db.goalDao()

    private suspend fun seedGoal(): Long = goalDao.insert(
        GoalEntity(
            category = "PULLUP",
            targetValue = 10,
            targetUnit = "REPS",
            initialMaxReps = 0,
            initialDeadHangSec = 30,
            availableDays = "MON,WED,FRI",
            isActive = true,
            startedAt = 0L,
            completedAt = null,
            archivedAt = null,
        ),
    )

    private fun weekly(goalId: Long, week: Int, start: String) = WeeklyPlanEntity(
        goalId = goalId,
        weekNumber = week,
        startDate = start,
        endDate = start,
        phase = "FOUNDATION",
        targetMaxReps = 1,
        totalVolume = 30,
        isAdjusted = false,
        notes = null,
    )

    @Test
    fun `insertWeeklyPlans returns generated ids in order`() = runTest {
        val goalId = seedGoal()
        val ids = planDao.insertWeeklyPlans(
            listOf(
                weekly(goalId, 1, "2026-05-04"),
                weekly(goalId, 2, "2026-05-11"),
            ),
        )
        assertEquals(2, ids.size)
        assertEquals(listOf(1L, 2L), ids)
    }

    @Test
    fun `observeWeeklyPlans returns plans sorted by weekNumber`() = runTest {
        val goalId = seedGoal()
        // 일부러 역순으로 insert
        planDao.insertWeeklyPlans(
            listOf(
                weekly(goalId, 3, "2026-05-18"),
                weekly(goalId, 1, "2026-05-04"),
                weekly(goalId, 2, "2026-05-11"),
            ),
        )

        planDao.observeWeeklyPlans(goalId).test {
            val plans = awaitItem()
            assertEquals(listOf(1, 2, 3), plans.map { it.weekNumber })
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `observeTodayTask filters by date`() = runTest {
        val goalId = seedGoal()
        val weekIds = planDao.insertWeeklyPlans(listOf(weekly(goalId, 1, "2026-05-04")))
        planDao.insertDailyTasks(
            listOf(
                DailyTaskEntity(
                    weeklyPlanId = weekIds[0],
                    date = "2026-05-06",
                    dayType = "WORKOUT",
                    intensityLevel = "MODERATE",
                    summary = "어시스트 풀업 5×3",
                    isCompleted = false,
                    completedAt = null,
                ),
            ),
        )

        planDao.observeTodayTask("2026-05-06").test {
            assertNotNull(awaitItem())
            cancelAndConsumeRemainingEvents()
        }
        planDao.observeTodayTask("2026-05-07").test {
            assertNull(awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `replacePlanFromWeek removes existing weeks at-and-after fromWeek and inserts new`() = runTest {
        val goalId = seedGoal()
        planDao.insertWeeklyPlans(
            listOf(
                weekly(goalId, 1, "2026-05-04"),
                weekly(goalId, 2, "2026-05-11"),
                weekly(goalId, 3, "2026-05-18"),
            ),
        )

        // week 2부터 교체
        planDao.replacePlanFromWeek(
            goalId = goalId,
            fromWeek = 2,
            newPlans = listOf(
                weekly(goalId, 2, "2026-05-11").copy(targetMaxReps = 5),
                weekly(goalId, 3, "2026-05-18").copy(targetMaxReps = 7),
                weekly(goalId, 4, "2026-05-25").copy(targetMaxReps = 9),
            ),
        )

        planDao.observeWeeklyPlans(goalId).test {
            val plans = awaitItem()
            assertEquals(listOf(1, 2, 3, 4), plans.map { it.weekNumber })
            assertEquals(1, plans[0].targetMaxReps)   // week 1: unchanged
            assertEquals(5, plans[1].targetMaxReps)   // week 2: replaced
            assertEquals(9, plans[3].targetMaxReps)   // week 4: new
            cancelAndConsumeRemainingEvents()
        }
    }
}
