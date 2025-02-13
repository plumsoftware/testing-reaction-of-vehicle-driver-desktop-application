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
}

group = "ru.plumsoftware"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)

    implementation(globalVersions.material3)
    implementation(globalVersions.serialization.json)

    api(globalVersions.precompose)
    api(globalVersions.precompose.molecule) // For Molecule intergration
    api(globalVersions.precompose.viewmodel) // For ViewModel intergration

//    Log
    implementation(globalVersions.slf4j.log)
    implementation(globalVersions.apache.slf4j.log)

//    Modules
    implementation(project(path = ":client-core:local-store-module"))

    implementation(project(path = ":ui:screens:authorization-ui-module"))
    implementation(project(path = ":ui:core:theme-ui-module"))
    implementation(project(path = ":ui:screens:add-new-user-ui-module"))
    implementation(project(path = ":ui:screens:all-users-ui-module"))
    implementation(project(path = ":ui:screens:privacy-policy-ui-module"))
    implementation(project(path = ":ui:screens:tests-ui-module"))
    implementation(project(path = ":ui:screens:about-user-ui-module"))
    implementation(project(path = ":ui:screens:picker-ui-module"))
    implementation(project(path = ":ui:screens:test-menu-ui-module"))
    implementation(project(path = ":ui:screens:about-app-ui-module"))
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