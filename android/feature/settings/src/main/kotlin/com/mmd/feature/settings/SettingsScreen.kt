package com.mmd.feature.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mmd.core.design.component.MmdGreetingCard
import com.mmd.core.design.theme.MmdTheme

/**
 * 설정 placeholder. Phase 6에서 프로파일 / 알림 / 백업 등으로 확장.
 */
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        MmdGreetingCard(message = "설정 (Settings)")
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    MmdTheme { SettingsScreen() }
}
