package com.mmd.core.data.repository

import com.mmd.core.data.mapper.toEntity
import com.mmd.core.database.dao.WorkoutDao
import com.mmd.core.domain.model.SetRecord
import com.mmd.core.domain.repository.WorkoutRepository
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

internal class WorkoutRepositoryImpl @Inject constructor(
    private val dao: WorkoutDao,
) : WorkoutRepository {

    override suspend fun saveSetRecord(taskExecutionId: Long, record: SetRecord) {
        dao.insertSetRecord(record.toEntity(taskExecutionId))
    }

    override suspend fun markTaskCompleted(taskId: Long, at: Instant) {
        dao.markTaskCompleted(taskId, done = true, ts = at.toEpochMilli())
    }

    override suspend fun totalRepsOnDate(date: LocalDate): Int =
        dao.totalRepsOnDate(date.toString()) ?: 0

    override suspend fun getRecordCountsForTask(taskId: Long): Map<Long, Int> =
        dao.getRecordsForTask(taskId)
            .groupBy { it.taskExecutionId }
            .mapValues { it.value.size }
}
