pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("jvm").version(extra["kotlin.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
        id("app.cash.sqldelight").version("2.0.2")
    }
}

rootProject.name = "ethernet-application-reaction-test"

include(":authorization-ui-module")
include(":theme-ui-module")
include(":add-new-user-ui-module")
include(":all-users-ui-module")
include(":privacy-policy-ui-module")
include(":tests-ui-module")
include(":about-user-ui-module")
include(":picker-ui-module")

include(":local-store-module")
