plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(project.dependencies.platform("dev.whyoleg.cryptography:cryptography-bom:0.3.1"))
    implementation("dev.whyoleg.cryptography:cryptography-core")
}
