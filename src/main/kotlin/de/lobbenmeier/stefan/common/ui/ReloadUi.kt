package de.lobbenmeier.stefan.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlin.time.Duration
import kotlinx.coroutines.delay

@Composable
fun reloadUiEvery(interval: Duration): State<Long> {
    val currentTime = remember { mutableStateOf(System.currentTimeMillis()) }
    currentTimeEvery(interval) { currentTime.value = it }
    return currentTime
}

@Composable
fun currentTimeEvery(interval: Duration, onIntervalPassed: (Long) -> Unit) {
    LaunchedEffect(Unit) {
        while (true) {
            delay(interval)
            onIntervalPassed(System.currentTimeMillis())
        }
    }
}
