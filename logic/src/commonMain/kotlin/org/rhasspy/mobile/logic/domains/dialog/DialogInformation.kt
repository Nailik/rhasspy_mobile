package org.rhasspy.mobile.logic.domains.dialog

import kotlinx.datetime.Clock
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction

sealed class DialogInformation {

    val timeStamp = Clock.System.now()

    data class State(val value: DialogManagerState) : DialogInformation()
    data class Action(val value: DialogServiceMiddlewareAction) : DialogInformation()

}