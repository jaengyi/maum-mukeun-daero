package com.mmd.feature.onboarding

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            OnboardingEvent.NextClicked -> handleNext()
            OnboardingEvent.BackClicked -> handleBack()
            is OnboardingEvent.NicknameChanged ->
                _uiState.update { it.copy(nickname = event.value, nicknameError = null) }
            is OnboardingEvent.GenderChanged ->
                _uiState.update { it.copy(gender = event.value) }
            is OnboardingEvent.BirthYearChanged ->
                _uiState.update { it.copy(birthYearText = event.value, birthYearError = null) }
            is OnboardingEvent.HeightChanged ->
                _uiState.update { it.copy(heightCmText = event.value, heightError = null) }
            is OnboardingEvent.WeightChanged ->
                _uiState.update { it.copy(weightKgText = event.value, weightError = null) }
            is OnboardingEvent.CurrentMaxPullupsChanged ->
                _uiState.update { it.copy(currentMaxPullups = event.value.coerceIn(0, 30)) }
            is OnboardingEvent.CurrentDeadHangChanged ->
                _uiState.update { it.copy(currentDeadHangSec = event.value.coerceIn(0, 120)) }
            is OnboardingEvent.DayToggled -> _uiState.update {
                val updated = if (event.day in it.availableDays) {
                    it.availableDays - event.day
                } else {
                    it.availableDays + event.day
                }
                it.copy(availableDays = updated, daysError = null)
            }
            is OnboardingEvent.DisclaimerAgreementChanged ->
                _uiState.update { it.copy(agreedToDisclaimer = event.value) }
        }
    }

    private fun handleNext() {
        val state = _uiState.value
        when (state.step) {
            OnboardingStep.Welcome -> _uiState.update { it.copy(step = OnboardingStep.BodyInfo) }
            OnboardingStep.BodyInfo -> {
                val withErrors = applyBodyInfoValidation(state)
                if (!withErrors.hasBodyInfoErrors()) {
                    _uiState.update { it.copy(step = OnboardingStep.Capability) }
                } else {
                    _uiState.update { withErrors }
                }
            }
            OnboardingStep.Capability ->
                _uiState.update { it.copy(step = OnboardingStep.Days) }
            OnboardingStep.Days -> {
                if (state.availableDays.isEmpty()) {
                    _uiState.update { it.copy(daysError = "최소 1일 이상 선택해주세요.") }
                } else {
                    _uiState.update { it.copy(step = OnboardingStep.Disclaimer) }
                }
            }
            OnboardingStep.Disclaimer -> {
                if (state.agreedToDisclaimer) {
                    _uiState.update { it.copy(isCompleted = true) }
                }
            }
        }
    }

    private fun handleBack() {
        if (_uiState.value.step != OnboardingStep.Welcome) {
            _uiState.update { it.copy(step = it.step.previous()) }
        }
    }

    private fun applyBodyInfoValidation(state: OnboardingUiState): OnboardingUiState {
        val nicknameErr = when {
            state.nickname.isBlank() -> "닉네임을 입력해주세요."
            state.nickname.length > 20 -> "20자 이내로 입력해주세요."
            else -> null
        }
        val birthYear = state.birthYearText.toIntOrNull()
        val currentYear = LocalDate.now().year
        val birthErr = when {
            birthYear == null -> "올바른 연도를 입력해주세요."
            birthYear !in 1920..currentYear -> "1920~$currentYear 사이 값을 입력해주세요."
            else -> null
        }
        val height = state.heightCmText.toIntOrNull()
        val heightErr = when {
            height == null -> "키를 입력해주세요."
            height !in 100..250 -> "100~250 cm 사이 값을 입력해주세요."
            else -> null
        }
        val weight = state.weightKgText.toFloatOrNull()
        val weightErr = when {
            weight == null -> "몸무게를 입력해주세요."
            weight < 30f || weight > 200f -> "30~200 kg 사이 값을 입력해주세요."
            else -> null
        }
        return state.copy(
            nicknameError = nicknameErr,
            birthYearError = birthErr,
            heightError = heightErr,
            weightError = weightErr,
        )
    }

    private fun OnboardingUiState.hasBodyInfoErrors(): Boolean =
        nicknameError != null || birthYearError != null || heightError != null || weightError != null
}
