package com.mmd.feature.plan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mmd.core.design.component.MmdGreetingCard
import com.mmd.core.design.theme.MmdTheme

/**
 * 계획 placeholder. Phase 2에서 시뮬레이션 결과 표시, Phase 4에서 전체 12주 계획 화면으로 확장.
 * Bottom Nav에는 노출 안 됨 — Tracker/Stats 화면에서 navigate로 진입.
 */
@Composable
fun PlanScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        MmdGreetingCard(message = "계획 (Plan)")
    }
}

@Preview(showBackground = true)
@Composable
private fun PlanScreenPreview() {
    MmdTheme { PlanScreen() }
}
