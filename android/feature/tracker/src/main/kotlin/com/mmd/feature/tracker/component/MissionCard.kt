package com.mmd.feature.tracker.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mmd.core.design.component.MmdButton
import com.mmd.core.design.component.MmdCard
import com.mmd.core.domain.model.DailyTask
import com.mmd.core.simulation.Intensity

@Composable
internal fun MissionCard(
    task: DailyTask,
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MmdCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "🌱 오늘의 미션",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "강도: ${intensityDots(task.intensity)}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "(${intensityLabel(task.intensity)})",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = task.summary,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.height(16.dp))
        MmdButton(text = "운동 시작하기", onClick = onStartWorkout)
    }
}

private fun intensityDots(intensity: Intensity): String = when (intensity) {
    Intensity.LIGHT -> "●○○"
    Intensity.MODERATE -> "●●○"
    Intensity.HARD -> "●●●"
    Intensity.REST -> "○○○"
}

private fun intensityLabel(intensity: Intensity): String = when (intensity) {
    Intensity.LIGHT -> "가볍게"
    Intensity.MODERATE -> "보통"
    Intensity.HARD -> "강하게"
    Intensity.REST -> "휴식"
}
