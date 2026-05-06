package com.mmd.core.data.mapper

import com.mmd.core.domain.model.UserProfile
import com.mmd.core.simulation.Gender
import org.junit.Assert.assertEquals
import org.junit.Test

class UserProfileMapperTest {

    @Test
    fun `roundtrip preserves all fields`() {
        val original = UserProfile(
            nickname = "효욱",
            gender = Gender.MALE,
            birthYear = 1990,
            heightCm = 175f,
            weightKg = 72f,
        )
        val restored = original.toEntity(createdAt = 100L, updatedAt = 200L).toDomain()
        assertEquals(original, restored)
    }

    @Test
    fun `entity id is always 1 (싱글톤)`() {
        val entity = UserProfile(
            nickname = "test",
            gender = Gender.OTHER,
            birthYear = 2000,
            heightCm = 170f,
            weightKg = 60f,
        ).toEntity(createdAt = 0L, updatedAt = 0L)
        assertEquals(1, entity.id)
    }
}
