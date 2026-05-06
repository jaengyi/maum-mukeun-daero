package com.mmd.feature.onboarding.step

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mmd.core.design.component.MmdButton
import com.mmd.feature.onboarding.OnboardingEvent
import com.mmd.feature.onboarding.OnboardingUiState
import java.time.DayOfWeek

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DaysStep(
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
        Text("언제 운동할 수 있나요?", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "주 3~4회를 추천드려요.",
            style = MaterialTheme.typography.bodyMedium,
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DayOfWeek.entries.forEach { day ->
                FilterChip(
                    selected = day in state.availableDays,
                    onClick = { onEvent(OnboardingEvent.DayToggled(day)) },
                    label = { Text(dayLabel(day)) },
                )
            }
        }

        Text(
            text = if (state.availableDays.isEmpty()) {
                "선택된 요일이 없어요"
            } else {
                "선택: " + state.availableDays.sortedBy { it.value }.joinToString(" / ") { dayLabel(it) }
            },
            style = MaterialTheme.typography.bodyMedium,
        )

        if (state.daysError != null) {
            Text(
                text = state.daysError,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        MmdButton(
            text = "다음",
            onClick = { onEvent(OnboardingEvent.NextClicked) },
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

private fun dayLabel(day: DayOfWeek): String = when (day) {
    DayOfWeek.MONDAY -> "월"
    DayOfWeek.TUESDAY -> "화"
    DayOfWeek.WEDNESDAY -> "수"
    DayOfWeek.THURSDAY -> "목"
    DayOfWeek.FRIDAY -> "금"
    DayOfWeek.SATURDAY -> "토"
    DayOfWeek.SUNDAY -> "일"
}
