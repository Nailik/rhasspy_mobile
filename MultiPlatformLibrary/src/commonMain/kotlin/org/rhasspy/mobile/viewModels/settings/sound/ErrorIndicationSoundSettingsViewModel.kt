package org.rhasspy.mobile.viewModels.settings.sound

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.nativeutils.SettingsUtils
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.sounds.SoundFile
import org.rhasspy.mobile.settings.sounds.SoundOptions

class ErrorIndicationSoundSettingsViewModel : IIndicationSoundSettingsViewModel() {

    override val isSoundIndicationDefault: StateFlow<Boolean> = AppSettings.errorSound.data.mapReadonlyState {
        it == SoundOptions.Default.name
    }

    override val isSoundIndicationDisabled: StateFlow<Boolean> = AppSettings.errorSound.data.mapReadonlyState {
        it == SoundOptions.Disabled.name
    }

    override val customSoundFiles: StateFlow<List<SoundFile>> =
        combineState(AppSettings.errorSound.data, AppSettings.customErrorSounds.data) { selected, set ->
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
            SettingsUtils.removeSoundFile(subfolder = "error", file.fileName)
        }
    }

    override fun clickAudioPlayer() {
        if (isAudioPlaying.value) {
            audioPlayer.stopPlayingData()
        } else {
            when (AppSettings.errorSound.value) {
                SoundOptions.Disabled.name -> {}
                SoundOptions.Default.name -> audioPlayer.playSoundFileResource(
                    MR.files.etc_wav_beep_error,
                    AppSettings.errorSoundVolume.data,
                    AppSettings.soundIndicationOutputOption.value
                )
                else -> audioPlayer.playSoundFile(
                    "error",
                    AppSettings.errorSound.value,
                    AppSettings.errorSoundVolume.data,
                    AppSettings.soundIndicationOutputOption.value
                )
            }
        }
    }

    override fun chooseSoundFile() {
        SettingsUtils.selectSoundFile(subfolder = "error") { fileName ->
            fileName?.also {
                val customSounds = AppSettings.customErrorSounds.data
                AppSettings.customErrorSounds.value = customSounds.value.toMutableSet().apply {
                    add(it)
                }
                AppSettings.errorSound.value = it
            }
        }
    }

}