pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "maum-mukeun-daero"

include(":app")

// core
include(":core:common")
include(":core:design")
include(":core:domain")
include(":core:simulation")
include(":core:database")
include(":core:datastore")
include(":core:data")

// feature
include(":feature:onboarding")
include(":feature:plan")
include(":feature:tracker")
include(":feature:stats")
include(":feature:settings")
