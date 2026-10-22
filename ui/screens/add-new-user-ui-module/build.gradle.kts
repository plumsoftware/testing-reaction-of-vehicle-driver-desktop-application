plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    kotlin("plugin.compose")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(globalVersions.material3)
    implementation(globalVersions.material.icons.core)

//    Precompose
    api(globalVersions.precompose.viewmodel)
    api(globalVersions.precompose)

//    Modules
    implementation(project(path = ":client-core:local-store-module"))
    implementation(project(path = ":ui:core:theme-ui-module"))
}

tasks.test {
    useJUnitPlatform()
}
