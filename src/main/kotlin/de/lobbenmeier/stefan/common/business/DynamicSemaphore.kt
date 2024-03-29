package de.lobbenmeier.stefan.common.business

import kotlinx.coroutines.sync.Semaphore

const val maximalSize = 100

class DynamicSemaphore(private var permits: Int) : Semaphore {
    private val backingSemaphore = Semaphore(maximalSize, maximalSize - permits)

    private var extraAcquires = 0

    fun updatePermits(newPermits: Int) {
        val difference = newPermits - permits

        if (difference < 0) {
            val numberOfFailedAcquires =
                List(-difference) { backingSemaphore.tryAcquire() }.count { it.not() }

            synchronized(this) { extraAcquires += numberOfFailedAcquires }
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
        synchronized(this) {
            if (extraAcquires > 0) {
                extraAcquires--
            } else {
                backingSemaphore.release()
            }
        }
    }

    override fun tryAcquire(): Boolean {
        return backingSemaphore.tryAcquire()
    }
}
