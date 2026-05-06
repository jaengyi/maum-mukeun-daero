package com.mmd.feature.onboarding.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun OnboardingTopBar(
    indicator: Int?,
    total: Int,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
    ) {
        IconButton(
            onClick = onBackClicked,
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "이전",
            )
        }
        if (indicator != null) {
            Text(
                text = "$indicator / $total",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        // 우측 균형용 빈 공간 (back button 너비만큼)
        Spacer(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(48.dp),
        )
    }
}
