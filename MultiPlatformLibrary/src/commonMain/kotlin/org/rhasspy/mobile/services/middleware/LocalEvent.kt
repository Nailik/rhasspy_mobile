package org.rhasspy.mobile.services.middleware

sealed class LocalEvent {
    class HotWordDetected(hotWord: String): LocalEvent()
    object UserSessionClick: LocalEvent()
}