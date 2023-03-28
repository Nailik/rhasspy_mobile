package org.rhasspy.mobile.viewmodel.settings.sound

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.logic.mapReadonlyState
import org.rhasspy.mobile.logic.nativeutils.DeviceVolume
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.data.serviceoption.AudioOutputOption
import org.rhasspy.mobile.data.sounds.SoundFile

abstract class IIndicationSoundSettingsViewModel : ViewModel(), KoinComponent {

    val localAudioService by inject<LocalAudioService>()

    abstract val isSoundIndicationDefault: StateFlow<Boolean>
    abstract val isSoundIndicationDisabled: StateFlow<Boolean>
    abstract val customSoundFiles: StateFlow<List<SoundFile>>
    abstract val soundVolume: StateFlow<Float>

    val isAudioPlaying: StateFlow<Boolean> = localAudioService.isPlayingState
    val audioOutputOption = AppSetting.soundIndicationOutputOption.data
    val isNoSoundInformationBoxVisible = when (AppSetting.soundIndicationOutputOption.value) {
        AudioOutputOption.Sound -> DeviceVolume.volumeFlowSound.mapReadonlyState { it == 0 }
        AudioOutputOption.Notification -> DeviceVolume.volumeFlowNotification.mapReadonlyState { it == 0 }
    }

    abstract fun onClickSoundIndicationDefault()

    abstract fun onClickSoundIndicationDisabled()

    //update sound volume
    abstract fun updateSoundVolume(volume: Float)

    //select sound file
    abstract fun selectSoundFile(file: SoundFile)

    //delete sound file
    abstract fun deleteSoundFile(file: SoundFile)

    //play/stop sound file
    abstract fun toggleAudioPlayer()

    //choose sound file from files
    abstract fun chooseSoundFile()

    fun onPause() {
        localAudioService.stop()
    }

}