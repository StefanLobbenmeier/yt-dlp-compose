import org.gradle.crypto.checksum.Checksum
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "2.2.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    kotlin("plugin.compose") version kotlinVersion
    id("org.jetbrains.compose") version "1.8.2"
    id("com.diffplug.spotless") version "7.2.1"
    id("com.gradleup.shadow") version "9.1.0"
    id("org.gradle.crypto.checksum") version "1.4.0"
}

group = "de.lobbenmeier.stefan"

val appVersion = System.getenv("VERSION") ?: "1.0.0"

version = appVersion

val versionDirectory = layout.buildDirectory.dir("version")

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

kotlin { jvmToolchain(21) }

sourceSets { main { output.dir(versionDirectory) } }

dependencies {
    val ktorVersion = "3.2.3"
    val kotestVersion = "6.0.1"
    val kotlinProcessVersion = "1.5.1"
    val kotlinxSerializationVersion = "1.9.0"
    val kamelVersion = "1.0.8"

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
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.13")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("dev.dirs:directories:26")
    implementation("io.github.vinceglb:filekit-compose:0.8.8")
    implementation("com.github.tkuenneth:nativeparameterstoreaccess:0.1.3")

    testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

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
                packageVersion = appVersion

                targetFormats(
                    TargetFormat.Deb,
                    TargetFormat.Dmg,
                    TargetFormat.Rpm,
                    TargetFormat.Exe,
                )

                linux { shortcut = true }

                windows {
                    dirChooser = true
                    menu = true
                    perUserInstall = true
                    shortcut = true
                    upgradeUuid = "760c3be8-21cf-43fe-ba50-241d1cc25ae8"
                }

                modules(
                    // lwjgl3 needs sun.misc.Unsafe to be included in the bundled JRE
                    "jdk.unsupported",

                    // required by NVDA on windows
                    "jdk.accessibility",

                    // others suggested by gradle suggestedRuntimeModules
                    "java.instrument",
                    "java.management",
                    "jdk.security.auth",
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
    withType<JavaCompile> { targetCompatibility = JavaVersion.VERSION_17.toString() }
    withType<KotlinCompile> { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

    val createVersionFile =
        register("createVersionFile") {
            doLast {
                val file = versionDirectory.get().file("version.txt")
                file.asFile.ensureParentDirsCreated()
                file.asFile.writeText(appVersion)
            }
        }
    processResources { dependsOn(createVersionFile) }

    register("nativeDistribution") {
        dependsOn("packageDistributionForCurrentOS", "createChecksumsForNativeDistributions")
    }
    register<Checksum>("createChecksumsForNativeDistributions") {
        dependsOn("packageDistributionForCurrentOS")

        inputFiles.setFrom(
            layout.buildDirectory.dir("compose/binaries/main/deb"),
            layout.buildDirectory.dir("compose/binaries/main/rpm"),
            layout.buildDirectory.dir("compose/binaries/main/dmg"),
            layout.buildDirectory.dir("compose/binaries/main/exe"),
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
