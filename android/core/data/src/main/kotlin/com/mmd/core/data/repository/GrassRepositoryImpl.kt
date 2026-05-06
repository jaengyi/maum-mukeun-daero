package com.mmd.core.data.repository

import com.mmd.core.data.mapper.toDomain
import com.mmd.core.database.dao.GrassDao
import com.mmd.core.domain.model.GrassCell
import com.mmd.core.domain.repository.GrassRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GrassRepositoryImpl @Inject constructor(
    private val dao: GrassDao,
) : GrassRepository {

    override fun observeRange(from: LocalDate, to: LocalDate): Flow<List<GrassCell>> =
        dao.observeRange(from.toString(), to.toString())
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun recalcDayCell(date: LocalDate) {
        // Phase 3: SetRecord 합산 → intensityLevel 결정 → GrassCell upsert
    }
}
