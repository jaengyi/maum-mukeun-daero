package com.mmd.navigation

/**
 * 최상위 그래프 라우트 — Onboarding ↔ Main 분기.
 * Onboarding 라우트 자체는 feature:onboarding이 OnboardingRoute로 노출.
 */
sealed class TopLevelRoute(val route: String) {
    data object Onboarding : TopLevelRoute("onboarding")
    data object Main : TopLevelRoute("main")
}
