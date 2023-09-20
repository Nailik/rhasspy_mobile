package org.rhasspy.mobile.logic.connections.webserver

sealed interface WebServerConnectionEvent {

    data object WebServerListenForCommand : WebServerConnectionEvent

    data object WebServerPlayRecording : WebServerConnectionEvent

    class WebServerPlayWav(val data: ByteArray) : WebServerConnectionEvent

    data object WebServerStartRecording : WebServerConnectionEvent

    data object WebServerStopRecording : WebServerConnectionEvent

    data class WebServerSay(val text: String) : WebServerConnectionEvent

}