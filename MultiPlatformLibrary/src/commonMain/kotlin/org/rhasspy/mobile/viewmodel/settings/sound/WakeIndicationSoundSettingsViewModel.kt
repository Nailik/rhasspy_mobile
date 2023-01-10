package org.rhasspy.mobile.viewmodel.settings.sound

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.nativeutils.FileUtils
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.sounds.SoundFile
import org.rhasspy.mobile.settings.sounds.SoundOption
import org.rhasspy.mobile.fileutils.FolderType

class WakeIndicationSoundSettingsViewModel : IIndicationSoundSettingsViewModel() {

    override val isSoundIndicationDefault: StateFlow<Boolean> =
        AppSetting.wakeSound.data.mapReadonlyState {
            it == SoundOption.Default.name
        }

    override val isSoundIndicationDisabled: StateFlow<Boolean> =
        AppSetting.wakeSound.data.mapReadonlyState {
            it == SoundOption.Disabled.name
        }

    override val customSoundFiles: StateFlow<List<SoundFile>> =
        combineState(
            AppSetting.wakeSound.data,
            AppSetting.customWakeSounds.data
        ) { selected, set ->
            set.map { fileName ->
                SoundFile(fileName, selected == fileName, selected != fileName)
            }.toList()
        }

    override val soundVolume: StateFlow<Float> = AppSetting.wakeSoundVolume.data

    override fun onClickSoundIndicationDefault() {
        AppSetting.wakeSound.value = SoundOption.Default.name
    }

    override fun onClickSoundIndicationDisabled() {
        AppSetting.wakeSound.value = SoundOption.Disabled.name
    }

    override fun updateSoundVolume(volume: Float) {
        AppSetting.wakeSoundVolume.value = volume
    }

    override fun selectSoundFile(file: SoundFile) {
        AppSetting.wakeSound.value = file.fileName
    }

    override fun deleteSoundFile(file: SoundFile) {
        if (file.canBeDeleted && !file.selected) {
            val customSounds = AppSetting.customWakeSounds.data
            AppSetting.customWakeSounds.value = customSounds.value.toMutableSet().apply {
                remove(file.fileName)
            }
            FileUtils.removeFile(
                FolderType.SoundFolder.Wake,
                file.fileName
            )
        }
    }

    override fun toggleAudioPlayer() {
        if (isAudioPlaying.value) {
            localAudioService.stop()
        } else {
            localAudioService.playWakeSound()
        }
    }

    override fun chooseSoundFile() {
        viewModelScope.launch {
            FileUtils.selectFile(FolderType.SoundFolder.Wake)
                ?.also { fileName ->
                    val customSounds = AppSetting.customWakeSounds.data
                    AppSetting.customWakeSounds.value =
                        customSounds.value.toMutableSet().apply {
                            add(fileName)
                        }
                    AppSetting.wakeSound.value = fileName
                }
        }
    }

}