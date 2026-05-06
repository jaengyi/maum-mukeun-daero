package com.mmd.feature.tracker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mmd.core.design.component.MmdGreetingCard
import com.mmd.core.design.theme.MmdTheme

/**
 * 일일 트래커 placeholder. Phase 3에서 오늘의 미션 카드로 확장.
 */
@Composable
fun TrackerScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        MmdGreetingCard(message = "홈 (Tracker)")
    }
}

@Preview(showBackground = true)
@Composable
private fun TrackerScreenPreview() {
    MmdTheme { TrackerScreen() }
}
