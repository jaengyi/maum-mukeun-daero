package com.mmd.core.design.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
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
    Card(modifier = modifier) {
        Text(
            text = message,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(24.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MmdGreetingCardPreview() {
    MmdTheme {
        MmdGreetingCard(message = "마음먹은대로")
    }
}
