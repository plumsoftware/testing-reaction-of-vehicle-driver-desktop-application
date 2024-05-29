import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val macExtraPlistKeys: String
    get() = """
      <key>CFBundleURLTypes</key>
      <array>
        <dict>
          <key>CFBundleURLName</key>
          <string>Example deep link</string>
          <key>CFBundleURLSchemes</key>
          <array>
            <string>compose</string>
          </array>
        </dict>
      </array>
    """


plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.9.24"
    id("app.cash.sqldelight") version "2.0.2"
}

group = "ru.plumsoftware"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

val sqlLightVersion = "2.0.2"

sqldelight {
    databases {
        create("Database") {
            packageName.set("ru.plumsoftware.sessions")
            schemaOutputDirectory = file("src/main/kotlin/data/sqldelight/databases")
            verifyMigrations = true
            deriveSchemaFromMigrations = true
        }
    }
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

//    Log
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("org.apache.logging.log4j:log4j-to-slf4j:2.8.2")

// api("moe.tlaster:precompose-koin:$precompose_version") // For Koin intergration

//    Database
    implementation("app.cash.sqldelight:sqlite-driver:$sqlLightVersion")
}

compose.desktop {
    application {
        mainClass = "presentation.MainKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg, TargetFormat.Pkg, //MacOS
                TargetFormat.Msi, TargetFormat.Exe, //Windows
                TargetFormat.Deb, TargetFormat.Rpm //Linux
            )
            modules("java.sql")

            packageName = "Тест на реакцию"
            packageVersion = "1.0.0"
            description =
                "Это программа для тестирования сложной сенсомоторной реакции водителя на зрительный раздражитель. Разработчики: студент СибАДИ Дейч Вячеслав Сергеевич, преподаватель кафедры ЦТ Селезнёва Елена Викторовна, преподаватель кафедры АТ Белякова Александра Владимировна."
            copyright = "© 2024 Дейч Вячеслав Сергеевич. All rights reserved."
            vendor = "ФГБОУ ВО «Сибирский государственный автомобильно-дорожный университет»"
            licenseFile.set(project.file("LICENSE.txt"))

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

                bundleID = "ru.plumsoftware.reactiontest"
                infoPlist {
                    extraKeysRawXml = macExtraPlistKeys
                }
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