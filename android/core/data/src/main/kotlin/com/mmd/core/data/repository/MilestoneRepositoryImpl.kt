package com.mmd.core.data.repository

import com.mmd.core.data.mapper.toDomain
import com.mmd.core.database.dao.MilestoneDao
import com.mmd.core.domain.model.Milestone
import com.mmd.core.domain.repository.MilestoneRepository
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class MilestoneRepositoryImpl @Inject constructor(
    private val dao: MilestoneDao,
) : MilestoneRepository {

    override fun observeAll(): Flow<List<Milestone>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun markAchieved(code: String, at: Instant) {
        dao.markAchieved(code, at.toEpochMilli())
    }
}
