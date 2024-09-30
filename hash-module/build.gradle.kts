plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)

    implementation(project.dependencies.platform(globalVersions.hash))
    implementation("dev.whyoleg.cryptography:cryptography-core")
}

compose.desktop {
    application {
        nativeDistributions {
            windows {
                dependencies {
                    implementation("dev.whyoleg.cryptography:cryptography-provider-jdk")
                }
            }
            macOS {
                dependencies {
                    implementation("dev.whyoleg.cryptography:cryptography-provider-jdk")
                }
            }
            linux {
                dependencies {
                    implementation("dev.whyoleg.cryptography:cryptography-provider-jdk")
                }
            }
        }
    }
}
