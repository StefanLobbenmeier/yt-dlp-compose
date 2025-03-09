package de.lobbenmeier.stefan.downloadlist.business

import de.lobbenmeier.stefan.settings.business.createEmptySettings
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldContainInOrder

class YtDlpConfigurationKtTest : AnnotationSpec() {

    @Test
    fun convertsKilobytesToBytesForRateLimit() {
        val settings = createEmptySettings().copy(rateLimit = 1u)
        settings.toYtDlpConfiguration().shouldContainInOrder("--rate-limit", "1024")
    }
}
