package com.mmd.feature.onboarding.step

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mmd.core.design.component.MmdButton
import com.mmd.core.design.component.MmdSegmentedControl
import com.mmd.core.simulation.IntensityPreference
import com.mmd.feature.onboarding.OnboardingEvent
import com.mmd.feature.onboarding.OnboardingUiState
import com.mmd.feature.onboarding.component.SimulationLineChart

@Composable
internal fun SimulationResultStep(
    state: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val result = state.simulationResult ?: return

    val intensityOptions = remember { listOf("천천히", "기본", "빠르게") }
    val nicknameLabel = state.nickname.ifBlank { "당신" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("${nicknameLabel}님의 여정", style = MaterialTheme.typography.headlineMedium)

        Column {
            Text(
                text = "✨ 약 ${result.totalWeeks}주 후",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = "턱걸이 10개 가능",
                style = MaterialTheme.typography.titleLarge,
            )
        }

        SimulationLineChart(
            weeklyTargetReps = result.weeklyPlans.map { it.weekNumber to it.targetMaxReps },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
        )

        if (result.expectedMilestones.isNotEmpty()) {
            Text("주요 마일스톤", style = MaterialTheme.typography.titleLarge)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                result.expectedMilestones.forEach { milestone ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("•", modifier = Modifier.padding(end = 8.dp))
                        Text(
                            text = "Week ${milestone.expectedWeek} — ${milestoneLabel(milestone.code)}",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }

        if (result.notes.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            result.notes.forEach { note ->
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text("강도 조절", style = MaterialTheme.typography.labelLarge)
            MmdSegmentedControl(
                options = intensityOptions,
                selectedIndex = state.intensityPreference.ordinal,
                onSelectionChange = { idx ->
                    onEvent(OnboardingEvent.IntensityChanged(IntensityPreference.entries[idx]))
                },
            )
        }

        if (state.saveError != null) {
            Text(
                text = state.saveError,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        MmdButton(
            text = if (state.isSaving) "저장 중..." else "이대로 시작하기",
            onClick = { onEvent(OnboardingEvent.SaveAndStart) },
            isLoading = state.isSaving,
            enabled = !state.isSaving,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

private fun milestoneLabel(code: String): String = when (code) {
    "FIRST_PULLUP" -> "첫 풀업 1개 ✨"
    "PULLUP_3" -> "풀업 3개"
    "PULLUP_5" -> "풀업 5개"
    "PULLUP_10" -> "풀업 10개 🏆"
    else -> code
}
