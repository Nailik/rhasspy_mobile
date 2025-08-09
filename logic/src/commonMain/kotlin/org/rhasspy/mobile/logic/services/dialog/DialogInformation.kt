package org.rhasspy.mobile.logic.services.dialog

import kotlin.time.Clock
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import kotlin.time.ExperimentalTime

sealed class DialogInformation {

    @OptIn(ExperimentalTime::class)
    val timeStamp = Clock.System.now()

    data class State(val value: DialogManagerState) : DialogInformation()
    data class Action(val value: DialogServiceMiddlewareAction) : DialogInformation()

}