package org.rhasspy.mobile.logic.services.localaudio

import kotlinx.coroutines.suspendCancellableCoroutine
import okio.Path
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.sounds.SoundOption
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.audioplayer.AudioPlayer
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.platformspecific.file.FolderType
import kotlin.coroutines.resume

class LocalAudioService : IService(LogType.LocalAudioService) {
    private val params by inject<LocalAudioServiceParams>()

    private val audioPlayer = AudioPlayer()
    val isPlayingState = audioPlayer.isPlayingState

    override fun onClose() {
        logger.d { "onClose" }
        audioPlayer.stop()
        audioPlayer.close()
    }

    suspend fun playAudio(audioSource: AudioSource): ServiceState = suspendCancellableCoroutine { continuation ->
        if (AppSetting.isAudioOutputEnabled.value) {
            logger.d { "playAudio $audioSource" }
            audioPlayer.playAudio(
                audioSource = audioSource,
                volume = AppSetting.volume.data,
                audioOutputOption = params.audioOutputOption,
                onFinished = { exception ->
                    exception?.also {
                        logger.e(exception) { "onError" }
                        if (!continuation.isCompleted) {
                            continuation.resume(ServiceState.Exception(exception))
                        }
                    } ?: run {
                        logger.e { "onFinished" }
                        if (!continuation.isCompleted) {
                            continuation.resume(ServiceState.Success)
                        }
                    }
                },
            )
        } else {
            if (!continuation.isCompleted) {
                continuation.resume(ServiceState.Success)
            }
        }
    }

    fun playWakeSound(onFinished: (exception: Exception?) -> Unit) {
        logger.d { "playWakeSound" }
        when (AppSetting.wakeSound.value) {
            SoundOption.Disabled.name -> {
                onFinished(null)
            }

            SoundOption.Default.name -> audioPlayer.playAudio(
                AudioSource.Resource(MR.files.etc_wav_beep_hi),
                AppSetting.wakeSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value,
                onFinished
            )

            else -> audioPlayer.playAudio(
                AudioSource.File(Path.commonInternalPath(get(), "${FolderType.SoundFolder.Wake}/${AppSetting.wakeSound.value}")),
                AppSetting.wakeSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value,
                onFinished
            )
        }
    }

    fun playRecordedSound() {
        logger.d { "playRecordedSound" }
        when (AppSetting.recordedSound.value) {
            SoundOption.Disabled.name -> {}
            SoundOption.Default.name -> audioPlayer.playAudio(
                AudioSource.Resource(MR.files.etc_wav_beep_lo),
                AppSetting.recordedSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            ) {}

            else -> audioPlayer.playAudio(
                AudioSource.File(Path.commonInternalPath(get(), "${FolderType.SoundFolder.Recorded}/${AppSetting.recordedSound.value}")),
                AppSetting.recordedSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            ) {}
        }
    }

    fun playErrorSound() {
        logger.d { "playErrorSound" }
        when (AppSetting.errorSound.value) {
            SoundOption.Disabled.name -> {}
            SoundOption.Default.name -> audioPlayer.playAudio(
                AudioSource.Resource(MR.files.etc_wav_beep_error),
                AppSetting.errorSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            ) {}

            else -> audioPlayer.playAudio(
                AudioSource.File(Path.commonInternalPath(get(), "${FolderType.SoundFolder.Error}/${AppSetting.errorSound.value}")),
                AppSetting.errorSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            ) {}
        }
    }

    fun stop() {
        logger.d { "stop" }
        audioPlayer.stop()
    }

}