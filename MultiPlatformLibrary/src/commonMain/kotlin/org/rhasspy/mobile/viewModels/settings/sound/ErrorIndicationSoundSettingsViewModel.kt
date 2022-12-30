package org.rhasspy.mobile.viewModels.settings.sound

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.nativeutils.FileUtils
import org.rhasspy.mobile.services.localaudio.LocalAudioService
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.FileType
import org.rhasspy.mobile.settings.sounds.SoundFile
import org.rhasspy.mobile.settings.sounds.SoundOptions

class ErrorIndicationSoundSettingsViewModel : IIndicationSoundSettingsViewModel() {

    override val isSoundIndicationDefault: StateFlow<Boolean> =
        AppSettings.errorSound.data.mapReadonlyState {
            it == SoundOptions.Default.name
        }

    override val isSoundIndicationDisabled: StateFlow<Boolean> =
        AppSettings.errorSound.data.mapReadonlyState {
            it == SoundOptions.Disabled.name
        }

    override val customSoundFiles: StateFlow<List<SoundFile>> =
        combineState(
            AppSettings.errorSound.data,
            AppSettings.customErrorSounds.data
        ) { selected, set ->
            set.map { fileName ->
                SoundFile(fileName, selected == fileName, selected != fileName)
            }.toList()
        }

    override val soundVolume: StateFlow<Float> = AppSettings.errorSoundVolume.data

    override fun onClickSoundIndicationDefault() {
        AppSettings.errorSound.value = SoundOptions.Default.name
    }

    override fun onClickSoundIndicationDisabled() {
        AppSettings.errorSound.value = SoundOptions.Disabled.name
    }

    override fun updateSoundVolume(volume: Float) {
        AppSettings.errorSoundVolume.value = volume
    }

    override fun selectSoundFile(file: SoundFile) {
        AppSettings.errorSound.value = file.fileName
    }

    override fun deleteSoundFile(file: SoundFile) {
        if (file.canBeDeleted && !file.selected) {
            val customSounds = AppSettings.customErrorSounds.data
            AppSettings.customErrorSounds.value = customSounds.value.toMutableSet().apply {
                remove(file.fileName)
            }
            FileUtils.removeFile(FileType.SOUND, subfolder = SoundFileFolder.Error.toString(), file.fileName)
        }
    }

    override fun roggleAudioPlayer() {
        if (isAudioPlaying.value) {
            localAudioService.stop()
        } else {
            localAudioService.playErrorSound()
        }
    }

    override fun chooseSoundFile() {
        viewModelScope.launch {
            FileUtils.selectFile(FileType.SOUND, subfolder = SoundFileFolder.Error.toString())?.also { fileName ->
                val customSounds = AppSettings.customErrorSounds.data
                AppSettings.customErrorSounds.value = customSounds.value.toMutableSet().apply {
                    add(fileName)
                }
                AppSettings.errorSound.value = fileName
            }
        }
    }

}