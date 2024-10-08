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
    implementation(project(path = ":config-module"))
}

tasks.test {
    useJUnitPlatform()
}
