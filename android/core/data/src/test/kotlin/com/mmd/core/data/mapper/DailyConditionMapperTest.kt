package com.mmd.core.data.mapper

import com.mmd.core.domain.model.DailyCondition
import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class DailyConditionMapperTest {

    @Test
    fun `roundtrip preserves date and Instant`() {
        val original = DailyCondition(
            date = LocalDate.of(2026, 5, 6),
            conditionScore = 4,
            weightKg = 71.5f,
            note = "컨디션 좋음",
            recordedAt = Instant.ofEpochMilli(1_700_000_000_000L),
        )
        assertEquals(original, original.toEntity().toDomain())
    }
}
