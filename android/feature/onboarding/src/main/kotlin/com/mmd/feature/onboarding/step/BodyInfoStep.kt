package com.mmd.feature.onboarding.step

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mmd.core.design.component.MmdButton
import com.mmd.core.design.component.MmdSegmentedControl
import com.mmd.core.design.component.MmdTextField
import com.mmd.core.simulation.Gender
import com.mmd.feature.onboarding.OnboardingEvent
import com.mmd.feature.onboarding.OnboardingUiState

@Composable
internal fun BodyInfoStep(
    state: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scroll = rememberScrollState()
    val genderOptions = remember { listOf("남성", "여성", "기타") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("당신을 알려주세요", style = MaterialTheme.typography.headlineMedium)

        MmdTextField(
            value = state.nickname,
            onValueChange = { onEvent(OnboardingEvent.NicknameChanged(it)) },
            label = "닉네임",
            isError = state.nicknameError != null,
            errorMessage = state.nicknameError,
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("성별", style = MaterialTheme.typography.labelLarge)
            MmdSegmentedControl(
                options = genderOptions,
                selectedIndex = state.gender.ordinal,
                onSelectionChange = { onEvent(OnboardingEvent.GenderChanged(Gender.entries[it])) },
            )
        }

        MmdTextField(
            value = state.birthYearText,
            onValueChange = { onEvent(OnboardingEvent.BirthYearChanged(it)) },
            label = "태어난 해",
            keyboardType = KeyboardType.Number,
            isError = state.birthYearError != null,
            errorMessage = state.birthYearError,
        )

        MmdTextField(
            value = state.heightCmText,
            onValueChange = { onEvent(OnboardingEvent.HeightChanged(it)) },
            label = "키 (cm)",
            keyboardType = KeyboardType.Number,
            isError = state.heightError != null,
            errorMessage = state.heightError,
        )

        MmdTextField(
            value = state.weightKgText,
            onValueChange = { onEvent(OnboardingEvent.WeightChanged(it)) },
            label = "몸무게 (kg)",
            keyboardType = KeyboardType.Decimal,
            isError = state.weightError != null,
            errorMessage = state.weightError,
        )

        MmdButton(
            text = "다음",
            onClick = { onEvent(OnboardingEvent.NextClicked) },
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}
