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
import com.mmd.feature.tracker.completion.completionRouteFor
import com.mmd.feature.tracker.completion.completionScreen
import com.mmd.feature.tracker.workout.WorkoutRoute
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
                    onComplete = { taskId ->
                        // S8 → S9: workout 백스택에서 제거하고 completion으로 이동
                        navController.navigate(completionRouteFor(taskId)) {
                            popUpTo(WorkoutRoute) { inclusive = true }
                        }
                    },
                    onCancel = { navController.popBackStack() },
                )
                completionScreen(
                    onConfirm = {
                        // S9 → Main(홈)으로 복귀
                        navController.popBackStack()
                    },
                )
            }
        }
    }
}
