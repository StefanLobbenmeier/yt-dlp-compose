package de.lobbenmeier.stefan.common.business

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.test.advanceUntilIdle
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testUpdatePermits_concurrentTest() = runTest {
        val sut = DynamicSemaphore(1)

        val taskWaiter = Mutex(locked = true)

        var task1Started = false
        var task2Started = false
        var task3Started = false

        async {
            sut.withPermit {
                task1Started = true
                taskWaiter.lock()
            }
        }
        advanceUntilIdle()

        async {
            sut.withPermit {
                task2Started = true
                taskWaiter.lock()
            }
        }
        advanceUntilIdle()

        async {
            sut.withPermit {
                task3Started = true
                taskWaiter.lock()
            }
        }
        advanceUntilIdle()

        task1Started shouldBe true
        task2Started shouldBe false
        task3Started shouldBe false

        sut.updatePermits(2)

        advanceUntilIdle()

        task2Started shouldBe true
        task3Started shouldBe false

        sut.updatePermits(1)
        advanceUntilIdle()

        task3Started shouldBe false

        // finish task 1
        taskWaiter.unlock()
        advanceUntilIdle()

        task3Started shouldBe false

        // finish task 2
        taskWaiter.unlock()
        advanceUntilIdle()

        task3Started shouldBe true

        // complete test
        taskWaiter.unlock()
        advanceUntilIdle()

        sut.availablePermits shouldBe 1
    }
}
