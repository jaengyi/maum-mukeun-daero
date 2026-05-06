package com.mmd.core.design.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmd.core.design.theme.MmdTheme

/**
 * 일반 카드 컨테이너. Material3 Card + Column wrapper.
 * contentPadding으로 내부 여백 조정.
 */
@Composable
fun MmdCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}

@Preview(showBackground = true)
@Composable
private fun MmdCardPreview() {
    MmdTheme {
        MmdCard {
            Text("제목", style = MaterialTheme.typography.titleLarge)
            Text("본문 텍스트", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MmdCardDarkPreview() {
    MmdTheme(darkTheme = true) {
        MmdCard {
            Text("제목", style = MaterialTheme.typography.titleLarge)
            Text("본문 텍스트", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
