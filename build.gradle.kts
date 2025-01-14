import org.gradle.crypto.checksum.Checksum
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "2.1.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    kotlin("plugin.compose") version kotlinVersion
    id("org.jetbrains.compose") version "1.7.3"
    id("com.diffplug.spotless") version "7.0.2"
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
    val ktorVersion = "3.0.3"
    val kotestVersion = "5.9.1"
    val kotlinProcessVersion = "1.5"
    val kotlinxSerializationVersion = "1.8.0"
    val kamelVersion = "1.0.2"

    implementation(compose.desktop.currentOs)

    if (System.getenv("FAT_JAR") == "true") {
        implementation(compose.desktop.macos_x64)
        implementation(compose.desktop.macos_arm64)
        implementation(compose.desktop.windows_x64)
        implementation(compose.desktop.linux_x64)
        implementation(compose.desktop.linux_arm64)
    }

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("com.github.pgreze:kotlin-process:$kotlinProcessVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    implementation("media.kamel:kamel-image-default:$kamelVersion")
    implementation("br.com.devsrsouza.compose.icons.jetbrains:feather:1.0.0")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("dev.dirs:directories:26")
    implementation("io.github.vinceglb:filekit-compose:0.8.8")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

tasks { test { useJUnitPlatform() } }

compose {
    desktop {
        application {
            mainClass = "MainKt"

            nativeDistributions {
                val processorArchitecture = System.getProperty("os.arch")
                packageName =
                    when (processorArchitecture) {
                        "aarch64" -> "yt-dlp-compose-arm64"
                        "x86_64" -> "yt-dlp-compose-x64"
                        else -> "yt-dlp-compose"
                    }
                packageVersion = "1.0.0"

                targetFormats(
                    TargetFormat.Deb,
                    TargetFormat.Dmg,
                    TargetFormat.Exe,
                    TargetFormat.Msi,
                )

                // lwjgl3 needs sun.misc.Unsafe to be included in the bundled JRE
                modules("jdk.unsupported")
            }
        }
    }
}

spotless {
    kotlin { ktfmt().kotlinlangStyle() }
    kotlinGradle { ktfmt().kotlinlangStyle() }
}

tasks {
    withType<JavaCompile> { targetCompatibility = JavaVersion.VERSION_17.toString() }
    withType<KotlinCompile> { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

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

        inputFiles.setFrom(layout.buildDirectory.dir("fatJar"))
        outputDirectory = layout.buildDirectory.dir("checksums")
        checksumAlgorithm = Checksum.Algorithm.SHA256
        appendFileNameToChecksum = true
    }
}
