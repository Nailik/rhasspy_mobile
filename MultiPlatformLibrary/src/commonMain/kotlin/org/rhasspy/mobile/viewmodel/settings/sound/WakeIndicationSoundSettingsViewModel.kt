package org.rhasspy.mobile.viewmodel.settings.sound

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.nativeutils.FileUtils
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.FileType
import org.rhasspy.mobile.settings.sounds.SoundFile
import org.rhasspy.mobile.settings.sounds.SoundOptions


class WakeIndicationSoundSettingsViewModel : IIndicationSoundSettingsViewModel() {

    override val isSoundIndicationDefault: StateFlow<Boolean> =
        AppSettings.wakeSound.data.mapReadonlyState {
            it == SoundOptions.Default.name
        }

    override val isSoundIndicationDisabled: StateFlow<Boolean> =
        AppSettings.wakeSound.data.mapReadonlyState {
            it == SoundOptions.Disabled.name
        }

    override val customSoundFiles: StateFlow<List<SoundFile>> =
        combineState(
            AppSettings.wakeSound.data,
            AppSettings.customWakeSounds.data
        ) { selected, set ->
            set.map { fileName ->
                SoundFile(fileName, selected == fileName, selected != fileName)
            }.toList()
        }

    override val soundVolume: StateFlow<Float> = AppSettings.wakeSoundVolume.data

    override fun onClickSoundIndicationDefault() {
        AppSettings.wakeSound.value = SoundOptions.Default.name
    }

    override fun onClickSoundIndicationDisabled() {
        AppSettings.wakeSound.value = SoundOptions.Disabled.name
    }

    override fun updateSoundVolume(volume: Float) {
        AppSettings.wakeSoundVolume.value = volume
    }

    override fun selectSoundFile(file: SoundFile) {
        AppSettings.wakeSound.value = file.fileName
    }

    override fun deleteSoundFile(file: SoundFile) {
        if (file.canBeDeleted && !file.selected) {
            val customSounds = AppSettings.customWakeSounds.data
            AppSettings.customWakeSounds.value = customSounds.value.toMutableSet().apply {
                remove(file.fileName)
            }
            FileUtils.removeFile(FileType.SOUND, subfolder = SoundFileFolder.Wake.toString(), file.fileName)
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
            FileUtils.selectFile(FileType.SOUND, subfolder = SoundFileFolder.Error.folderName)?.also { fileName ->
                val customSounds = AppSettings.customWakeSounds.data
                AppSettings.customWakeSounds.value =
                    customSounds.value.toMutableSet().apply {
                        add(fileName)
                    }
                AppSettings.wakeSound.value = fileName
            }
        }
    }

}