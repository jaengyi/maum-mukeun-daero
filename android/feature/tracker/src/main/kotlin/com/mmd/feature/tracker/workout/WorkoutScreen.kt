package com.mmd.feature.tracker.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmd.core.design.component.MmdButton
import com.mmd.core.design.component.MmdCard
import com.mmd.core.domain.model.DailyTask
import com.mmd.core.domain.model.TaskExecution
import com.mmd.core.simulation.ExerciseType

@Composable
fun WorkoutScreen(
    onComplete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WorkoutViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) onComplete()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        // Top bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onCancel) {
                Icon(Icons.Filled.Close, contentDescription = "닫기")
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            when {
                state.isLoading -> Text("불러오는 중...")
                state.errorMessage != null -> Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                )
                state.task != null -> WorkoutContent(
                    task = state.task!!,
                    state = state,
                    onEvent = viewModel::onEvent,
                )
            }
        }
    }
}

@Composable
private fun WorkoutContent(
    task: DailyTask,
    state: WorkoutUiState,
    onEvent: (WorkoutEvent) -> Unit,
) {
    val currentExec = task.executions[state.currentExecutionIndex]

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header: exercise name, set number, target
        Text(
            text = exerciseDisplayName(currentExec.exerciseType),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "세트 ${state.currentSetNumber} / ${currentExec.targetSets}",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        val unit = if (currentExec.exerciseType == ExerciseType.DEAD_HANG) "초" else "회"
        Text(
            text = "목표: ${currentExec.targetReps}$unit",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(8.dp))

        if (state.restRemainingSeconds != null) {
            RestingView(
                remainingSeconds = state.restRemainingSeconds!!,
                onSkip = { onEvent(WorkoutEvent.SkipRest) },
            )
        } else {
            SetInputView(
                value = state.repsInput,
                unit = unit,
                onDecrement = { onEvent(WorkoutEvent.DecreaseReps) },
                onIncrement = { onEvent(WorkoutEvent.IncreaseReps) },
                onComplete = { onEvent(WorkoutEvent.SetCompleted) },
            )
        }

        Spacer(Modifier.height(16.dp))

        SetProgressDots(
            executions = task.executions,
            recordCountByExec = state.recordCountByExec,
            currentExecutionIndex = state.currentExecutionIndex,
            currentSetNumber = state.currentSetNumber,
        )
    }
}

@Composable
private fun SetInputView(
    value: Int,
    unit: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onComplete: () -> Unit,
) {
    MmdCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onDecrement, enabled = value > 0) {
                Text("−", fontSize = 36.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = "$value",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            IconButton(onClick = onIncrement) {
                Text("+", fontSize = 36.sp, fontWeight = FontWeight.Bold)
            }
        }
        Text(
            text = "단위: $unit",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
    Spacer(Modifier.height(16.dp))
    MmdButton(
        text = "세트 완료",
        onClick = onComplete,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun RestingView(
    remainingSeconds: Int,
    onSkip: () -> Unit,
) {
    MmdCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "휴식 중",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Text(
            text = "⏱ ${formatTime(remainingSeconds)}",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )
    }
    Spacer(Modifier.height(16.dp))
    MmdButton(
        text = "건너뛰기",
        onClick = onSkip,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun SetProgressDots(
    executions: List<TaskExecution>,
    recordCountByExec: Map<Long, Int>,
    currentExecutionIndex: Int,
    currentSetNumber: Int,
) {
    val primary = MaterialTheme.colorScheme.primary
    val outline = MaterialTheme.colorScheme.outline
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        executions.forEachIndexed { execIdx, exec ->
            repeat(exec.targetSets) { setIdx ->
                val color = when {
                    execIdx < currentExecutionIndex -> primary
                    execIdx > currentExecutionIndex -> outline.copy(alpha = 0.3f)
                    setIdx + 1 < currentSetNumber -> primary
                    setIdx + 1 == currentSetNumber -> primary.copy(alpha = 0.6f)
                    else -> outline.copy(alpha = 0.3f)
                }
                Dot(color = color)
                if (setIdx < exec.targetSets - 1) Spacer(Modifier.width(4.dp))
            }
            if (execIdx < executions.size - 1) Spacer(Modifier.width(12.dp))
        }
    }
}

@Composable
private fun Dot(color: Color) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color),
    )
}

private fun exerciseDisplayName(type: ExerciseType): String = when (type) {
    ExerciseType.PULLUP -> "풀업"
    ExerciseType.ASSISTED_PULLUP -> "어시스트 풀업"
    ExerciseType.NEGATIVE -> "네거티브"
    ExerciseType.AUSTRALIAN_PULLUP -> "호주 풀업"
    ExerciseType.DEAD_HANG -> "매달리기"
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}
