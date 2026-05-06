package com.mmd.core.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.mmd.core.database.DatabaseTestRule
import com.mmd.core.database.entity.GoalEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoalDaoTest : DatabaseTestRule() {

    private val dao get() = db.goalDao()

    private fun sample(
        active: Boolean = true,
        startedAt: Long = 0L,
    ) = GoalEntity(
        category = "PULLUP",
        targetValue = 10,
        targetUnit = "REPS",
        initialMaxReps = 0,
        initialDeadHangSec = 30,
        availableDays = "MON,WED,FRI",
        isActive = active,
        startedAt = startedAt,
        completedAt = null,
        archivedAt = null,
    )

    @Test
    fun `insert returns generated id`() = runTest {
        val id = dao.insert(sample())
        assertEquals(1L, id)
    }

    @Test
    fun `observeActive emits active goal only`() = runTest {
        dao.insert(sample(active = false, startedAt = 1L))
        val activeId = dao.insert(sample(active = true, startedAt = 2L))

        dao.observeActive().test {
            val goal = awaitItem()
            assertNotNull(goal)
            assertEquals(activeId, goal!!.id)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `archive deactivates and sets archivedAt`() = runTest {
        val id = dao.insert(sample())
        dao.archive(id, ts = 1234L)

        val archived = dao.getById(id)!!
        assertFalse(archived.isActive)
        assertEquals(1234L, archived.archivedAt)

        dao.observeActive().test {
            assertNull(awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }
}
