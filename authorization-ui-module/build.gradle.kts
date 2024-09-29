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
    val material3 = "1.2.1"

    implementation(compose.desktop.currentOs)

    implementation("org.jetbrains.compose.material3:material3-desktop:${material3}")

//    Precompose
    api(globalVersions.precompose.viewmodel)
    api(globalVersions.precompose)

//    Modules
    implementation(project(":local-store-module"))
    implementation(project(":theme-ui-module"))
}

tasks.test {
    useJUnitPlatform()
}
