package de.lobbenmeier.stefan.ytdlp

import io.kotest.core.spec.style.FunSpec

class YtDlpTest : FunSpec({

    test("download") {
        val ytDlp = YtDlp(YtDlpConfiguration(), YtDlpVersion())

        ytDlp.download(DownloadItem())
    }
})
