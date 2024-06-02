import org.gradle.crypto.checksum.Checksum
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    val kotlinVersion = "2.0.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    kotlin("plugin.compose") version kotlinVersion
    id("org.jetbrains.compose") version "1.6.10"
    id("com.diffplug.spotless") version "6.25.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.gradle.crypto.checksum") version "1.4.0"
}

group = "com.example"

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

kotlin { jvmToolchain(21) }

dependencies {
    val ktorVersion = "2.3.11"
    val kotestVersion = "5.9.0"
    val kotlinProcessVersion = "1.4.1"
    val kotlinxSerializationVersion = "1.6.3"
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
    implementation("io.github.oshai:kotlin-logging-jvm:6.0.9")
    implementation("org.slf4j:slf4j-simple:2.0.13")
    implementation("dev.dirs:directories:26")
    implementation("com.darkrockstudios:mpfilepicker:3.1.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

tasks { test { useJUnitPlatform() } }

compose {
    desktop {
        application {
            mainClass = "MainKt"

            nativeDistributions {
                packageName = "yt-dlp-compose"
                packageVersion = "1.0.0"

                targetFormats(
                    TargetFormat.Deb,
                    TargetFormat.Dmg,
                    TargetFormat.Exe,
                    TargetFormat.Msi,
                )
            }
        }
    }
}

spotless {
    kotlin { ktfmt().kotlinlangStyle() }
    kotlinGradle { ktfmt().kotlinlangStyle() }
}

tasks {
    register("nativeDistribution") {
        dependsOn("packageDistributionForCurrentOS", "createChecksumsForNativeDistributions")
    }
    register<Checksum>("createChecksumsForNativeDistributions") {
        dependsOn("packageDistributionForCurrentOS")

        inputFiles.setFrom(
            layout.buildDirectory.dir("compose/binaries/main/deb"),
            layout.buildDirectory.dir("compose/binaries/main/dmg"),
            layout.buildDirectory.dir("compose/binaries/main/exe"),
            layout.buildDirectory.dir("compose/binaries/main/msi"),
        )
        outputDirectory = layout.buildDirectory.dir("checksums")
        checksumAlgorithm = Checksum.Algorithm.SHA256
        appendFileNameToChecksum = true
    }
    jar { manifest { attributes("Main-Class" to "MainKt") } }
    shadowJar {
        destinationDirectory = layout.buildDirectory.dir("fatJar")
        archiveFileName = "yt-dlp-compose.jar"
        finalizedBy("createChecksumsForFatJar")
    }
    register<Checksum>("createChecksumsForFatJar") {
        dependsOn("shadowJar")

        inputFiles.setFrom(
            layout.buildDirectory.dir("fatJar"),
        )
        outputDirectory = layout.buildDirectory.dir("checksums")
        checksumAlgorithm = Checksum.Algorithm.SHA256
        appendFileNameToChecksum = true
    }
}
