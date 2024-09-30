pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("jvm").version(extra["kotlin.version"] as String)
        id("app.cash.sqldelight").version(extra["sqldelight.version"] as String)
    }
}

rootProject.name = "local-store-module"

include(":hash-module")
