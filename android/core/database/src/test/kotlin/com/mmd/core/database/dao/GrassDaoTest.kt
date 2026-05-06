package com.mmd.core.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.mmd.core.database.DatabaseTestRule
import com.mmd.core.database.entity.GrassCellEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GrassDaoTest : DatabaseTestRule() {

    private val dao get() = db.grassDao()

    private fun cell(date: String, intensity: Int = 2, total: Int = 10) = GrassCellEntity(
        date = date,
        intensityLevel = intensity,
        totalReps = total,
        isWorkoutDay = true,
        isCompleted = true,
        updatedAt = 0L,
    )

    @Test
    fun `upsert and observeRange returns cells in range sorted by date`() = runTest {
        dao.upsert(cell("2026-05-06"))
        dao.upsert(cell("2026-05-04"))
        dao.upsert(cell("2026-05-10"))     // out of range below

        dao.observeRange("2026-05-04", "2026-05-08").test {
            val cells = awaitItem()
            assertEquals(listOf("2026-05-04", "2026-05-06"), cells.map { it.date })
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `upsert with same date replaces previous values`() = runTest {
        dao.upsert(cell("2026-05-06", intensity = 1, total = 5))
        dao.upsert(cell("2026-05-06", intensity = 4, total = 30))

        dao.observeRange("2026-05-06", "2026-05-06").test {
            val cells = awaitItem()
            assertEquals(1, cells.size)
            assertEquals(4, cells[0].intensityLevel)
            assertEquals(30, cells[0].totalReps)
            cancelAndConsumeRemainingEvents()
        }
    }
}
