package org.rhasspy.mobile.middleware.action

sealed interface WebServerAction {

    object ListenForCommand : WebServerAction
    class ListenForWake(val value: Boolean) : WebServerAction
    class SetVolume(val volume: Float) : WebServerAction
    class PlayWav(val byteArray: ByteArray) : WebServerAction
    class Say(val byteArray: ByteArray) : WebServerAction

    object PlayRecording : WebServerAction

    object StartRecording : WebServerAction

    object StopRecording : WebServerAction

}