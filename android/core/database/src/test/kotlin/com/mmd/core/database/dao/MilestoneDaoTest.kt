package com.mmd.core.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.mmd.core.database.DatabaseTestRule
import com.mmd.core.database.entity.MilestoneEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MilestoneDaoTest : DatabaseTestRule() {

    private val dao get() = db.milestoneDao()

    private fun milestone(code: String, achieved: Long? = null) = MilestoneEntity(
        code = code,
        title = code,
        description = "",
        achievedAt = achieved,
    )

    @Test
    fun `insertIfAbsent does not duplicate by code`() = runTest {
        dao.insertIfAbsent(milestone("FIRST_PULLUP"))
        dao.insertIfAbsent(milestone("FIRST_PULLUP"))     // ignored

        dao.observeAll().test {
            val all = awaitItem()
            assertEquals(1, all.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `markAchieved sets achievedAt for matching code`() = runTest {
        dao.insertIfAbsent(milestone("PULLUP_5"))
        dao.insertIfAbsent(milestone("PULLUP_10"))

        dao.markAchieved("PULLUP_5", ts = 4242L)

        assertEquals(4242L, dao.getByCode("PULLUP_5")?.achievedAt)
        assertNull(dao.getByCode("PULLUP_10")?.achievedAt)
    }

    @Test
    fun `getByCode returns null for unknown code`() = runTest {
        dao.insertIfAbsent(milestone("FIRST_PULLUP"))
        assertNotNull(dao.getByCode("FIRST_PULLUP"))
        assertNull(dao.getByCode("NONEXISTENT"))
    }
}
