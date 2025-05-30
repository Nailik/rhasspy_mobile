package org.rhasspy.mobile.viewmodel.settings.indication.sound

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Path
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.logic.services.localaudio.ILocalAudioService
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.platformspecific.file.FileUtils
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.updateList
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.ISetting
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.*
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.Consumed.ShowSnackBar
import kotlin.reflect.KFunction1

@Stable
abstract class IIndicationSoundSettingsViewModel(
    private val localAudioService: ILocalAudioService,
    private val nativeApplication: NativeApplication,
    private val customSoundOptions: ISetting<ImmutableList<String>>,
    private val soundSetting: ISetting<String>,
    private val soundVolume: ISetting<Float>,
    private val soundFolderType: FolderType,
) : ScreenViewModel() {

    val viewStateCreator: IIndicationSoundSettingsViewStateCreator =
        get { parametersOf(customSoundOptions, soundSetting, soundVolume) }
    abstract val playSound: KFunction1<ILocalAudioService, Unit>

    private val _viewState: MutableStateFlow<IIndicationSoundSettingsViewState> = viewStateCreator()
    val viewState = _viewState.readOnly

    fun onEvent(event: IIndicationSoundSettingsUiEvent) {
        when (event) {
            is Change   -> onChange(event)
            is Action   -> onAction(event)
            is Consumed -> onConsumed(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is SetSoundFile             -> soundSetting.value = change.file
            is SetSoundIndicationOption -> soundSetting.value = change.option.name
            is UpdateSoundVolume        -> soundVolume.value = change.volume
            is AddSoundFile             -> {
                val customSounds = customSoundOptions.value.updateList {
                    add(change.file)
                }
                customSoundOptions.value = customSounds
                soundSetting.value = change.file
            }

            is DeleteSoundFile          -> {
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
            ChooseSoundFile         -> {
                viewModelScope.launch {
                    FileUtils.selectFile(soundFolderType)?.also { path ->
                        onEvent(AddSoundFile(path.name))
                    } ?: run {
                        _viewState.update {
                            it.copy(snackBarText = MR.strings.selectFileFailed.stable)
                        }
                    }
                }
            }

            ToggleAudioPlayerActive ->
                if (localAudioService.isPlayingState.value) localAudioService.stop() else playSound(
                    localAudioService
                )

            BackClick               -> navigator.onBackPressed()
        }
    }


    private fun onConsumed(consumed: Consumed) {
        _viewState.update {
            when (consumed) {
                ShowSnackBar -> it.copy(snackBarText = null)
            }
        }
    }

    fun onPause() {
        localAudioService.stop()
    }

}