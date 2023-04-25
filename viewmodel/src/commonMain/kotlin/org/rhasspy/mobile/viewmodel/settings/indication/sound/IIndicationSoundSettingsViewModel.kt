package org.rhasspy.mobile.viewmodel.settings.indication.sound

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.Path
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.platformspecific.file.FileUtils
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.updateList
import org.rhasspy.mobile.settings.ISetting
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.Action.ChooseSoundFile
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.Action.ToggleAudioPlayerActive
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.Change.*
import kotlin.reflect.KFunction1

@Stable
abstract class IIndicationSoundSettingsViewModel(
    private val localAudioService: LocalAudioService,
    private val nativeApplication: NativeApplication,
    private val customSoundOptions: ISetting<ImmutableList<String>>,
    private val soundSetting: ISetting<String>,
    private val soundVolume: ISetting<Float>,
    private val soundFolderType: FolderType,
    viewStateCreator: IIndicationSoundSettingsViewStateCreator
) : ViewModel(), KoinComponent {

    abstract val playSound: KFunction1<LocalAudioService, Unit>

    val viewState: StateFlow<IIndicationSoundSettingsViewState> = viewStateCreator()

    fun onEvent(event: IIndicationSoundSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is SetSoundFile -> soundSetting.value = change.file
            is SetSoundIndicationOption -> soundSetting.value = change.option.name
            is UpdateSoundVolume -> soundVolume.value = change.volume
            is AddSoundFile -> {
                val customSounds = customSoundOptions.value.updateList {
                    add(change.file)
                }
                customSoundOptions.value = customSounds
                soundSetting.value = change.file
            }

            is DeleteSoundFile -> {
                if (viewState.value.soundSetting != change.file) {
                    val customSounds = customSoundOptions.value.updateList {
                        remove(change.file)
                    }
                    customSoundOptions.value = customSounds
                    Path.commonInternalPath(
                        nativeApplication = nativeApplication,
                        fileName = "${soundFolderType}/${change.file}"
                    ).commonDelete()
                }
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            ChooseSoundFile -> {
                viewModelScope.launch {
                    FileUtils.selectFile(soundFolderType)?.also { path -> onEvent(AddSoundFile(path.name)) }
                }
            }

            ToggleAudioPlayerActive ->
                if (localAudioService.isPlayingState.value) localAudioService.stop() else playSound(localAudioService)
        }
    }

    fun onPause() {
        localAudioService.stop()
    }

}