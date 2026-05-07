package com.mmd.feature.tracker.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmd.core.domain.model.DailyTask
import com.mmd.core.domain.repository.PlanRepository
import com.mmd.core.domain.repository.WorkoutRepository
import com.mmd.core.domain.usecase.RecordSetUseCase
import com.mmd.core.simulation.ExerciseType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val planRepo: PlanRepository,
    private val workoutRepo: WorkoutRepository,
    private val recordSet: RecordSetUseCase,
) : ViewModel() {

    private val taskId: Long = checkNotNull(savedStateHandle.get<Long>(WorkoutNavArgs.TASK_ID)) {
        "${WorkoutNavArgs.TASK_ID} required"
    }

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    private var restJob: Job? = null

    init {
        loadSession()
    }

    fun onEvent(event: WorkoutEvent) {
        when (event) {
            WorkoutEvent.DecreaseReps ->
                _uiState.update { it.copy(repsInput = (it.repsInput - 1).coerceAtLeast(0)) }
            WorkoutEvent.IncreaseReps ->
                _uiState.update { it.copy(repsInput = it.repsInput + 1) }
            WorkoutEvent.SetCompleted -> handleSetCompleted()
            WorkoutEvent.SkipRest -> {
                restJob?.cancel()
                _uiState.update { it.copy(restRemainingSeconds = null) }
            }
        }
    }

    private fun loadSession() {
        viewModelScope.launch {
            try {
                val task = planRepo.getTaskWithExecutions(taskId)
                if (task == null || task.executions.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "운동 데이터를 찾을 수 없어요.",
                        )
                    }
                    return@launch
                }

                val recordCounts = workoutRepo.getRecordCountsForTask(taskId)
                val (currentIdx, currentSet, allDone) = computeStartPosition(task, recordCounts)

                if (allDone) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            task = task,
                            recordCountByExec = recordCounts,
                            isCompleted = true,
                        )
                    }
                    return@launch
                }

                val currentExec = task.executions[currentIdx]
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        task = task,
                        recordCountByExec = recordCounts,
                        currentExecutionIndex = currentIdx,
                        currentSetNumber = currentSet,
                        repsInput = currentExec.targetReps,
                    )
                }
            } catch (e: Throwable) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "로드 실패")
                }
            }
        }
    }

    private fun computeStartPosition(
        task: DailyTask,
        recordCounts: Map<Long, Int>,
    ): Triple<Int, Int, Boolean> {
        for ((idx, exec) in task.executions.withIndex()) {
            val recorded = recordCounts[exec.id] ?: 0
            if (recorded < exec.targetSets) {
                return Triple(idx, recorded + 1, false)
            }
        }
        return Triple(task.executions.lastIndex, 1, true)
    }

    private fun handleSetCompleted() {
        viewModelScope.launch {
            val state = _uiState.value
            val task = state.task ?: return@launch
            val exec = task.executions[state.currentExecutionIndex]
            val isDeadHang = exec.exerciseType == ExerciseType.DEAD_HANG

            try {
                recordSet(
                    taskExecutionId = exec.id,
                    setNumber = state.currentSetNumber,
                    actualReps = if (isDeadHang) 0 else state.repsInput,
                    actualSeconds = if (isDeadHang) state.repsInput else null,
                )
            } catch (e: Throwable) {
                _uiState.update { it.copy(errorMessage = "세트 저장 실패: ${e.message}") }
                return@launch
            }

            // 진행 상태 업데이트
            val newCounts = state.recordCountByExec.toMutableMap().apply {
                this[exec.id] = (this[exec.id] ?: 0) + 1
            }
            val nextSetNumber = state.currentSetNumber + 1

            if (nextSetNumber > exec.targetSets) {
                // 다음 종목으로
                val nextExecIdx = state.currentExecutionIndex + 1
                if (nextExecIdx >= task.executions.size) {
                    _uiState.update {
                        it.copy(
                            recordCountByExec = newCounts,
                            isCompleted = true,
                            restRemainingSeconds = null,
                        )
                    }
                } else {
                    val nextExec = task.executions[nextExecIdx]
                    _uiState.update {
                        it.copy(
                            recordCountByExec = newCounts,
                            currentExecutionIndex = nextExecIdx,
                            currentSetNumber = 1,
                            repsInput = nextExec.targetReps,
                        )
                    }
                    startRestTimer(exec.restSeconds)
                }
            } else {
                // 같은 종목 다음 세트
                _uiState.update {
                    it.copy(
                        recordCountByExec = newCounts,
                        currentSetNumber = nextSetNumber,
                        repsInput = exec.targetReps,
                    )
                }
                startRestTimer(exec.restSeconds)
            }
        }
    }

    private fun startRestTimer(seconds: Int) {
        if (seconds <= 0) return
        restJob?.cancel()
        _uiState.update { it.copy(restRemainingSeconds = seconds) }
        restJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                delay(1_000L)
                remaining -= 1
                _uiState.update {
                    if (it.restRemainingSeconds == null) it    // skipped
                    else it.copy(restRemainingSeconds = remaining.takeIf { v -> v > 0 })
                }
                if (_uiState.value.restRemainingSeconds == null) break
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        restJob?.cancel()
    }
}
