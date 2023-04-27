package org.rhasspy.mobile.logic.services.localaudio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import okio.Path
import org.koin.core.component.inject
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Notification
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.sounds.SoundOption
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.audiofocus.AudioFocusService
import org.rhasspy.mobile.platformspecific.audioplayer.AudioPlayer
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.settings.AppSetting
import kotlin.coroutines.resume

class LocalAudioService(
    paramsCreator: LocalAudioServiceParamsCreator
) : IService(LogType.LocalAudioService) {

    private val audioFocusService by inject<AudioFocusService>()
    private var coroutineScope = CoroutineScope(Dispatchers.Default)

    private val paramsFlow: StateFlow<LocalAudioServiceParams> = paramsCreator()
    private val params: LocalAudioServiceParams get() = paramsFlow.value

    private val audioPlayer = AudioPlayer()
    val isPlayingState = audioPlayer.isPlayingState

    init {
        coroutineScope.launch {
            paramsFlow.collect {
                audioPlayer.stop()
            }
        }
    }

    suspend fun playAudio(audioSource: AudioSource): ServiceState = suspendCancellableCoroutine { continuation ->
        if (AppSetting.isAudioOutputEnabled.value) {
            logger.d { "playAudio $audioSource" }
            playAudio(
                audioSource = audioSource,
                volume = AppSetting.volume.data,
                audioOutputOption = params.audioOutputOption,
                onFinished = { exception ->
                    exception?.also {
                        logger.e(exception) { "onError" }
                        if (!continuation.isCompleted) {
                            continuation.resume(ServiceState.Exception(exception))
                        }
                    }  ?: run {
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

    fun playWakeSoundWithoutParameter() = playWakeSound {}

    fun playWakeSound(onFinished: (exception: Exception?) -> Unit) {
        logger.d { "playWakeSound" }
        when (AppSetting.wakeSound.value) {
            SoundOption.Disabled.name -> {
                onFinished(null)
            }

            SoundOption.Default.name -> playAudio(
                AudioSource.Resource(MR.files.etc_wav_beep_hi),
                AppSetting.wakeSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value,
                onFinished
            )

            else -> playAudio(
                AudioSource.File(
                    Path.commonInternalPath(
                        nativeApplication = nativeApplication,
                        fileName = "${FolderType.SoundFolder.Wake}/${AppSetting.wakeSound.value}"
                    )
                ),
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
            SoundOption.Default.name -> playAudio(
                AudioSource.Resource(MR.files.etc_wav_beep_lo),
                AppSetting.recordedSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            )

            else -> playAudio(
                AudioSource.File(
                    Path.commonInternalPath(
                        nativeApplication = nativeApplication,
                        fileName = "${FolderType.SoundFolder.Recorded}/${AppSetting.recordedSound.value}"
                    )
                ),
                AppSetting.recordedSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            )
        }
    }

    fun playErrorSound() {
        logger.d { "playErrorSound" }
        when (AppSetting.errorSound.value) {
            SoundOption.Disabled.name -> {}
            SoundOption.Default.name -> playAudio(
                AudioSource.Resource(MR.files.etc_wav_beep_error),
                AppSetting.errorSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            )

            else -> playAudio(
                AudioSource.File(
                    Path.commonInternalPath(
                        nativeApplication = nativeApplication,
                        fileName = "${FolderType.SoundFolder.Error}/${AppSetting.errorSound.value}"
                    )
                ),
                AppSetting.errorSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            )
        }
    }

    private fun playAudio(
        audioSource: AudioSource,
        volume: StateFlow<Float>,
        audioOutputOption: AudioOutputOption,
        onFinished: ((exception: Exception?) -> Unit)? = null
    ) {
        audioFocusService.request(Notification)

        audioPlayer.playAudio(audioSource, volume, audioOutputOption) {
            audioFocusService.abandon(Notification)
            onFinished?.invoke(it)
        }

    }

    fun stop() {
        logger.d { "stop" }
        audioPlayer.stop()
    }

}