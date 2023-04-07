package de.lobbenmeier.stefan.ytdlp

import io.kotest.core.spec.style.FunSpec

class YtDlpTest :
    FunSpec({
        test("download") {
            val ytDlp = YtDlp(YtDlpConfiguration(), YtDlpVersion())

            ytDlp
                .createDownloadItem("https://www.youtube.com/watch?v=CBB75zjxTR4")
                .download(null, null)
        }
    })
