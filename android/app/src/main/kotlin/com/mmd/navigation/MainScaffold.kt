package com.mmd.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.mmd.feature.plan.planScreen
import com.mmd.feature.settings.settingsScreen
import com.mmd.feature.stats.statsScreen
import com.mmd.feature.tracker.trackerScreen

/**
 * Main 그래프 — Bottom Nav 3탭 + Plan(non-tab, 다른 화면에서 navigate).
 */
@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { MmdBottomBar(navController) },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = MainRoute.Tracker.route,
            modifier = Modifier.padding(padding),
        ) {
            trackerScreen(
                onStartWorkout = { _ ->
                    // chunk 3.3에서 navController.navigate(WorkoutRoute) wiring 추가 예정
                },
            )
            statsScreen()
            settingsScreen()
            planScreen()    // Bottom Nav에는 없지만 Tracker/Stats에서 navigate 가능
        }
    }
}
