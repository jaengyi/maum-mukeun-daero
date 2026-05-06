package com.mmd.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.mmd.feature.settings.SettingsRoute
import com.mmd.feature.stats.StatsRoute
import com.mmd.feature.tracker.TrackerRoute

/**
 * Main 그래프 내 라우트. 라우트 문자열은 각 feature 모듈이 single source of truth로 보유.
 */
sealed class MainRoute(val route: String, val label: String, val icon: ImageVector) {
    data object Tracker : MainRoute(TrackerRoute, "홈", Icons.Filled.Home)
    data object Stats : MainRoute(StatsRoute, "통계", Icons.Filled.DateRange)
    data object SettingsTab : MainRoute(SettingsRoute, "설정", Icons.Filled.Settings)

    companion object {
        val bottomNavItems: List<MainRoute> = listOf(Tracker, Stats, SettingsTab)
    }
}
