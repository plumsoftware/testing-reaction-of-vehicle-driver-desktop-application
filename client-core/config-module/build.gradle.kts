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
//    Serialization
    implementation(globalVersions.serialization.json)
}
