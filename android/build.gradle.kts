// Top-level build file.
// AGP 9.0+ has built-in Kotlin for Android modules — kotlin-android plugin is no longer needed.
// kotlin-jvm is still required for pure-Kotlin modules (e.g. core:common, core:simulation).
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.androidx.room) apply false
}
