package com.mmd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mmd.core.database.entity.SetRecordEntity

@Dao
interface WorkoutDao {
    @Insert
    suspend fun insertSetRecord(record: SetRecordEntity): Long

    @Query("UPDATE daily_task SET isCompleted = :done, completedAt = :ts WHERE id = :id")
    suspend fun markTaskCompleted(id: Long, done: Boolean, ts: Long)

    @Query(
        """
        SELECT SUM(actualReps) FROM set_record sr
        JOIN task_execution te ON sr.taskExecutionId = te.id
        JOIN daily_task dt ON te.dailyTaskId = dt.id
        WHERE dt.date = :date
    """,
    )
    suspend fun totalRepsOnDate(date: String): Int?
}
