package com.mmd.core.data.mapper

import com.mmd.core.database.entity.MilestoneEntity
import com.mmd.core.domain.model.Milestone
import java.time.Instant

internal fun MilestoneEntity.toDomain(): Milestone = Milestone(
    id = id,
    code = code,
    title = title,
    description = description,
    achievedAt = achievedAt?.let(Instant::ofEpochMilli),
)

internal fun Milestone.toEntity(): MilestoneEntity = MilestoneEntity(
    id = id,
    code = code,
    title = title,
    description = description,
    achievedAt = achievedAt?.toEpochMilli(),
)
