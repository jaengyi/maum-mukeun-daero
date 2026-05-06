package com.mmd.core.design.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmd.core.design.theme.MmdTheme

/**
 * 기본 강조 버튼. primary 색상 사용.
 *
 * @param isLoading true면 텍스트 대신 progress indicator 표시 + onClick 무시 (시각적으로는 active 유지)
 * @param enabled false면 비활성 (회색) 처리
 */
@Composable
fun MmdButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    Button(
        onClick = { if (!isLoading) onClick() },
        enabled = enabled,
        modifier = modifier,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        } else {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MmdButtonDefaultPreview() {
    MmdTheme { MmdButton(text = "이대로 시작하기", onClick = {}) }
}

@Preview(showBackground = true)
@Composable
private fun MmdButtonLoadingPreview() {
    MmdTheme { MmdButton(text = "이대로 시작하기", onClick = {}, isLoading = true) }
}

@Preview(showBackground = true)
@Composable
private fun MmdButtonDisabledPreview() {
    MmdTheme { MmdButton(text = "이대로 시작하기", onClick = {}, enabled = false) }
}

@Preview(showBackground = true)
@Composable
private fun MmdButtonDarkPreview() {
    MmdTheme(darkTheme = true) { MmdButton(text = "이대로 시작하기", onClick = {}) }
}
