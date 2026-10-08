# Open Video Downloader

A desktop video and playlist downloader powered by yt-dlp, ffmpeg, Compose, and [Nucleus](https://nucleusframework.dev/en/).

The application uses Nucleus's Tao window backend, platform window controls and context menus, native file and directory pickers, and reactive system appearance and accent colors. Settings and download information open in resizable desktop dialogs; Escape closes them. Settings, download lists, playlists, and logs include draggable Compose desktop scrollbars. These are rendered by Compose rather than OS scrollbar widgets. Download behavior and existing settings are preserved.

## Development

Install JDK 25, then run:

```sh
./gradlew run
./gradlew build
```

In IntelliJ, reload the Gradle project and launch the Gradle `run` task. Nucleus owns the desktop launch and packaging tasks; only Kotlin's Compose compiler plugin is needed alongside it.

On a graphical desktop, `./gradlew desktopDialogSmoke` checks that a single request opens a Tao dialog from inside a layout subcomposition, and that its light/dark text colors are independent of the opening button.

## GraalVM distribution

```sh
VERSION=3.0.57 ./gradlew nativeDistribution
```

Nucleus downloads the configured GraalVM Community 25 toolchain automatically. `GRAALVM_HOME` can point to a compatible local toolchain. Intel macOS uses Nucleus's Liberica NIK fallback when GraalVM CE is unavailable for that architecture.

Installer packaging also requires Node.js 18 or later (CI uses Node.js 22). Native compilation requires Xcode Command Line Tools on macOS, MSVC Build Tools on Windows, or GCC and the GTK/X11 development libraries plus `patchelf` on Linux. On headless Linux, run the command through `xvfb-run -a`.

`nativeDistribution` builds GraalVM installers for the current OS: DMG on macOS, EXE on Windows, and DEB/RPM on Linux. Installers are written to `build/compose/binaries/main/graalvm-<format>/`, with SHA-256 checksums in `build/checksums/`. These builds ship a native executable rather than a bundled JVM. Build on each target operating system; the release workflow includes both macOS architectures, Windows, and Linux.

For the native development loop, use `./gradlew runGraalvmNative`. To build an unpackaged native application folder, use `./gradlew createGraalvmNativeDistributable`.

The optional JVM fat JAR remains available with `FAT_JAR=true ./gradlew shadowJar`.
