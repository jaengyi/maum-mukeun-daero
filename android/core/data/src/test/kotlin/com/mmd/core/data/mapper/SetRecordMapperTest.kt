package com.mmd.core.data.mapper

import com.mmd.core.domain.model.SetRecord
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class SetRecordMapperTest {

    @Test
    fun `roundtrip preserves Instant precision and nullable seconds`() {
        val original = SetRecord(
            id = 11L,
            setNumber = 2,
            actualReps = 4,
            actualSeconds = null,
            recordedAt = Instant.ofEpochMilli(1_750_000_000_000L),
        )
        val restored = original.toEntity(taskExecutionId = 1L).toDomain()
        assertEquals(original, restored)
    }

    @Test
    fun `roundtrip with non-null actualSeconds (DEAD_HANG)`() {
        val original = SetRecord(
            id = 12L,
            setNumber = 1,
            actualReps = 0,
            actualSeconds = 35,
            recordedAt = Instant.ofEpochMilli(0L),
        )
        assertEquals(original, original.toEntity(taskExecutionId = 1L).toDomain())
    }
}
