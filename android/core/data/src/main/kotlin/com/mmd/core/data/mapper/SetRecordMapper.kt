package com.mmd.core.data.mapper

import com.mmd.core.database.entity.SetRecordEntity
import com.mmd.core.domain.model.SetRecord
import java.time.Instant

internal fun SetRecordEntity.toDomain(): SetRecord = SetRecord(
    id = id,
    setNumber = setNumber,
    actualReps = actualReps,
    actualSeconds = actualSeconds,
    recordedAt = Instant.ofEpochMilli(recordedAt),
)

internal fun SetRecord.toEntity(taskExecutionId: Long): SetRecordEntity = SetRecordEntity(
    id = id,
    taskExecutionId = taskExecutionId,
    setNumber = setNumber,
    actualReps = actualReps,
    actualSeconds = actualSeconds,
    recordedAt = recordedAt.toEpochMilli(),
)
