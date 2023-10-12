package org.rhasspy.mobile.logic.local.localaudio

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Notification
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.platformspecific.audioplayer.AudioPlayer
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal interface ILocalAudioPlayer {

    val isPlayingState: StateFlow<Boolean>

    suspend fun playAudio(
        audioSource: AudioSource,
        volume: Float,
        audioOutputOption: AudioOutputOption
    )

    fun stop()

}

internal class LocalAudioPlayer(
    private val audioFocusService: IAudioFocus
) : ILocalAudioPlayer {

    private val logger = Logger.withTag("LocalAudioService")

    private val audioPlayer = AudioPlayer()
    override val isPlayingState = audioPlayer.isPlayingState

    override suspend fun playAudio(
        audioSource: AudioSource,
        volume: Float,
        audioOutputOption: AudioOutputOption
    ) = suspendCoroutine { continuation ->
        logger.d { "playAudio $audioSource" }
        playAudio(
            audioSource = audioSource,
            volume = volume,
            audioOutputOption = audioOutputOption,
            onFinished = {
                logger.d { "onFinished" }
                continuation.resume(Unit)
            },
        )
    }

    private fun playAudio(
        audioSource: AudioSource,
        volume: Float,
        audioOutputOption: AudioOutputOption,
        onFinished: (() -> Unit)? = null
    ) {
        audioFocusService.request(Notification)

        audioPlayer.playAudio(audioSource, volume, audioOutputOption) {
            audioFocusService.abandon(Notification)
            onFinished?.invoke()
        }
    }

    override fun stop() {
        logger.d { "stop" }
        audioPlayer.stop()
    }

}