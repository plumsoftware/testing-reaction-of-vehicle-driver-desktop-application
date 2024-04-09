import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "ru.plumsoftware"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    val apachi_poi = "5.2.3"
    implementation(compose.desktop.currentOs)

    implementation("org.apache.poi:poi:${apachi_poi}")
    implementation("org.apache.poi:poi-ooxml:${apachi_poi}")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "reaction-test"
            packageVersion = "1.0.0"

            windows {
                packageVersion = "1.0.0"
                msiPackageVersion = "1.0.0"
                exePackageVersion = "1.0.0"
            }
        }
    }
}
