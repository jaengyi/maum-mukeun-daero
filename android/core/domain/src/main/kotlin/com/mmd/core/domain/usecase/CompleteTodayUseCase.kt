package com.mmd.core.domain.usecase

import com.mmd.core.domain.model.DailyCondition
import com.mmd.core.domain.repository.ConditionRepository
import com.mmd.core.domain.repository.GrassRepository
import com.mmd.core.domain.repository.WorkoutRepository
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

/**
 * 오늘 미션 완료 처리: DailyTask 완료 표시 + 컨디션 저장 + 잔디 셀 갱신.
 * S9 완료 화면에서 호출.
 */
class CompleteTodayUseCase @Inject constructor(
    private val workoutRepo: WorkoutRepository,
    private val conditionRepo: ConditionRepository,
    private val grassRepo: GrassRepository,
) {
    suspend operator fun invoke(
        taskId: Long,
        date: LocalDate,
        conditionScore: Int,
        note: String? = null,
    ) {
        val now = Instant.now()
        workoutRepo.markTaskCompleted(taskId = taskId, at = now)
        conditionRepo.upsert(
            DailyCondition(
                date = date,
                conditionScore = conditionScore,
                weightKg = null,
                note = note,
                recordedAt = now,
            ),
        )
        grassRepo.recalcDayCell(date)
    }
}
