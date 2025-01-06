package de.lobbenmeier.stefan.downloadlist.business

import de.lobbenmeier.stefan.settings.business.createEmptySettings
import de.lobbenmeier.stefan.updater.model.homeBrewBinaries
import io.kotest.core.spec.style.FunSpec

class YtDlpTest :
    FunSpec({
        test("download") {
            val ytDlp = YtDlp(homeBrewBinaries, createEmptySettings())

            ytDlp.createDownloadItem("https://www.youtube.com/watch?v=CBB75zjxTR4").download()
        }
    })
