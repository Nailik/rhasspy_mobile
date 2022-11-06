package org.rhasspy.mobile.viewModels.settings.sound

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.nativeutils.AudioPlayer
import org.rhasspy.mobile.nativeutils.SettingsUtils
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.sounds.SoundFile
import org.rhasspy.mobile.settings.sounds.SoundOptions

class WakeIndicationSoundSettingsViewModel : ViewModel(), IIndicationSoundSettingsViewModel {

    override val isSoundIndicationDefault: StateFlow<Boolean> = AppSettings.wakeSound.data.mapReadonlyState {
        it == SoundOptions.Default.name
    }

    override val isSoundIndicationDisabled: StateFlow<Boolean> = AppSettings.wakeSound.data.mapReadonlyState {
        it == SoundOptions.Disabled.name
    }

    override val customSoundFiles: StateFlow<List<SoundFile>> =
        combineState(AppSettings.wakeSound.data, AppSettings.customWakeSounds.data) { selected, set ->
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
            SettingsUtils.removeSoundFile(subfolder = "wake", file.fileName)
        }
    }

    override fun playSoundFile() {
        when (AppSettings.wakeSound.value) {
            SoundOptions.Disabled.name -> {}
            SoundOptions.Default.name -> AudioPlayer.playSoundFileResource(MR.files.etc_wav_beep_hi, AppSettings.wakeSoundVolume.value)
            else -> AudioPlayer.playSoundFile("wake", AppSettings.wakeSound.value, AppSettings.wakeSoundVolume.value)
        }
    }

    override fun chooseSoundFile() {
        SettingsUtils.selectSoundFile(subfolder = "wake") { fileName ->
            fileName?.also {
                val customSounds = AppSettings.customWakeSounds.data
                AppSettings.customWakeSounds.value = customSounds.value.toMutableSet().apply {
                    add(it)
                }
            }
        }
    }

}