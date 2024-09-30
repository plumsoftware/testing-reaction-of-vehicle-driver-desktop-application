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
    implementation(project(":local-store-module"))

    implementation(compose.desktop.currentOs)

    implementation(globalVersions.material3)

//    JUnit
    testImplementation(globalVersions.junit)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
