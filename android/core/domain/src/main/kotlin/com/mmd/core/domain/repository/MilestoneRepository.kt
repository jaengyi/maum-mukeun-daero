package com.mmd.core.domain.repository

import com.mmd.core.domain.model.Milestone
import java.time.Instant
import kotlinx.coroutines.flow.Flow

interface MilestoneRepository {
    fun observeAll(): Flow<List<Milestone>>
    suspend fun markAchieved(code: String, at: Instant)
}
