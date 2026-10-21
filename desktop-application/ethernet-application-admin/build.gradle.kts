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
    kotlin("plugin.compose")
}

group = "ru.plumsoftware"
version = "1.0-SNAPSHOT"

val appName = "Администратор"
val appVersion = providers.gradleProperty("app.version").get()

// Название и версия для экрана «О программе» (читает getProgramData() из config-module)
val generateProgramInfo by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/program-info")
    val name = appName
    val version = appVersion
    inputs.property("name", name)
    inputs.property("version", version)
    outputs.dir(outputDir)
    doLast {
        val file = outputDir.get().file("program.properties").asFile
        file.parentFile.mkdirs()
        file.writeText("name=$name\nversion=$version\n", Charsets.UTF_8)
    }
}

sourceSets.main {
    resources.srcDir(generateProgramInfo)
}

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
    implementation(project(path = ":client-core:config-module"))

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

            packageName = appName
            packageVersion = appVersion

            description =
                "Это программа предназначена для добавления людей в базу, которые в последующем будут проходить тестиривание в программе «Тест на реакцию» для тестирования сложной сенсомоторной реакции водителя на зрительный раздражитель. Разработчики: студент СибАДИ Дейч Вячеслав Сергеевич, преподаватель кафедры ЦТ Селезнёва Елена Викторовна, преподаватель кафедры АТ Белякова Александра Владимировна."
            copyright = "© 2024 Дейч Вячеслав Сергеевич. All rights reserved."
            vendor = "ФГБОУ ВО «Сибирский государственный автомобильно-дорожный университет»"
            licenseFile.set(project.file("LICENSE.txt"))

            windows {
                packageVersion = appVersion
                msiPackageVersion = appVersion
                exePackageVersion = appVersion
                iconFile.set(project.file("main_icon.ico"))
            }
            macOS {
                packageVersion = appVersion
                dmgPackageVersion = appVersion
                pkgPackageVersion = appVersion
                packageBuildVersion = appVersion
                dmgPackageBuildVersion = appVersion
                pkgPackageBuildVersion = appVersion
                iconFile.set(project.file("main_icon.icns"))

                bundleID = "ru.plumsoftware.adminapp"
                infoPlist {
                    extraKeysRawXml = macExtraPlistKeys
                }
            }
            linux {
                packageVersion = appVersion
                debPackageVersion = appVersion
                rpmPackageVersion = appVersion
                iconFile.set(project.file("main_icon.png"))
            }
        }
    }
}
