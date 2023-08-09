package org.rhasspy.mobile.logic.services.localaudio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Notification
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.sounds.SoundOption
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.audiofocus.IAudioFocusService
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.audioplayer.AudioPlayer
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.file.FileUtils
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.AppSetting

interface ILocalAudioService : IService {

    override val serviceState: StateFlow<ServiceState>

    val isPlayingState: StateFlow<Boolean>

    fun playAudio(audioSource: AudioSource, onFinished: (result: ServiceState) -> Unit)
    fun playWakeSoundWithoutParameter()
    fun playWakeSound(onFinished: () -> Unit)
    fun playRecordedSound()
    fun playErrorSound()
    fun stop()

}

internal class LocalAudioService(
    paramsCreator: LocalAudioServiceParamsCreator
) : ILocalAudioService {

    override val logger = LogType.LocalAudioService.logger()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Pending)
    override val serviceState = _serviceState.readOnly

    private val nativeApplication by inject<NativeApplication>()
    private val audioFocusService by inject<IAudioFocusService>()

    private var coroutineScope = CoroutineScope(Dispatchers.IO)

    private val paramsFlow: StateFlow<LocalAudioServiceParams> = paramsCreator()
    private val params: LocalAudioServiceParams get() = paramsFlow.value

    private val audioPlayer = AudioPlayer()
    override val isPlayingState = audioPlayer.isPlayingState

    init {
        coroutineScope.launch {
            paramsFlow.collect {
                audioPlayer.stop()
            }
        }
    }

    override fun playAudio(audioSource: AudioSource, onFinished: (result: ServiceState) -> Unit) {
        if (AppSetting.isAudioOutputEnabled.value) {
            logger.d { "playAudio $audioSource" }
            playAudio(
                audioSource = audioSource,
                volume = AppSetting.volume.data,
                audioOutputOption = params.audioOutputOption,
                onFinished = {
                    logger.d { "onFinished" }
                    onFinished(ServiceState.Success)
                },
            )
        } else {
            onFinished(ServiceState.Success)
        }
    }

    override fun playWakeSoundWithoutParameter() = playWakeSound {}

    override fun playWakeSound(onFinished: () -> Unit) {
        logger.d { "playWakeSound" }
        when (AppSetting.wakeSound.value) {
            SoundOption.Disabled.name -> {
                onFinished()
            }

            SoundOption.Default.name  -> playAudio(
                AudioSource.Resource(MR.files.etc_wav_beep_hi),
                AppSetting.wakeSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value,
                onFinished
            )

            else                      -> playAudio(
                AudioSource.File(FileUtils.getPath("${FolderType.SoundFolder.Wake}/${AppSetting.wakeSound.value}")),
                AppSetting.wakeSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value,
                onFinished
            )
        }
    }

    override fun playRecordedSound() {
        logger.d { "playRecordedSound" }
        when (AppSetting.recordedSound.value) {
            SoundOption.Disabled.name -> Unit
            SoundOption.Default.name  -> playAudio(
                AudioSource.Resource(MR.files.etc_wav_beep_lo),
                AppSetting.recordedSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            )

            else                      -> playAudio(
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

    override fun playErrorSound() {
        logger.d { "playErrorSound" }
        when (AppSetting.errorSound.value) {
            SoundOption.Disabled.name -> Unit
            SoundOption.Default.name  -> playAudio(
                AudioSource.Resource(MR.files.etc_wav_beep_error),
                AppSetting.errorSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            )

            else                      -> playAudio(
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