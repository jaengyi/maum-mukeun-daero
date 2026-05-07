package com.mmd.core.domain.repository

import com.mmd.core.domain.model.SetRecord
import java.time.Instant
import java.time.LocalDate

interface WorkoutRepository {
    suspend fun saveSetRecord(taskExecutionId: Long, record: SetRecord)
    suspend fun markTaskCompleted(taskId: Long, at: Instant)
    suspend fun totalRepsOnDate(date: LocalDate): Int

    /** S8 진행 상태 복원용 — execution id별 기록된 세트 수 */
    suspend fun getRecordCountsForTask(taskId: Long): Map<Long, Int>
}
