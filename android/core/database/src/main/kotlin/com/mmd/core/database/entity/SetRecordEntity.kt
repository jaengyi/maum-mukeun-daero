package com.mmd.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "set_record",
    foreignKeys = [
        ForeignKey(
            entity = TaskExecutionEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskExecutionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("taskExecutionId"), Index("recordedAt")],
)
data class SetRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskExecutionId: Long,
    val setNumber: Int,
    val actualReps: Int,
    val actualSeconds: Int?,       // 매달리기 등 시간 측정 종목
    val recordedAt: Long,          // epoch millis
)
