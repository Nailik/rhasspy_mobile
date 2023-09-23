package org.rhasspy.mobile.logic.domains.wake

import kotlinx.datetime.Instant

sealed interface WakeEvent {

    data class Detection(
        val name: String,
        val timeStamp: Instant,
    ) : WakeEvent

    data object NotDetected : WakeEvent

}