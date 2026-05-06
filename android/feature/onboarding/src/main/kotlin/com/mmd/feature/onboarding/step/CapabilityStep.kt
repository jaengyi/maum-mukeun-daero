package com.mmd.feature.onboarding.step

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mmd.core.design.component.MmdButton
import com.mmd.feature.onboarding.OnboardingEvent
import com.mmd.feature.onboarding.OnboardingUiState

@Composable
internal fun CapabilityStep(
    state: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text("현재 능력을 알려주세요", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "솔직하게 입력할수록 계획이 정확해집니다.",
            style = MaterialTheme.typography.bodyMedium,
        )

        Stepper(
            label = "지금 한 번에 가능한 풀업 횟수",
            value = state.currentMaxPullups,
            min = 0,
            max = 30,
            onChange = { onEvent(OnboardingEvent.CurrentMaxPullupsChanged(it)) },
        )

        Stepper(
            label = "매달리기 가능 시간 (초)",
            value = state.currentDeadHangSec,
            min = 0,
            max = 120,
            onChange = { onEvent(OnboardingEvent.CurrentDeadHangChanged(it)) },
        )

        MmdButton(
            text = "다음",
            onClick = { onEvent(OnboardingEvent.NextClicked) },
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
private fun Stepper(
    label: String,
    value: Int,
    min: Int,
    max: Int,
    onChange: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            IconButton(
                onClick = { onChange((value - 1).coerceAtLeast(min)) },
                enabled = value > min,
            ) {
                Text("−", style = MaterialTheme.typography.headlineMedium)
            }
            Box(
                modifier = Modifier.size(width = 64.dp, height = 48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$value",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )
            }
            IconButton(
                onClick = { onChange((value + 1).coerceAtMost(max)) },
                enabled = value < max,
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}
