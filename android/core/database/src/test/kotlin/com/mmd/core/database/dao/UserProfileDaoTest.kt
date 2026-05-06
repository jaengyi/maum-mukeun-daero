package com.mmd.core.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.mmd.core.database.DatabaseTestRule
import com.mmd.core.database.entity.UserProfileEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserProfileDaoTest : DatabaseTestRule() {

    private val dao get() = db.userProfileDao()

    private fun sample(nickname: String = "효욱", weight: Float = 72f) = UserProfileEntity(
        nickname = nickname,
        gender = "MALE",
        birthYear = 1990,
        heightCm = 175f,
        weightKg = weight,
        createdAt = 0L,
        updatedAt = 0L,
    )

    @Test
    fun `observe emits null then upserted profile`() = runTest {
        dao.observe().test {
            assertNull(awaitItem())
            dao.upsert(sample())
            assertEquals("효욱", awaitItem()?.nickname)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `second upsert replaces existing profile (id = 1 singleton)`() = runTest {
        dao.upsert(sample(nickname = "first", weight = 70f))
        dao.upsert(sample(nickname = "second", weight = 75f))

        dao.observe().test {
            val profile = awaitItem()
            assertEquals("second", profile?.nickname)
            assertEquals(75f, profile?.weightKg)
            cancelAndConsumeRemainingEvents()
        }
    }
}
