package com.mmd.core.data.mapper

import com.mmd.core.domain.model.Goal
import com.mmd.core.domain.model.GoalCategory
import java.time.DayOfWeek
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class GoalMapperTest {

    @Test
    fun `availableDays serialize in canonical Mon-Sun order`() {
        // 입력 순서가 다르더라도 결과는 항상 MON,WED,FRI 순서
        val days = setOf(DayOfWeek.FRIDAY, DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
        assertEquals("MON,WED,FRI", days.serializeDays())
    }

    @Test
    fun `availableDays parse round-trips through serialize`() {
        val days = setOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        assertEquals(days, days.serializeDays().parseDays())
    }

    @Test
    fun `empty days serialize to empty string`() {
        assertEquals("", emptySet<DayOfWeek>().serializeDays())
        assertEquals(emptySet<DayOfWeek>(), "".parseDays())
    }

    @Test
    fun `roundtrip preserves all fields including completedAt`() {
        val original = Goal(
            id = 42L,
            category = GoalCategory.PULLUP,
            targetValue = 10,
            initialMaxReps = 0,
            initialDeadHangSec = 30,
            availableDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            isActive = true,
            startedAt = Instant.ofEpochMilli(1_700_000_000_000L),
            completedAt = Instant.ofEpochMilli(1_800_000_000_000L),
        )
        assertEquals(original, original.toEntity().toDomain())
    }
}
