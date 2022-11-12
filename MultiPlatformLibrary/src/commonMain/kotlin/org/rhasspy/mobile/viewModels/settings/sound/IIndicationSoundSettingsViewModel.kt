package org.rhasspy.mobile.viewModels.settings.sound

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.AudioOutputOptions
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.nativeutils.AudioPlayer
import org.rhasspy.mobile.nativeutils.DeviceVolume
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.sounds.SoundFile

abstract class IIndicationSoundSettingsViewModel : ViewModel() {


    abstract val isSoundIndicationDefault: StateFlow<Boolean>
    abstract val isSoundIndicationDisabled: StateFlow<Boolean>
    abstract val customSoundFiles: StateFlow<List<SoundFile>>
    abstract val soundVolume: StateFlow<Float>

    internal val audioPlayer = AudioPlayer()
    val isAudioPlaying: StateFlow<Boolean> = audioPlayer.isPlayingState
    val audioOutputOption = AppSettings.soundIndicationOutputOption.data
    val isNoSoundInformationBoxVisible = when(AppSettings.soundIndicationOutputOption.value){
        AudioOutputOptions.Sound -> DeviceVolume.volumeFlowSound.mapReadonlyState { it == 0 }
        AudioOutputOptions.Notification ->  DeviceVolume.volumeFlowNotification.mapReadonlyState { it == 0 }
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
    abstract fun clickAudioPlayer()

    //choose sound file from files
    abstract fun chooseSoundFile()

    fun onPause() {
        audioPlayer.stopPlayingData()
    }

}