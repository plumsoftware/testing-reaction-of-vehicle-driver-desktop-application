plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)

    implementation(globalVersions.material3)

//    Precompose
    api(globalVersions.precompose.viewmodel)
    api(globalVersions.precompose)

//    Modules
    implementation(project(":theme-ui-module"))
    implementation(project(":config-module"))
}
