package com.mmd.core.data.repository

import com.mmd.core.data.mapper.toDomain
import com.mmd.core.data.mapper.toEntity
import com.mmd.core.database.dao.GoalDao
import com.mmd.core.domain.model.Goal
import com.mmd.core.domain.repository.GoalRepository
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GoalRepositoryImpl @Inject constructor(
    private val dao: GoalDao,
) : GoalRepository {

    override fun observeActive(): Flow<Goal?> =
        dao.observeActive().map { it?.toDomain() }

    override suspend fun create(goal: Goal): Long = dao.insert(goal.toEntity())

    override suspend fun update(goal: Goal) {
        dao.update(goal.toEntity())
    }

    override suspend fun complete(goalId: Long, at: Instant) {
        // GoalDao에 complete 전용 쿼리가 없으므로 read-modify-write
        val existing = dao.getById(goalId) ?: return
        dao.update(existing.copy(completedAt = at.toEpochMilli()))
    }

    override suspend fun archive(goalId: Long, at: Instant) {
        dao.archive(goalId, at.toEpochMilli())
    }
}
