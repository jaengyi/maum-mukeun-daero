package com.mmd.core.data.mapper

import com.mmd.core.domain.model.Milestone
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class MilestoneMapperTest {

    @Test
    fun `roundtrip with achievedAt = null (unachieved)`() {
        val original = Milestone(
            id = 1L,
            code = "FIRST_PULLUP",
            title = "첫 풀업",
            description = "처음 1개 성공",
            achievedAt = null,
        )
        assertEquals(original, original.toEntity().toDomain())
    }

    @Test
    fun `roundtrip with achievedAt set`() {
        val original = Milestone(
            id = 2L,
            code = "PULLUP_5",
            title = "5개 달성",
            description = "한 번에 5개",
            achievedAt = Instant.ofEpochMilli(1_700_000_000_000L),
        )
        assertEquals(original, original.toEntity().toDomain())
    }
}
