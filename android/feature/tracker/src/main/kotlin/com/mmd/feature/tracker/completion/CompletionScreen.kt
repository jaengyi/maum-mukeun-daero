package com.mmd.feature.tracker.completion

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmd.core.design.component.MmdButton
import com.mmd.core.design.component.MmdCard
import com.mmd.core.design.component.MmdTextField

@Composable
fun CompletionScreen(
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CompletionViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }
    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) onConfirm()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SproutHero()

        Text(
            text = "오늘 미션 완료!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )

        MmdCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "잘했어요. 잔디 한 칸이 채워졌어요. 🌱",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Text(
            text = "컨디션은 어땠나요?",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp),
        )
        ConditionPicker(
            selectedScore = state.conditionScore,
            onSelect = { viewModel.onEvent(CompletionEvent.ConditionSelected(it)) },
        )

        MmdTextField(
            value = state.note,
            onValueChange = { viewModel.onEvent(CompletionEvent.NoteChanged(it)) },
            label = "메모 (선택)",
            singleLine = false,
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage!!,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        MmdButton(
            text = if (state.isSaving) "저장 중..." else "확인",
            onClick = { viewModel.onEvent(CompletionEvent.Confirm) },
            enabled = state.conditionScore != null && !state.isSaving,
            isLoading = state.isSaving,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun SproutHero() {
    var sprouted by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (sprouted) 1.0f else 0.3f,
        animationSpec = tween(durationMillis = 600, easing = EaseOutBack),
        label = "sprout-scale",
    )
    LaunchedEffect(Unit) { sprouted = true }

    Text(
        text = "🌳",
        fontSize = 96.sp,
        modifier = Modifier
            .padding(top = 16.dp)
            .scale(scale),
    )
}

@Composable
private fun ConditionPicker(
    selectedScore: Int?,
    onSelect: (Int) -> Unit,
) {
    val emojis = listOf("😩", "😐", "🙂", "😊", "💪")
    val primary = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        emojis.forEachIndexed { idx, emoji ->
            val score = idx + 1
            val isSelected = selectedScore == score
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) primary.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.surface,
                    )
                    .clickable { onSelect(score) },
                contentAlignment = Alignment.Center,
            ) {
                Text(text = emoji, fontSize = 32.sp)
            }
        }
    }
}
