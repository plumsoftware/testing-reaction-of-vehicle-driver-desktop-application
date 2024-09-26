plugins {
    kotlin("jvm")
    id("app.cash.sqldelight") version ("2.0.2")
    kotlin("plugin.serialization") version ("1.9.24")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

val sqlLightVersion = extra["sqldelight.version"] as String

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
    implementation("app.cash.sqldelight:sqlite-driver:$sqlLightVersion")

    val apachi_poi = "5.2.3"
    val kotlinx_serialization_json = "1.6.0"

//    Excel
    implementation("org.apache.poi:poi:${apachi_poi}")
    implementation("org.apache.poi:poi-ooxml:${apachi_poi}")

//    Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${kotlinx_serialization_json}")
}
