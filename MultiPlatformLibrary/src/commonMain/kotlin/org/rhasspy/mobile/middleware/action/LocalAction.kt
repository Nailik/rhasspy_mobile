package org.rhasspy.mobile.middleware.action

sealed interface LocalAction {
    class HotWordDetected(hotWord: String) : LocalAction
    object UserSessionClick : LocalAction
}