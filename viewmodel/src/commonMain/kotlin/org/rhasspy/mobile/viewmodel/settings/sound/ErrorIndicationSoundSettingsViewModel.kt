package org.rhasspy.mobile.viewmodel.settings.sound

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.Path
import org.koin.core.component.get
import org.rhasspy.mobile.data.sounds.SoundFile
import org.rhasspy.mobile.data.sounds.SoundOption
import org.rhasspy.mobile.logic.combineState
import org.rhasspy.mobile.logic.fileutils.FolderType
import org.rhasspy.mobile.logic.mapReadonlyState
import org.rhasspy.mobile.logic.nativeutils.FileUtils
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath

class ErrorIndicationSoundSettingsViewModel : IIndicationSoundSettingsViewModel() {

    override val isSoundIndicationDefault: StateFlow<Boolean> =
        AppSetting.errorSound.data.mapReadonlyState {
            it == SoundOption.Default.name
        }

    override val isSoundIndicationDisabled: StateFlow<Boolean> =
        AppSetting.errorSound.data.mapReadonlyState {
            it == SoundOption.Disabled.name
        }

    override val customSoundFiles: StateFlow<List<SoundFile>> =
        combineState(
            AppSetting.errorSound.data,
            AppSetting.customErrorSounds.data
        ) { selected, set ->
            set.map { fileName ->
                SoundFile(fileName, selected == fileName, selected != fileName)
            }.toList()
        }

    override val soundVolume: StateFlow<Float> = AppSetting.errorSoundVolume.data

    override fun onClickSoundIndicationDefault() {
        AppSetting.errorSound.value = SoundOption.Default.name
    }

    override fun onClickSoundIndicationDisabled() {
        AppSetting.errorSound.value = SoundOption.Disabled.name
    }

    override fun updateSoundVolume(volume: Float) {
        AppSetting.errorSoundVolume.value = volume
    }

    override fun selectSoundFile(file: SoundFile) {
        AppSetting.errorSound.value = file.fileName
    }

    override fun deleteSoundFile(file: SoundFile) {
        if (file.canBeDeleted && !file.selected) {
            val customSounds = AppSetting.customErrorSounds.data
            AppSetting.customErrorSounds.value = customSounds.value.toMutableSet().apply {
                remove(file.fileName)
            }
            Path.commonInternalPath(get(),"${FolderType.SoundFolder.Error}/${file.fileName}").commonDelete()
        }
    }

    override fun toggleAudioPlayer() {
        if (isAudioPlaying.value) {
            localAudioService.stop()
        } else {
            localAudioService.playErrorSound()
        }
    }

    override fun chooseSoundFile() {
        viewModelScope.launch {
            FileUtils.selectFile(FolderType.SoundFolder.Error)
                ?.also { fileName ->
                    val customSounds = AppSetting.customErrorSounds.data
                    AppSetting.customErrorSounds.value = customSounds.value.toMutableSet().apply {
                        add(fileName.name)
                    }
                    AppSetting.errorSound.value = fileName.name
                }
        }
    }

}