package de.lobbenmeier.stefan.common.business

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore

const val maximalSize = 100

class DynamicSemaphore(private var permits: Int) : Semaphore {
    private val backingSemaphore = Semaphore(maximalSize, maximalSize - permits)

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun updatePermits(newPermits: Int) {
        val difference = newPermits - permits

        if (difference < 0) {
            coroutineScope.launch { repeat(-difference) { backingSemaphore.acquire() } }
        } else {
            repeat(difference) { backingSemaphore.release() }
        }
        permits = newPermits
    }

    override val availablePermits: Int
        get() = backingSemaphore.availablePermits

    override suspend fun acquire() {
        backingSemaphore.acquire()
    }

    override fun release() {
        backingSemaphore.release()
    }

    override fun tryAcquire(): Boolean {
        return backingSemaphore.tryAcquire()
    }
}
