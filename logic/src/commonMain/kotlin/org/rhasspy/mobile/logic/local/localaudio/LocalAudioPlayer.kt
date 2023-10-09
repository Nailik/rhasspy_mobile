package org.rhasspy.mobile.logic.local.localaudio

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.StateFlow
import okio.Path
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Notification
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.sounds.SoundOption
import org.rhasspy.mobile.data.sounds.SoundOption.*
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.audioplayer.AudioPlayer
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.extensions.commonInternalFilePath
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.AppSetting
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal interface ILocalAudioPlayer {

    val isPlayingState: StateFlow<Boolean>

    suspend fun playAudio(audioSource: AudioSource, audioOutputOption: AudioOutputOption)

    fun playIndicationSound(
        soundIndicationOutputOption: AudioOutputOption,
        volume: Float,
        option: SoundOption,
        onFinished: (() -> Unit)?,
    )

    fun stop()

}

internal class LocalAudioPlayer(
    private val nativeApplication: NativeApplication,
    private val audioFocusService: IAudioFocus
) : ILocalAudioPlayer {

    private val logger = Logger.withTag("LocalAudioService")

    private val audioPlayer = AudioPlayer()
    override val isPlayingState = audioPlayer.isPlayingState

    override suspend fun playAudio(audioSource: AudioSource, audioOutputOption: AudioOutputOption) = suspendCoroutine { continuation ->
        if (AppSetting.isAudioOutputEnabled.value) {
            logger.d { "playAudio $audioSource" }
            playAudio(
                audioSource = audioSource,
                volume = AppSetting.volume.value,
                audioOutputOption = audioOutputOption,
                onFinished = {
                    logger.d { "onFinished" }
                    continuation.resume(Unit)
                },
            )
        } else {
            continuation.resume(Unit)
        }
    }

    override fun playIndicationSound(
        soundIndicationOutputOption: AudioOutputOption,
        volume: Float,
        option: SoundOption,
        onFinished: (() -> Unit)?,
    ) {
        logger.d { "playWakeSound" }
        when (option) {
            is Disabled ->
                onFinished?.invoke()

            is Default  ->
                playAudio(
                    audioSource = AudioSource.Resource(MR.files.etc_wav_beep_hi),
                    volume = volume,
                    audioOutputOption = soundIndicationOutputOption,
                    onFinished = onFinished
                )

            is Custom   ->
                playAudio(
                    audioSource = AudioSource.File(
                        Path.commonInternalFilePath(
                            nativeApplication = nativeApplication,
                            fileName = option.file,
                        )
                    ),
                    volume = volume,
                    audioOutputOption = soundIndicationOutputOption,
                    onFinished = onFinished
                )
        }
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