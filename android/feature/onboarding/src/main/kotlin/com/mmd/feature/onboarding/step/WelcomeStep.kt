package com.mmd.feature.onboarding.step

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmd.core.design.component.MmdButton
import com.mmd.core.design.theme.MmdTheme

@Composable
internal fun WelcomeStep(
    onStartClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "🌱",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "마음먹은대로",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = "나만 믿고 따라와.\n네가 마음먹은 작고 소중한\n목표를 이루게 만들어 줄게.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 48.dp),
        )
        MmdButton(text = "시작하기", onClick = onStartClicked)
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeStepPreview() {
    MmdTheme { WelcomeStep(onStartClicked = {}) }
}
