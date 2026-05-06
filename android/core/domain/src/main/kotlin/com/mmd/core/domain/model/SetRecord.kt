package com.mmd.core.domain.model

import java.time.Instant

data class SetRecord(
    val id: Long,
    val setNumber: Int,
    val actualReps: Int,
    val actualSeconds: Int?,
    val recordedAt: Instant,
)
