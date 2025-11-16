package de.lobbenmeier.stefan.updater.business

import dev.dirs.ProjectDirectories
import java.nio.file.Path

val Directories = ProjectDirectories.fromPath("yt-dlp-compose")
val platform = detectPlatform()

data class Platform(
    val platformType: PlatformType,
    val name: String,
    val ytDlpName: YtDlpNames,
    val ffmpegPlatform: FfmpegPlatforms,
    val denoName: DenoName,
    val needsExecutableBit: Boolean,
    val pathDelimiter: String,
    val extraPaths: List<Path> = emptyList(),
) {
    val settingsFile = Path.of(Directories.configDir).resolve("settings.json")
    val binariesFolder = Path.of(Directories.dataDir).resolve("binaries")
    val homeFolder = Path.of(System.getProperty("user.home"))
    val downloadsFolder = homeFolder.resolve("Downloads")

    val ytDlpBinary = binariesFolder.resolve(ytDlpName.filename).toFile()
    val ffmpegBinary = binariesFolder.resolve("ffmpeg").toFile()
    val ffprobeBinary = binariesFolder.resolve("ffprobe").toFile()
}

enum class PlatformType {
    WINDOWS,
    MAC_OS,
    LINUX,
}

enum class YtDlpNames(val filename: String) {
    python("yt-dlp"),
    osxLegacy("yt-dlp_macos_legacy"),
    osx("yt-dlp_macos"),
    windows("yt-dlp.exe"),
}

enum class DenoName(val filename: String, val zipFileName: String) {
    macos_arm("deno", "deno-aarch64-apple-darwin.zip"),
    linux_arm("deno", "deno-aarch64-unknown-linux-gnu.zip"),
    macos_x86("deno", "deno-x86_64-apple-darwin.zip"),
    linux_x86("deno", "deno-x86_64-unknown-linux-gnu.zip"),
    windows("deno.exe", "deno-x86_64-pc-windows-msvc.zip"),
}

enum class FfmpegPlatforms(val platformName: String) {
    linux32("linux-32"),
    linux64("linux-64"),
    linuxArm64("linux-arm64"),
    linuxArmel("linux-armel"),
    linuxArmhf("linux-armhf"),
    osx64("osx-64"),
    windows64("windows-64"),
}

private fun detectPlatform(): Platform {
    val name = System.getProperty("os.name")
    val arch = System.getProperty("os.arch")
    val version = System.getProperty("os.version")

    val displayName = "Name: $name, Version: $version, Architecture: $arch"

    return when {
        name.contains("Windows") -> {
            Platform(
                PlatformType.WINDOWS,
                displayName,
                YtDlpNames.windows,
                FfmpegPlatforms.windows64,
                DenoName.windows,
                needsExecutableBit = false,
                pathDelimiter = ";",
            )
        }
        name.contains("Mac") -> {
            Platform(
                PlatformType.MAC_OS,
                displayName,
                if (version < "10.15") YtDlpNames.osxLegacy else YtDlpNames.osx,
                FfmpegPlatforms.osx64,
                if (arch == "aarch64") DenoName.macos_arm else DenoName.macos_x86,
                needsExecutableBit = true,
                pathDelimiter = ":",
                extraPaths = listOf("/opt/homebrew/bin/").map(Path::of),
            )
        }
        else -> {
            val ffmpegPlatform =
                when {
                    arch.contains("32") -> FfmpegPlatforms.linux32
                    arch.contains("arm") -> FfmpegPlatforms.linuxArm64
                    else -> FfmpegPlatforms.linux64
                }

            val denoName =
                when {
                    arch.contains("arm") -> DenoName.linux_arm
                    else -> DenoName.linux_x86
                }

            Platform(
                PlatformType.LINUX,
                displayName,
                YtDlpNames.python,
                ffmpegPlatform,
                denoName,
                needsExecutableBit = true,
                pathDelimiter = ":",
            )
        }
    }
}
