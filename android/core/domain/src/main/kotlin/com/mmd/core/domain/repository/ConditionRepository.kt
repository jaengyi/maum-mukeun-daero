package com.mmd.core.domain.repository

import com.mmd.core.domain.model.DailyCondition
import java.time.LocalDate

interface ConditionRepository {
    suspend fun upsert(condition: DailyCondition)
    suspend fun range(from: LocalDate, to: LocalDate): List<DailyCondition>
}
