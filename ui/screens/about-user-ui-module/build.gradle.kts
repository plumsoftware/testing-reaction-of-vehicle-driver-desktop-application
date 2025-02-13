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

//    Charts
    implementation(globalVersions.chart)

//    Modules
    implementation(project(path = ":client-core:local-store-module"))
    implementation(project(path = ":ui:core:theme-ui-module"))
}

tasks.test {
    useJUnitPlatform()
}
