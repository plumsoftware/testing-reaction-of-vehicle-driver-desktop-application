plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
//    Coroutines
    implementation(globalVersions.kotlin.coroutines)

//    JUnit
    testImplementation(globalVersions.junit)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
