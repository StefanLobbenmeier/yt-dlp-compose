package de.lobbenmeier.stefan.updater.business

import java.io.File
import java.nio.file.Path

fun detectOnPath(binaryName: String, pathVariable: String = System.getenv("PATH")): List<File> {
    val systemPaths = pathVariable.split(platform.pathDelimiter).map(Path::of)

    return (systemPaths + platform.extraPaths)
        .map { it.resolve(binaryName).toFile() }
        .filter(File::isFile)
}
