package com.mmd.core.domain.repository

import com.mmd.core.domain.model.Goal
import java.time.Instant
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun observeActive(): Flow<Goal?>
    suspend fun create(goal: Goal): Long
    suspend fun update(goal: Goal)
    suspend fun complete(goalId: Long, at: Instant)
    suspend fun archive(goalId: Long, at: Instant)
}
