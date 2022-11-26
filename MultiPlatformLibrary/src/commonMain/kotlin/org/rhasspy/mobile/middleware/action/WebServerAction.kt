package org.rhasspy.mobile.middleware.action

sealed interface WebServerAction {

    object ListenForCommand : WebServerAction
    class ListenForWake(value: Boolean) : WebServerAction
    class SetVolume(volume: Float) : WebServerAction
    class PlayWav(byteArray: ByteArray) : WebServerAction
    class Say(byteArray: ByteArray) : WebServerAction

    object PlayRecording : WebServerAction

    object StartRecording : WebServerAction

    object StopRecording : WebServerAction

}