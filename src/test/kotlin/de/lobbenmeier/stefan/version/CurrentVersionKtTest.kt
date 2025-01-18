package de.lobbenmeier.stefan.version

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

class CurrentVersionKtTest : AnnotationSpec() {
    @Test
    fun testDetectOnPath() {
        currentVersion shouldBe "1.0.0"
    }
}
