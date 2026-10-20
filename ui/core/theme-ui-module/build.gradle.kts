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
    implementation(project(path = ":client-core:local-store-module"))

    implementation(compose.desktop.currentOs)

    implementation(globalVersions.material3)
    implementation(globalVersions.material.icons.core)

//    JUnit
    testImplementation(globalVersions.junit)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
