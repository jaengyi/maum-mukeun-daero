package com.mmd.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mmd.core.design.theme.MmdTheme
import com.mmd.feature.onboarding.onboardingScreen

/**
 * 앱 진입 — Top-level NavHost.
 * 현재는 항상 Onboarding으로 시작. Phase 2에서 "온보딩 완료 여부" DataStore 플래그 도입 시 분기.
 */
@Composable
fun MmdApp() {
    MmdTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = TopLevelRoute.Onboarding.route,
            ) {
                onboardingScreen(
                    onComplete = {
                        navController.navigate(TopLevelRoute.Main.route) {
                            popUpTo(TopLevelRoute.Onboarding.route) { inclusive = true }
                        }
                    },
                )
                composable(TopLevelRoute.Main.route) {
                    MainScaffold()
                }
            }
        }
    }
}
