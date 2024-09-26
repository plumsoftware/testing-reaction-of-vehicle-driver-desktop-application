plugins {
    kotlin("jvm")
    id("app.cash.sqldelight") version ("2.0.2")
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
}
