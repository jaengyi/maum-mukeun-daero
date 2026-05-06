package com.mmd.core.domain.repository

import com.mmd.core.domain.model.SetRecord
import java.time.Instant
import java.time.LocalDate

interface WorkoutRepository {
    suspend fun saveSetRecord(taskExecutionId: Long, record: SetRecord)
    suspend fun markTaskCompleted(taskId: Long, at: Instant)
    suspend fun totalRepsOnDate(date: LocalDate): Int
}
