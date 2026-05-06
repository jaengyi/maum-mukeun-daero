package com.mmd.feature.stats

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mmd.core.design.component.MmdGreetingCard
import com.mmd.core.design.theme.MmdTheme

/**
 * 통계 placeholder. Phase 4에서 잔디 그리드 + 차트로 확장.
 */
@Composable
fun StatsScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        MmdGreetingCard(message = "통계 (Stats)")
    }
}

@Preview(showBackground = true)
@Composable
private fun StatsScreenPreview() {
    MmdTheme { StatsScreen() }
}
