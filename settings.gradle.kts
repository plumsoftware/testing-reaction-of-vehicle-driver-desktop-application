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

include(":desktop-application:offline-application-hybrid")
include(":desktop-application:ethernet-application-reaction-test")
include(":desktop-application:ethernet-application-admin")
include(":desktop-application:generate-code-application")

include(":client-core:hash-module")
include(":client-core:local-store-module")
include(":client-core:cryptography-module")
include(":client-core:config-module")

include(":ui:screens:authorization-ui-module")
include(":ui:core:theme-ui-module")
include(":ui:screens:add-new-user-ui-module")
include(":ui:screens:all-users-ui-module")
include(":ui:screens:privacy-policy-ui-module")
include(":ui:screens:about-user-ui-module")
include(":ui:screens:picker-ui-module")
include(":ui:screens:tests-ui-module")
include(":ui:screens:test-menu-ui-module")
include(":ui:screens:about-app-ui-module")