package com.mmd.feature.tracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmd.core.design.component.MmdCard
import com.mmd.feature.tracker.component.MiniGrassGrid
import com.mmd.feature.tracker.component.MissionCard

@Composable
fun TrackerScreen(
    onStartWorkout: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrackerViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (val s = state) {
            TrackerUiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )
            TrackerUiState.NoActiveGoal -> Text(
                text = "활성 목표가 없어요. 온보딩을 다시 시작해주세요.",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
            )
            is TrackerUiState.CountdownToStart -> CountdownView(s)
            is TrackerUiState.WorkoutDay -> WorkoutDayView(s, onStartWorkout)
            is TrackerUiState.RestDay -> RestDayView(s)
            is TrackerUiState.PlanEnded -> PlanEndedView(s)
        }
    }
}

@Composable
private fun CountdownView(state: TrackerUiState.CountdownToStart) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        MmdCard(modifier = Modifier.padding(top = 8.dp)) {
            Text("계획이 곧 시작돼요!", style = MaterialTheme.typography.titleLarge)
            Text(
                text = "${state.firstWorkoutDate}부터 시작 (${state.daysUntil}일 남음)",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        Text("최근 잔디 (4주)", style = MaterialTheme.typography.titleLarge)
        MiniGrassGrid(cells = state.recentGrass)
    }
}

@Composable
private fun WorkoutDayView(
    state: TrackerUiState.WorkoutDay,
    onStartWorkout: (Long) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Week ${state.weekNumber} / ${state.totalWeeks}",
            style = MaterialTheme.typography.titleLarge,
        )
        MissionCard(
            task = state.task,
            onStartWorkout = { onStartWorkout(state.task.id) },
        )
        Text("최근 잔디 (4주)", style = MaterialTheme.typography.titleLarge)
        MiniGrassGrid(cells = state.recentGrass)
    }
}

@Composable
private fun RestDayView(state: TrackerUiState.RestDay) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        MmdCard {
            Text("🌙 오늘은 회복일", style = MaterialTheme.typography.titleLarge)
            Text(
                text = state.message,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        Text("최근 잔디 (4주)", style = MaterialTheme.typography.titleLarge)
        MiniGrassGrid(cells = state.recentGrass)
    }
}

@Composable
private fun PlanEndedView(state: TrackerUiState.PlanEnded) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        MmdCard {
            Text("🏆 계획 종료", style = MaterialTheme.typography.titleLarge)
            Text(
                text = "12주 계획이 완료됐어요! 새로운 목표 도전은 곧 추가될 예정이에요.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        Text("지난 잔디 (4주)", style = MaterialTheme.typography.titleLarge)
        MiniGrassGrid(cells = state.recentGrass)
    }
}
