package de.lobbenmeier.stefan.platform

data class Platform(
    val name: String,
    val ytDlpName: YtDlpNames,
    val ffmpegPlatform: FfmpegPlatforms,
)

enum class YtDlpNames(val filename: String) {
    python("yt-dlp"),
    osxLegacy("yt-dlp_macos_legacy"),
    osx("yt-dlp_macos"),
    windows("yt-dlp")
}

enum class FfmpegPlatforms(val filename: String) {
    linux32("linux-32"),
    linux64("linux-64"),
    linuxArm64("linux-arm64"),
    linuxArmel("linux-armel"),
    linuxArmhf("linux-armhf"),
    osx64("osx-64"),
    windows64("windows-64")
}

fun getPlatform(): Platform {
    val name = System.getProperty("os.name")
    val arch = System.getProperty("os.arch")
    val version = System.getProperty("os.version")

    val displayName = "Name: $name, Version: $version, Architecture: $arch"

    return when {
        name.contains("Windows") -> {
            Platform(displayName, YtDlpNames.windows, FfmpegPlatforms.windows64)
        }
        name.contains("Mac") -> {
            val ytDlpName =
                if (version < "10.15") {
                    YtDlpNames.osxLegacy
                } else {
                    YtDlpNames.osx
                }
            Platform(displayName, ytDlpName, FfmpegPlatforms.osx64)
        }
        else -> {
            val ffmpegPlatform =
                when {
                    arch.contains("32") -> FfmpegPlatforms.linux32
                    arch.contains("arm") -> FfmpegPlatforms.linuxArm64
                    else -> FfmpegPlatforms.linux64
                }

            Platform(displayName, YtDlpNames.python, ffmpegPlatform)
        }
    }
}
