package org.rhasspy.mobile.viewmodel.settings.indication.sound

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.Path
import org.koin.core.component.get
import org.rhasspy.mobile.data.sounds.SoundFile
import org.rhasspy.mobile.data.sounds.SoundOption
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.platformspecific.file.FileUtils
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.mapReadonlyState

class RecordedIndicationSoundSettingsViewModel : IIndicationSoundSettingsViewModel() {

    override val isSoundIndicationDefault: StateFlow<Boolean> =
        AppSetting.recordedSound.data.mapReadonlyState {
            it == SoundOption.Default.name
        }

    override val isSoundIndicationDisabled: StateFlow<Boolean> =
        AppSetting.recordedSound.data.mapReadonlyState {
            it == SoundOption.Disabled.name
        }

    override val customSoundFiles: StateFlow<List<SoundFile>> =
        combineState(
            AppSetting.recordedSound.data,
            AppSetting.customRecordedSounds.data
        ) { selected, set ->
            set.map { fileName ->
                SoundFile(fileName, selected == fileName, selected != fileName)
            }.toList()
        }

    override val soundVolume: StateFlow<Float> = AppSetting.recordedSoundVolume.data

    override fun onClickSoundIndicationDefault() {
        AppSetting.recordedSound.value = SoundOption.Default.name
    }

    override fun onClickSoundIndicationDisabled() {
        AppSetting.recordedSound.value = SoundOption.Disabled.name
    }

    override fun updateSoundVolume(volume: Float) {
        AppSetting.recordedSoundVolume.value = volume
    }

    override fun selectSoundFile(file: SoundFile) {
        AppSetting.recordedSound.value = file.fileName
    }

    override fun deleteSoundFile(file: SoundFile) {
        if (file.canBeDeleted && !file.selected) {
            val customSounds = AppSetting.customRecordedSounds.data
            AppSetting.customRecordedSounds.value = customSounds.value.toMutableSet().apply {
                remove(file.fileName)
            }
            Path.commonInternalPath(get(), "${FolderType.SoundFolder.Recorded}/${file.fileName}").commonDelete()
        }
    }

    override fun toggleAudioPlayer() {
        if (isAudioPlaying.value) {
            localAudioService.stop()
        } else {
            localAudioService.playRecordedSound()
        }
    }

    override fun chooseSoundFile() {
        viewModelScope.launch {
            FileUtils.selectFile(FolderType.SoundFolder.Recorded)
                ?.also { fileName ->
                    val customSounds = AppSetting.customRecordedSounds.data
                    AppSetting.customRecordedSounds.value =
                        customSounds.value.toMutableSet().apply {
                            add(fileName.name)
                        }
                    AppSetting.recordedSound.value = fileName.name
                }
        }
    }

}