package org.rhasspy.mobile.platformspecific.audiorecorder

interface IInternalAudioManagerCallback {

    fun register(audioSessionId: Int?)

    fun unregister()

}