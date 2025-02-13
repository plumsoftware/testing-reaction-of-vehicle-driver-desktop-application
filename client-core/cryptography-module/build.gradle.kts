plugins {
    kotlin("jvm")
    alias(globalVersions.plugins.kotlin.serialization)
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(project(path = ":client-core::config-module"))
}

tasks.test {
    useJUnitPlatform()
}
