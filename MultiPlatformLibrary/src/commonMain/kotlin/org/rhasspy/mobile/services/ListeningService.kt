package org.rhasspy.mobile.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

object ListeningService : (Boolean) -> Unit {


    fun start() {
        ForegroundService.listening.addObserver(this)
    }

    fun stop() {
        ForegroundService.listening.removeObserver(this)
    }

    override fun invoke(listening: Boolean) {
        if (listening) {
            test()
        }
    }

    private fun test() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(2.seconds)
            ForegroundService.listening.value = false
        }
    }

}