package com.mmd.core.design.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mmd.core.design.theme.MmdTheme

/**
 * 단일 선택 세그먼트 컨트롤. 강도 조절(천천히/기본/빠르게) 등에 사용.
 */
@Composable
fun MmdSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelectionChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                selected = index == selectedIndex,
                onClick = { onSelectionChange(index) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                label = { Text(text = label, style = MaterialTheme.typography.labelMedium) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MmdSegmentedControlThreeOptionsPreview() {
    MmdTheme {
        var selected by remember { mutableIntStateOf(1) }
        MmdSegmentedControl(
            options = listOf("천천히", "기본", "빠르게"),
            selectedIndex = selected,
            onSelectionChange = { selected = it },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MmdSegmentedControlTwoOptionsPreview() {
    MmdTheme {
        var selected by remember { mutableIntStateOf(0) }
        MmdSegmentedControl(
            options = listOf("운동일", "휴식일"),
            selectedIndex = selected,
            onSelectionChange = { selected = it },
        )
    }
}
