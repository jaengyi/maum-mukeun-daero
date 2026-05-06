package com.mmd.core.data.mapper

import com.mmd.core.database.entity.GrassCellEntity
import com.mmd.core.domain.model.GrassCell
import java.time.LocalDate

internal fun GrassCellEntity.toDomain(): GrassCell = GrassCell(
    date = LocalDate.parse(date),
    intensityLevel = intensityLevel,
    totalReps = totalReps,
    isWorkoutDay = isWorkoutDay,
    isCompleted = isCompleted,
)

internal fun GrassCell.toEntity(updatedAt: Long): GrassCellEntity = GrassCellEntity(
    date = date.toString(),
    intensityLevel = intensityLevel,
    totalReps = totalReps,
    isWorkoutDay = isWorkoutDay,
    isCompleted = isCompleted,
    updatedAt = updatedAt,
)
