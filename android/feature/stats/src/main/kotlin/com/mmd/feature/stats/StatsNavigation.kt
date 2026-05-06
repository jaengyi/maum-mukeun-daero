package com.mmd.feature.stats

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val StatsRoute = "stats"

fun NavGraphBuilder.statsScreen() {
    composable(route = StatsRoute) { StatsScreen() }
}
