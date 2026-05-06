package com.mmd.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmd.core.design.component.MmdButton
import com.mmd.core.design.theme.MmdTheme

/**
 * Phase 2에서 다단계 온보딩(S1~S6)으로 확장 예정.
 * 현 단계는 placeholder — 환영 메시지 + "시작하기" 버튼.
 */
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "마음먹은대로",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "작심한 목표를 시뮬레이션 기반으로 습관화시키는 앱입니다.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
        )
        MmdButton(text = "시작하기", onClick = onComplete)
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    MmdTheme { OnboardingScreen(onComplete = {}) }
}
