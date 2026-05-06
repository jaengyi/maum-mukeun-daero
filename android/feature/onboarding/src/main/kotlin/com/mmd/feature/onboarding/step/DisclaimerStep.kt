package com.mmd.feature.onboarding.step

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mmd.core.design.component.MmdButton
import com.mmd.feature.onboarding.OnboardingEvent
import com.mmd.feature.onboarding.OnboardingUiState

@Composable
internal fun DisclaimerStep(
    state: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("안전을 위한 안내", style = MaterialTheme.typography.headlineMedium)

        DisclaimerLine("운동 중 통증이나 이상 증상이 있으면 즉시 중단해주세요.")
        DisclaimerLine("특정 질환이 있으신 분은 의료진과 상의해주세요.")
        DisclaimerLine("본 앱의 계획은 일반적인 가이드이며 개인 상황에 따라 조정이 필요해요.")

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Checkbox(
                checked = state.agreedToDisclaimer,
                onCheckedChange = { onEvent(OnboardingEvent.DisclaimerAgreementChanged(it)) },
            )
            Text(
                text = "위 내용을 확인했습니다",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        MmdButton(
            text = "계획 만들기",
            onClick = { onEvent(OnboardingEvent.NextClicked) },
            enabled = state.agreedToDisclaimer,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
private fun DisclaimerLine(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text("•", style = MaterialTheme.typography.bodyLarge)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
