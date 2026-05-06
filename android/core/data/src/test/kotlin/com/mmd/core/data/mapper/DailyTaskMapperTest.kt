package com.mmd.core.data.mapper

import com.mmd.core.domain.model.DailyTask
import com.mmd.core.simulation.DayType
import com.mmd.core.simulation.Intensity
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class DailyTaskMapperTest {

    @Test
    fun `roundtrip preserves date enums and summary`() {
        val original = DailyTask(
            id = 7L,
            date = LocalDate.of(2026, 5, 6),
            dayType = DayType.WORKOUT,
            intensity = Intensity.MODERATE,
            summary = "어시스트 풀업 5×3",
            executions = emptyList(),
            isCompleted = false,
        )
        val restored = original.toEntity(weeklyPlanId = 1L).toDomain()
        assertEquals(original, restored)
    }
}
