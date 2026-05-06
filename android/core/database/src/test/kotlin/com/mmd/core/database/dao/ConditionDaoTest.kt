package com.mmd.core.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mmd.core.database.DatabaseTestRule
import com.mmd.core.database.entity.DailyConditionEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConditionDaoTest : DatabaseTestRule() {

    private val dao get() = db.conditionDao()

    private fun cond(date: String, score: Int = 4) = DailyConditionEntity(
        date = date,
        conditionScore = score,
        weightKg = null,
        note = null,
        recordedAt = 0L,
    )

    @Test
    fun `range filters dates and excludes out-of-bounds`() = runTest {
        dao.upsert(cond("2026-05-04"))
        dao.upsert(cond("2026-05-06"))
        dao.upsert(cond("2026-05-10"))

        val results = dao.range("2026-05-05", "2026-05-08")
        assertEquals(listOf("2026-05-06"), results.map { it.date })
    }

    @Test
    fun `upsert with same date replaces previous condition`() = runTest {
        dao.upsert(cond("2026-05-06", score = 3))
        dao.upsert(cond("2026-05-06", score = 5))

        val results = dao.range("2026-05-06", "2026-05-06")
        assertEquals(1, results.size)
        assertEquals(5, results[0].conditionScore)
    }
}
