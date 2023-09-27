package org.rhasspy.mobile.logic.connections.webserver

sealed interface WebServerConnectionEvent {
    sealed interface StartSession

    data object WebServerListenForCommand : StartSession, WebServerConnectionEvent

    class WebServerPlayWav(val data: ByteArray) : WebServerConnectionEvent

    data object WebServerStartRecording : StartSession, WebServerConnectionEvent

    data object WebServerStopRecording : WebServerConnectionEvent

    data class WebServerSay(val text: String) : WebServerConnectionEvent

}