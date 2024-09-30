plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(project.dependencies.platform(globalVersions.hash))
    implementation("dev.whyoleg.cryptography:cryptography-core")
}
