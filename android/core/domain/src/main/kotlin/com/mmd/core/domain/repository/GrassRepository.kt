package com.mmd.core.domain.repository

import com.mmd.core.domain.model.GrassCell
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface GrassRepository {
    fun observeRange(from: LocalDate, to: LocalDate): Flow<List<GrassCell>>

    /** 세트 기록 시 해당 일자의 잔디 셀 재계산 */
    suspend fun recalcDayCell(date: LocalDate)
}
