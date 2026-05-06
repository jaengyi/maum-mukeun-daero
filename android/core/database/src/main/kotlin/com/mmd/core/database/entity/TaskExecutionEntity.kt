package com.mmd.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_execution",
    foreignKeys = [
        ForeignKey(
            entity = DailyTaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["dailyTaskId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("dailyTaskId")],
)
data class TaskExecutionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dailyTaskId: Long,
    val exerciseType: String,      // "PULLUP" / "ASSISTED_PULLUP" / ...
    val targetSets: Int,
    val targetReps: Int,           // DEAD_HANG의 경우 초
    val restSeconds: Int,
    val orderInTask: Int,
)
