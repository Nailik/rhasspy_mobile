package org.rhasspy.mobile.middleware.action

sealed interface WebServerAction {

    object ListenForCommand: WebServerAction

    object StartRecording: WebServerAction

    object StopRecording: WebServerAction

}