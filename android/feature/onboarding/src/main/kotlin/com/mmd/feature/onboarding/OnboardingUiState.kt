package com.mmd.feature.onboarding

import com.mmd.core.simulation.Gender
import com.mmd.core.simulation.IntensityPreference
import com.mmd.core.simulation.SimulationResult
import java.time.DayOfWeek
import java.time.LocalDate

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.Welcome,

    // S2 Body Info
    val nickname: String = "",
    val gender: Gender = Gender.MALE,
    val birthYearText: String = (LocalDate.now().year - 30).toString(),
    val heightCmText: String = "",
    val weightKgText: String = "",

    // S3 Capability
    val currentMaxPullups: Int = 0,
    val currentDeadHangSec: Int = 0,

    // S4 Days
    val availableDays: Set<DayOfWeek> = emptySet(),

    // S5 Disclaimer
    val agreedToDisclaimer: Boolean = false,

    // S6 Simulation Result
    val intensityPreference: IntensityPreference = IntensityPreference.NORMAL,
    val simulationResult: SimulationResult? = null,
    val isSaving: Boolean = false,
    val saveError: String? = null,

    // Validation errors
    val nicknameError: String? = null,
    val birthYearError: String? = null,
    val heightError: String? = null,
    val weightError: String? = null,
    val daysError: String? = null,

    // Completion signal
    val isCompleted: Boolean = false,
)

enum class OnboardingStep(val indicator: Int?) {
    Welcome(null),
    BodyInfo(1),
    Capability(2),
    Days(3),
    Disclaimer(4),
    SimulationResult(null),                // S6 — 인디케이터 안 보임
    ;

    fun next(): OnboardingStep = entries.getOrNull(ordinal + 1) ?: this

    fun previous(): OnboardingStep = entries.getOrNull(ordinal - 1) ?: this

    companion object {
        const val TOTAL = 4
    }
}

sealed interface OnboardingEvent {
    data object NextClicked : OnboardingEvent
    data object BackClicked : OnboardingEvent
    data class NicknameChanged(val value: String) : OnboardingEvent
    data class GenderChanged(val value: Gender) : OnboardingEvent
    data class BirthYearChanged(val value: String) : OnboardingEvent
    data class HeightChanged(val value: String) : OnboardingEvent
    data class WeightChanged(val value: String) : OnboardingEvent
    data class CurrentMaxPullupsChanged(val value: Int) : OnboardingEvent
    data class CurrentDeadHangChanged(val value: Int) : OnboardingEvent
    data class DayToggled(val day: DayOfWeek) : OnboardingEvent
    data class DisclaimerAgreementChanged(val value: Boolean) : OnboardingEvent
    data class IntensityChanged(val value: IntensityPreference) : OnboardingEvent
    data object SaveAndStart : OnboardingEvent
}
