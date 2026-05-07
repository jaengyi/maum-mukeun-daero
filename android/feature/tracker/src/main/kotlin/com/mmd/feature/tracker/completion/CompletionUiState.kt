package com.mmd.feature.tracker.completion

data class CompletionUiState(
    val conditionScore: Int? = null,             // 1..5, null = 미선택
    val note: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isCompleted: Boolean = false,
)

sealed interface CompletionEvent {
    data class ConditionSelected(val score: Int) : CompletionEvent
    data class NoteChanged(val value: String) : CompletionEvent
    data object Confirm : CompletionEvent
}
