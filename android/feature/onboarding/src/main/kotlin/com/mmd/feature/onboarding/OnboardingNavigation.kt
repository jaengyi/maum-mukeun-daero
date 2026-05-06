package com.mmd.feature.onboarding

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val OnboardingRoute = "onboarding"

fun NavGraphBuilder.onboardingScreen(onComplete: () -> Unit) {
    composable(route = OnboardingRoute) {
        OnboardingScreen(onComplete = onComplete)
    }
}
