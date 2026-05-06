package com.mmd.core.domain.usecase

import com.mmd.core.domain.model.SetRecord
import com.mmd.core.domain.repository.WorkoutRepository
import java.time.Instant
import javax.inject.Inject

/**
 * 운동 세트 1개 기록. S8 운동 수행 화면에서 세트 완료마다 호출.
 */
class RecordSetUseCase @Inject constructor(
    private val workoutRepo: WorkoutRepository,
) {
    suspend operator fun invoke(
        taskExecutionId: Long,
        setNumber: Int,
        actualReps: Int,
        actualSeconds: Int? = null,
    ) {
        workoutRepo.saveSetRecord(
            taskExecutionId = taskExecutionId,
            record = SetRecord(
                id = 0,
                setNumber = setNumber,
                actualReps = actualReps,
                actualSeconds = actualSeconds,
                recordedAt = Instant.now(),
            ),
        )
    }
}
