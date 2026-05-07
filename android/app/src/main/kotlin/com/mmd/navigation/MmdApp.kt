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
import com.mmd.feature.tracker.workout.workoutRouteFor
import com.mmd.feature.tracker.workout.workoutScreen

/**
 * 앱 진입 — Top-level NavHost.
 * 현재는 항상 Onboarding으로 시작. Phase 6에서 "온보딩 완료 여부" 플래그 도입 시 분기.
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
                    MainScaffold(
                        onStartWorkout = { taskId ->
                            navController.navigate(workoutRouteFor(taskId))
                        },
                    )
                }
                workoutScreen(
                    onComplete = {
                        // chunk 3.4: S9 완료 화면으로 이동.
                        // 현재는 단순 popBackStack으로 Main(홈)으로 복귀.
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() },
                )
            }
        }
    }
}
