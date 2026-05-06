package com.mmd.core.data.mapper

import com.mmd.core.database.entity.GoalEntity
import com.mmd.core.domain.model.Goal
import com.mmd.core.domain.model.GoalCategory
import java.time.DayOfWeek
import java.time.Instant

private val DAY_TO_ABBREV = mapOf(
    DayOfWeek.MONDAY to "MON",
    DayOfWeek.TUESDAY to "TUE",
    DayOfWeek.WEDNESDAY to "WED",
    DayOfWeek.THURSDAY to "THU",
    DayOfWeek.FRIDAY to "FRI",
    DayOfWeek.SATURDAY to "SAT",
    DayOfWeek.SUNDAY to "SUN",
)
private val ABBREV_TO_DAY = DAY_TO_ABBREV.entries.associate { (k, v) -> v to k }

internal fun Set<DayOfWeek>.serializeDays(): String =
    DayOfWeek.entries
        .filter { it in this }
        .joinToString(",") { DAY_TO_ABBREV.getValue(it) }

internal fun String.parseDays(): Set<DayOfWeek> =
    if (isEmpty()) emptySet()
    else split(",").map { ABBREV_TO_DAY.getValue(it) }.toSet()

internal fun GoalEntity.toDomain(): Goal = Goal(
    id = id,
    category = GoalCategory.valueOf(category),
    targetValue = targetValue,
    initialMaxReps = initialMaxReps,
    initialDeadHangSec = initialDeadHangSec,
    availableDays = availableDays.parseDays(),
    isActive = isActive,
    startedAt = Instant.ofEpochMilli(startedAt),
    completedAt = completedAt?.let(Instant::ofEpochMilli),
)

/**
 * 도메인 → entity. archivedAt은 도메인에 없으므로 항상 null로 매핑.
 * archive()는 Repository.archive()가 별도 DAO 쿼리로 처리.
 * MVP에서는 update() 시 archivedAt이 null로 덮여씌워질 수 있음 (archived goal 수정은 가정 안 함).
 */
internal fun Goal.toEntity(): GoalEntity = GoalEntity(
    id = id,
    category = category.name,
    targetValue = targetValue,
    targetUnit = "REPS",          // MVP: REPS만
    initialMaxReps = initialMaxReps,
    initialDeadHangSec = initialDeadHangSec,
    availableDays = availableDays.serializeDays(),
    isActive = isActive,
    startedAt = startedAt.toEpochMilli(),
    completedAt = completedAt?.toEpochMilli(),
    archivedAt = null,
)
