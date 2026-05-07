package com.mmd.feature.tracker.completion

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmd.core.domain.usecase.CompleteTodayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CompletionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val completeToday: CompleteTodayUseCase,
) : ViewModel() {

    private val taskId: Long = checkNotNull(savedStateHandle.get<Long>(CompletionNavArgs.TASK_ID)) {
        "${CompletionNavArgs.TASK_ID} required"
    }

    private val _uiState = MutableStateFlow(CompletionUiState())
    val uiState: StateFlow<CompletionUiState> = _uiState.asStateFlow()

    fun onEvent(event: CompletionEvent) {
        when (event) {
            is CompletionEvent.ConditionSelected ->
                _uiState.update { it.copy(conditionScore = event.score) }
            is CompletionEvent.NoteChanged ->
                _uiState.update { it.copy(note = event.value) }
            CompletionEvent.Confirm -> handleConfirm()
        }
    }

    private fun handleConfirm() {
        val state = _uiState.value
        val score = state.conditionScore ?: return    // 컨디션 미선택이면 무시 (UI에서 버튼 비활성)

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                completeToday(
                    taskId = taskId,
                    date = LocalDate.now(),
                    conditionScore = score,
                    note = state.note.takeIf { it.isNotBlank() },
                )
                Log.i(TAG, "Completed taskId=$taskId conditionScore=$score")
                _uiState.update { it.copy(isSaving = false, isCompleted = true) }
            } catch (e: Throwable) {
                Log.e(TAG, "Complete failed", e)
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = e.message ?: "저장 실패")
                }
            }
        }
    }

    private companion object {
        const val TAG = "Completion"
    }
}
