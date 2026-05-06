package com.mmd.core.data.mapper

import com.mmd.core.domain.model.WeeklyPlan
import com.mmd.core.simulation.TrainingPhase
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class WeeklyPlanMapperTest {

    @Test
    fun `roundtrip preserves dates and phase`() {
        val original = WeeklyPlan(
            id = 1L,
            weekNumber = 3,
            startDate = LocalDate.of(2026, 5, 4),
            endDate = LocalDate.of(2026, 5, 10),
            phase = TrainingPhase.BUILD,
            targetMaxReps = 5,
            totalVolume = 60,
            dailyTasks = emptyList(),
        )
        val restored = original.toEntity(goalId = 99L).toDomain()
        assertEquals(original, restored)
    }
}
