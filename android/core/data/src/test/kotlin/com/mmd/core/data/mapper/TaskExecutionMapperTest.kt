package com.mmd.core.data.mapper

import com.mmd.core.domain.model.TaskExecution
import com.mmd.core.simulation.ExerciseType
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskExecutionMapperTest {

    @Test
    fun `roundtrip preserves exerciseType and timing`() {
        val original = TaskExecution(
            id = 5L,
            exerciseType = ExerciseType.ASSISTED_PULLUP,
            targetSets = 3,
            targetReps = 5,
            restSeconds = 90,
            records = emptyList(),
        )
        val restored = original.toEntity(dailyTaskId = 1L, orderInTask = 0).toDomain()
        assertEquals(original, restored)
    }
}
