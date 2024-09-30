plugins {
    kotlin("jvm")
    alias(globalVersions.plugins.sqdelight)
    alias(globalVersions.plugins.kotlin.serialization)
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("ru.plumsoftware")
            schemaOutputDirectory = file("src/main/kotlin/data/sqldelight/databases")
            verifyMigrations = true
            deriveSchemaFromMigrations = true
        }
    }
}

dependencies {
//    Excel
    implementation(globalVersions.apache.poi)
    implementation(globalVersions.apache.ooxml)

//    Serialization
    implementation(globalVersions.serialization.json)

//    SQDelight
    implementation(globalVersions.sqdelight)

//    Coroutines
    implementation(globalVersions.kotlin.coroutines)

//    Modules
    implementation(project(path = ":hash-module"))
}
