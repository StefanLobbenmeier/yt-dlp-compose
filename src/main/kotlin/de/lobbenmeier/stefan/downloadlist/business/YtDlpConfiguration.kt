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
            addArgument("--rate-limit", rateLimit)

            addArgument("--merge-output-format", mergeOutputFormat)
            addArgument("--remux-video", remuxFormat)
            addArgument("--recode-video", recodeFormat)
            addArgument("--audio-format", audioFormat)

            addArgument("--embed-subs", embedSubtitles)
            addArgument("--embed-metadata", embedMetadata)
            addArgument("--embed-thumbnail", embedThumbnail)
            addArgument("--embed-chapters", embedChapters)

            addArgument("--output", filenameTemplate)
            addArgument("--save", saveMetadataToJsonFile)
            addArgument("--write-thumbnail", saveThumbnailToFile)
            addArgument("--keep-video", keepUnmergedFiles)
        }
        .toTypedArray()
