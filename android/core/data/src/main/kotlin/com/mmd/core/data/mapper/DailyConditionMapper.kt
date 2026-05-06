package com.mmd.core.data.mapper

import com.mmd.core.database.entity.DailyConditionEntity
import com.mmd.core.domain.model.DailyCondition
import java.time.Instant
import java.time.LocalDate

internal fun DailyConditionEntity.toDomain(): DailyCondition = DailyCondition(
    date = LocalDate.parse(date),
    conditionScore = conditionScore,
    weightKg = weightKg,
    note = note,
    recordedAt = Instant.ofEpochMilli(recordedAt),
)

internal fun DailyCondition.toEntity(): DailyConditionEntity = DailyConditionEntity(
    date = date.toString(),
    conditionScore = conditionScore,
    weightKg = weightKg,
    note = note,
    recordedAt = recordedAt.toEpochMilli(),
)
