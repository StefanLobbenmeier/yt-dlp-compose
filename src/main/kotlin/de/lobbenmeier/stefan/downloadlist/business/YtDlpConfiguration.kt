package de.lobbenmeier.stefan.downloadlist.business

import de.lobbenmeier.stefan.settings.business.Settings

fun Settings.toYtDlpConfiguration(): Array<String> =
    buildList {
            fun addArgument(a: String, b: Any?) {
                if (b != null) {
                    add(a)
                    add(b.toString())
                }
            }

            fun addArgument(a: String, b: Boolean) {
                if (b) {
                    add(a)
                }
            }

            addArgument("--proxy", proxy)
            addArgument("--concurrent-fragments", concurrentFragments)
            addArgument("--rate-limit", rateLimit?.kilobytesToBytes())
            addArgument("--add-headers", header)
            addArgument("--cookies-from-browser", cookiesFromBrowser)
            addArgument("--cookies", cookiesFile)

            addArgument("--merge-output-format", mergeOutputFormat)
            addArgument("--remux-video", remuxFormat)
            addArgument("--recode-video", recodeFormat)
            addArgument("--audio-format", audioFormat)

            addArgument("--prefer-free-formats", preferFreeFormats)
            addArgument("--format-sort", formatSort)

            addArgument("--write-subs", writeSubtitles)
            addArgument("--write-auto-subs", writeAutomaticSubtitles)
            addArgument("--sub-format", subtitleFormat)
            addArgument("--sub-langs", subtitleLanguages)

            addArgument("--embed-subs", embedSubtitles)
            addArgument("--embed-metadata", embedMetadata)
            addArgument("--embed-thumbnail", embedThumbnail)
            addArgument("--embed-chapters", embedChapters)

            addArgument("--paths", downloadFolder)
            addArgument("--output", filenameTemplate)
            addArgument("--write-info-json", saveMetadataToJsonFile)
            addArgument("--write-thumbnail", saveThumbnailToFile)
            addArgument("--keep-video", keepUnmergedFiles)
        }
        .toTypedArray()

fun UInt.kilobytesToBytes() = this.times(1024u)
