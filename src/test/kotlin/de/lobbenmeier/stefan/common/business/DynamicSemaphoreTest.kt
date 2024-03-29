package de.lobbenmeier.stefan.common.business

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest

class DynamicSemaphoreTest : AnnotationSpec() {

    @Test
    fun testUpdatePermits_easyCase() = runTest {
        val sut = DynamicSemaphore(1)

        sut.tryAcquire() shouldBe true
        sut.tryAcquire() shouldBe false

        sut.updatePermits(2)

        sut.tryAcquire() shouldBe true
        sut.tryAcquire() shouldBe false

        sut.release()
        sut.release()

        sut.tryAcquire() shouldBe true
        sut.tryAcquire() shouldBe true
        sut.tryAcquire() shouldBe false
    }

    @Test
    fun testUpdatePermits_negativeCase() = runTest {
        val sut = DynamicSemaphore(2)

        sut.tryAcquire() shouldBe true
        sut.tryAcquire() shouldBe true
        sut.tryAcquire() shouldBe false

        sut.updatePermits(1)

        // -1
        sut.tryAcquire() shouldBe false

        // 0
        sut.release()
        sut.tryAcquire() shouldBe false

        // 1
        sut.release()
        sut.tryAcquire() shouldBe true
    }
}
