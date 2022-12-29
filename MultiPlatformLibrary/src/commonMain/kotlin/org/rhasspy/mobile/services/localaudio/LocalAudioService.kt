package org.rhasspy.mobile.services.localaudio

import org.rhasspy.mobile.nativeutils.AudioPlayer
import org.rhasspy.mobile.services.IService
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocalAudioService : IService() {

    private val audioPlayer = AudioPlayer()
    override fun onClose() {
        audioPlayer.stopPlayingData()
    }

    suspend fun playAudio(data: List<Byte>): Unit = suspendCoroutine { continuation ->
        audioPlayer.playData(
            data = data,
            onFinished = {
                continuation.resume(Unit)
            },
            onError = {
                continuation.resume(Unit)
            }
        )
    }

}