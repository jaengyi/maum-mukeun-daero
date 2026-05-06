package com.mmd.feature.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mmd.feature.onboarding.component.OnboardingTopBar
import com.mmd.feature.onboarding.step.BodyInfoStep
import com.mmd.feature.onboarding.step.CapabilityStep
import com.mmd.feature.onboarding.step.DaysStep
import com.mmd.feature.onboarding.step.DisclaimerStep
import com.mmd.feature.onboarding.step.WelcomeStep

/**
 * 5-step 마법사 — 단일 ViewModel이 form state 보유.
 * S5 "계획 만들기" → uiState.isCompleted = true → onComplete() 콜백 (chunk 2.4가 데이터 소비).
 */
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) onComplete()
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (state.step != OnboardingStep.Welcome) {
            OnboardingTopBar(
                indicator = state.step.indicator,
                total = OnboardingStep.TOTAL,
                onBackClicked = { viewModel.onEvent(OnboardingEvent.BackClicked) },
            )
        }

        when (state.step) {
            OnboardingStep.Welcome -> WelcomeStep(
                onStartClicked = { viewModel.onEvent(OnboardingEvent.NextClicked) },
            )
            OnboardingStep.BodyInfo -> BodyInfoStep(state = state, onEvent = viewModel::onEvent)
            OnboardingStep.Capability -> CapabilityStep(state = state, onEvent = viewModel::onEvent)
            OnboardingStep.Days -> DaysStep(state = state, onEvent = viewModel::onEvent)
            OnboardingStep.Disclaimer -> DisclaimerStep(state = state, onEvent = viewModel::onEvent)
        }
    }
}
