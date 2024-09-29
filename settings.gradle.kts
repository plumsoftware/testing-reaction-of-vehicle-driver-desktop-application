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
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("globalVersions") {
            from(files("gradle/globalVersions.versions.toml"))
        }
    }
}

rootProject.name = "testing-reaction-of-vehicle-driver-desktop-application"

include(":offline-application-hybrid")
include(":ethernet-application-reaction-test")
include(":ethernet-application-admin")

include(":hash-module")
include(":local-store-module")
include(":cryptography-module")

include(":authorization-ui-module")
include(":theme-ui-module")
include(":add-new-user-ui-module")
include(":all-users-ui-module")
include(":privacy-policy-ui-module")
include(":about-user-ui-module")
include(":picker-ui-module")
include(":tests-ui-module")
include(":test-menu-ui-module")
