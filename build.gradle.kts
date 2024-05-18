import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.9.22"
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
    val material3 = "1.2.1"
    val precompose_version = "1.6.0"
    val kotlinx_serialization_json = "1.6.0"


    implementation(compose.desktop.currentOs)

    implementation("org.apache.poi:poi:${apachi_poi}")
    implementation("org.apache.poi:poi-ooxml:${apachi_poi}")
    implementation("org.jetbrains.compose.material3:material3-desktop:${material3}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${kotlinx_serialization_json}")

    api("moe.tlaster:precompose-viewmodel:${precompose_version}")

    api(compose.foundation)
    api(compose.animation)

    api("moe.tlaster:precompose:$precompose_version")
    api("moe.tlaster:precompose-molecule:$precompose_version") // For Molecule intergration
    api("moe.tlaster:precompose-viewmodel:$precompose_version") // For ViewModel intergration

// api("moe.tlaster:precompose-koin:$precompose_version") // For Koin intergration
}

compose.desktop {
    application {
        mainClass = "presentation.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Тест на реакцию"
            packageVersion = "1.0.0"

            windows {
                packageVersion = "1.0.0"
                msiPackageVersion = "1.0.0"
                exePackageVersion = "1.0.0"
                iconFile.set(project.file("main_icon.ico"))
            }
            macOS {
                packageVersion = "1.0.0"
                dmgPackageVersion = "1.0.0"
                pkgPackageVersion = "1.0.0"
                packageBuildVersion = "1.0.0"
                dmgPackageBuildVersion = "1.0.0"
                pkgPackageBuildVersion = "1.0.0"
                iconFile.set(project.file("main_icon.icns"))
            }
            linux {
                packageVersion = "1.0.0"
                debPackageVersion = "1.0.0"
                rpmPackageVersion = "1.0.0"
                iconFile.set(project.file("main_icon.png"))
            }
        }
    }
}
