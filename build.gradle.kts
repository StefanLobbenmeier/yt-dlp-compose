import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.8.21"
    id("com.diffplug.spotless") version "6.19.0"
}

group = "com.example"

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

kotlin { jvmToolchain(17) }

dependencies {
    val ktorVersion = "2.3.1"
    val kotestVersion = "5.6.2"
    val kotlinProcessVersion = "1.4.1"
    val kotlinxSerializationVersion = "1.5.1"
    val kamelVersion = "0.4.1"

    implementation(compose.desktop.currentOs)

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("com.github.pgreze:kotlin-process:$kotlinProcessVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    implementation("com.alialbaali.kamel:kamel-image:$kamelVersion")
    implementation("br.com.devsrsouza.compose.icons.jetbrains:feather:1.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

tasks { test { useJUnitPlatform() } }

compose {
    kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:1.4.7")
    desktop {
        application {
            mainClass = "MainKt"
            nativeDistributions {
                targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
                packageName = "yt-dlp-compose"
                packageVersion = "1.0.0"
            }
        }
    }
}

spotless {
    kotlin { ktfmt().dropboxStyle() }
    kotlinGradle { ktfmt().dropboxStyle() }
}
