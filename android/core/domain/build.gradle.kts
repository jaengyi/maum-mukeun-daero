plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    implementation(project(":core:common"))
    // simulation의 enum (Gender, TrainingPhase 등)이 도메인 모델 public API에 노출되므로 api
    api(project(":core:simulation"))
    // Flow가 Repository 인터페이스 반환 타입이므로 api
    api(libs.kotlinx.coroutines.core)
}
