package com.mmd.feature.tracker

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val TrackerRoute = "tracker"

fun NavGraphBuilder.trackerScreen() {
    composable(route = TrackerRoute) { TrackerScreen() }
}
