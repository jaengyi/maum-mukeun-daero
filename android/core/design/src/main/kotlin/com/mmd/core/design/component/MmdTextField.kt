package com.mmd.core.design.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmd.core.design.theme.MmdTheme

/**
 * 단일/멀티라인 텍스트 입력 필드. 에러 메시지 슬롯 포함.
 */
@Composable
fun MmdTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it) } },
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth(),
        )
        if (isError && !errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MmdTextFieldEmptyPreview() {
    MmdTheme { MmdTextField(value = "", onValueChange = {}, label = "닉네임") }
}

@Preview(showBackground = true)
@Composable
private fun MmdTextFieldFilledPreview() {
    MmdTheme { MmdTextField(value = "효욱", onValueChange = {}, label = "닉네임") }
}

@Preview(showBackground = true)
@Composable
private fun MmdTextFieldErrorPreview() {
    MmdTheme {
        MmdTextField(
            value = "10000",
            onValueChange = {},
            label = "키 (cm)",
            isError = true,
            errorMessage = "100~250cm 사이 값을 입력해주세요",
            keyboardType = KeyboardType.Number,
        )
    }
}
