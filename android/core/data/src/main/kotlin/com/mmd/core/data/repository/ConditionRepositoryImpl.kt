package com.mmd.core.data.repository

import com.mmd.core.data.mapper.toDomain
import com.mmd.core.data.mapper.toEntity
import com.mmd.core.database.dao.ConditionDao
import com.mmd.core.domain.model.DailyCondition
import com.mmd.core.domain.repository.ConditionRepository
import java.time.LocalDate
import javax.inject.Inject

internal class ConditionRepositoryImpl @Inject constructor(
    private val dao: ConditionDao,
) : ConditionRepository {

    override suspend fun upsert(condition: DailyCondition) {
        dao.upsert(condition.toEntity())
    }

    override suspend fun range(from: LocalDate, to: LocalDate): List<DailyCondition> =
        dao.range(from.toString(), to.toString()).map { it.toDomain() }
}
