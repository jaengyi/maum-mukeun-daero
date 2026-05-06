package com.mmd.core.design.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmd.core.design.theme.MmdTheme

@Composable
fun MmdGreetingCard(
    message: String,
    modifier: Modifier = Modifier,
) {
    MmdCard(modifier = modifier, contentPadding = PaddingValues(24.dp)) {
        Text(text = message, style = MaterialTheme.typography.headlineMedium)
    }
}

@Preview(showBackground = true)
@Composable
private fun MmdGreetingCardPreview() {
    MmdTheme { MmdGreetingCard(message = "마음먹은대로") }
}
