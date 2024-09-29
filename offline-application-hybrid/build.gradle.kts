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
}

group = "ru.plumsoftware"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    val material3 = "1.2.1"
    val precompose_version = "1.6.0"
    val kotlinx_serialization_json = "1.6.0"
    val aay_chart_version = "Beta-0.0.5"

    implementation(compose.desktop.currentOs)

    implementation("org.jetbrains.compose.material3:material3-desktop:${material3}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${kotlinx_serialization_json}")

    api("moe.tlaster:precompose:$precompose_version")
    api("moe.tlaster:precompose-molecule:$precompose_version") // For Molecule intergration
    api("moe.tlaster:precompose-viewmodel:$precompose_version") // For ViewModel intergration

//    Log
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("org.apache.logging.log4j:log4j-to-slf4j:2.8.2")

// api("moe.tlaster:precompose-koin:$precompose_version") // For Koin intergration

//    Charts
    implementation("io.github.thechance101:chart:$aay_chart_version")

//    Modules
    implementation(project(path = ":authorization-ui-module"))
    implementation(project(path = ":theme-ui-module"))
    implementation(project(path = ":local-store-module"))
    implementation(project(path = ":add-new-user-ui-module"))
    implementation(project(path = ":all-users-ui-module"))
    implementation(project(path = ":privacy-policy-ui-module"))
    implementation(project(path = ":tests-ui-module"))
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

            packageName = "Тестирование реакции"
            packageVersion = "1.0.0"

            description =
                "Это программа предназначена для добавления людей в базу, которые в последующем будут проходить тестиривание в программе «Тест на реакцию» для тестирования сложной сенсомоторной реакции водителя на зрительный раздражитель. Разработчики: студент СибАДИ Дейч Вячеслав Сергеевич, преподаватель кафедры ЦТ Селезнёва Елена Викторовна, преподаватель кафедры АТ Белякова Александра Владимировна."
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

                bundleID = "ru.plumsoftware.adminapp"
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
