package com.mmd.feature.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val SettingsRoute = "settings"

fun NavGraphBuilder.settingsScreen() {
    composable(route = SettingsRoute) { SettingsScreen() }
}
