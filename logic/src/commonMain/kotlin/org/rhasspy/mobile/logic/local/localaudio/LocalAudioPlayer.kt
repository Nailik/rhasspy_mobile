package org.rhasspy.mobile.logic.local.localaudio

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.StateFlow
import okio.Path
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Notification
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.sounds.SoundOption
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.audioplayer.AudioPlayer
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.extensions.commonInternalFilePath
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.AppSetting

interface ILocalAudioPlayer {

    val isPlayingState: StateFlow<Boolean>

    fun playAudio(audioSource: AudioSource, audioOutputOption: AudioOutputOption, onFinished: (result: ServiceState) -> Unit)
    fun playWakeSound(onFinished: (() -> Unit)? = null)
    fun playRecordedSound()
    fun playErrorSound()
    fun stop()

}

internal class LocalAudioPlayer(
    private val nativeApplication: NativeApplication,
    private val audioFocusService: IAudioFocus
) : ILocalAudioPlayer {

    private val logger = Logger.withTag("LocalAudioService")

    private val audioPlayer = AudioPlayer()
    override val isPlayingState = audioPlayer.isPlayingState

    override fun playAudio(audioSource: AudioSource, audioOutputOption: AudioOutputOption, onFinished: (result: ServiceState) -> Unit) {
        if (AppSetting.isAudioOutputEnabled.value) {
            logger.d { "playAudio $audioSource" }
            playAudio(
                audioSource = audioSource,
                volume = AppSetting.volume.data,
                audioOutputOption = audioOutputOption,
                onFinished = {
                    logger.d { "onFinished" }
                    onFinished(ServiceState.Success)
                },
            )
        } else {
            onFinished(ServiceState.Success)
        }
    }

    override fun playWakeSound(onFinished: (() -> Unit)?) {
        logger.d { "playWakeSound" }
        when (AppSetting.wakeSound.value) {
            SoundOption.Disabled.name -> {
                onFinished?.invoke()
            }

            SoundOption.Default.name  ->
                playAudio(
                    audioSource = AudioSource.Resource(MR.files.etc_wav_beep_hi),
                    volume = AppSetting.wakeSoundVolume.data,
                    audioOutputOption = AppSetting.soundIndicationOutputOption.value,
                    onFinished = onFinished
                )

            else                      ->
                playAudio(
                    audioSource = AudioSource.File(
                        Path.commonInternalFilePath(
                            nativeApplication = nativeApplication,
                            fileName = "${FolderType.SoundFolder.Wake}/${AppSetting.wakeSound.value}"
                        )
                    ),
                    volume = AppSetting.wakeSoundVolume.data,
                    audioOutputOption = AppSetting.soundIndicationOutputOption.value,
                    onFinished = onFinished
                )
        }
    }

    override fun playRecordedSound() {
        logger.d { "playRecordedSound" }
        when (AppSetting.recordedSound.value) {
            SoundOption.Disabled.name -> Unit
            SoundOption.Default.name  ->
                playAudio(
                    audioSource = AudioSource.Resource(MR.files.etc_wav_beep_lo),
                    volume = AppSetting.recordedSoundVolume.data,
                    audioOutputOption = AppSetting.soundIndicationOutputOption.value
                )

            else                      ->
                playAudio(
                    audioSource = AudioSource.File(
                        Path.commonInternalFilePath(
                            nativeApplication = nativeApplication,
                            fileName = "${FolderType.SoundFolder.Recorded}/${AppSetting.recordedSound.value}"
                        )
                    ),
                    volume = AppSetting.recordedSoundVolume.data,
                    audioOutputOption = AppSetting.soundIndicationOutputOption.value
                )
        }
    }

    override fun playErrorSound() {
        logger.d { "playErrorSound" }
        when (AppSetting.errorSound.value) {
            SoundOption.Disabled.name -> Unit
            SoundOption.Default.name  -> playAudio(
                audioSource = AudioSource.Resource(MR.files.etc_wav_beep_error),
                volume = AppSetting.errorSoundVolume.data,
                audioOutputOption = AppSetting.soundIndicationOutputOption.value
            )

            else                      ->
                playAudio(
                    audioSource = AudioSource.File(
                        Path.commonInternalFilePath(
                            nativeApplication = nativeApplication,
                            fileName = "${FolderType.SoundFolder.Error}/${AppSetting.errorSound.value}"
                        )
                    ),
                    volume = AppSetting.errorSoundVolume.data,
                    audioOutputOption = AppSetting.soundIndicationOutputOption.value
                )
        }
    }

    private fun playAudio(
        audioSource: AudioSource,
        volume: StateFlow<Float>,
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