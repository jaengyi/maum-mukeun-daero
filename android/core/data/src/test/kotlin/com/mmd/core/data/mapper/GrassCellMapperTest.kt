package com.mmd.core.data.mapper

import com.mmd.core.domain.model.GrassCell
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class GrassCellMapperTest {

    @Test
    fun `roundtrip preserves all fields`() {
        val original = GrassCell(
            date = LocalDate.of(2026, 5, 6),
            intensityLevel = 3,
            totalReps = 12,
            isWorkoutDay = true,
            isCompleted = true,
        )
        val restored = original.toEntity(updatedAt = 999L).toDomain()
        assertEquals(original, restored)
    }
}
