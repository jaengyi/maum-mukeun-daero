package com.mmd.feature.plan

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val PlanRoute = "plan"

fun NavGraphBuilder.planScreen() {
    composable(route = PlanRoute) { PlanScreen() }
}
