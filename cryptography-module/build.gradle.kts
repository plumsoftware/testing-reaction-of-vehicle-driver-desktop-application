plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {

    val version = "3.8.2"

//    Signim
    implementation("at.asitplus.signum:indispensable:$version")
    implementation("at.asitplus.signum:indispensable-josef:$version")
    implementation("at.asitplus.signum:indispensable-cosef:$version")
    implementation("at.asitplus.signum:supreme:0.2.0")

//    JUnit
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
