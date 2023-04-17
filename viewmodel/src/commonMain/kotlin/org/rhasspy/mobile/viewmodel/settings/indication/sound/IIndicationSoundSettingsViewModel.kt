package org.rhasspy.mobile.viewmodel.settings.indication.sound

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Path
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.logic.settings.ISetting
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.platformspecific.file.FileUtils
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.volume.DeviceVolume
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.Action.ChooseSoundFile
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.Action.ToggleAudioPlayerActive
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.Change.*
import kotlin.reflect.KFunction1

abstract class IIndicationSoundSettingsViewModel(
    private val localAudioService: LocalAudioService,
    private val nativeApplication: NativeApplication,
    private val customSoundOptions: ISetting<ImmutableList<String>>,
    private  val soundSetting: ISetting<String>,
    private val soundVolume: ISetting<Float>,
    private val soundFolderType: FolderType
) : ViewModel(), KoinComponent {

    abstract val playSound: KFunction1<LocalAudioService, Unit>


    private val _viewState =
        MutableStateFlow(
            IIndicationSoundSettingsViewState.getInitialViewState(
                soundSetting = soundSetting.value,
                customSoundFiles = customSoundOptions.value,
                soundVolume = soundVolume.value,
                localAudioService = localAudioService
            )
        )
    val viewState = _viewState.readOnly

    init {
        viewModelScope.launch(Dispatchers.Default) {
            combineStateFlow(
                DeviceVolume.volumeFlowSound,
                DeviceVolume.volumeFlowNotification,
                localAudioService.isPlayingState
            ).collect { data ->
                _viewState.update {
                    it.copy(
                        isNoSoundInformationBoxVisible = when (AppSetting.soundIndicationOutputOption.value) {
                            AudioOutputOption.Sound -> data[0] as Int? == 0
                            AudioOutputOption.Notification -> data[1] as Int? == 0
                        },
                        isAudioPlaying = data[2] as Boolean
                    )
                }
            }
        }
    }

    fun onEvent(event: IIndicationSoundSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            when (change) {
                is SetSoundFile -> {
                    soundSetting.value = change.file
                    it.copy(soundSetting = change.file)
                }

                is SetSoundIndicationOption -> {
                    soundSetting.value = change.option.name
                    it.copy(soundSetting = change.option.name)
                }

                is UpdateSoundVolume -> {
                    soundVolume.value = change.volume
                    it.copy(soundVolume = change.volume)
                }

                is AddSoundFile -> {
                    val customSounds = it.customSoundFiles
                        .toMutableList()
                        .apply { remove(change.file) }
                        .toImmutableList()
                    soundSetting.value = change.file
                    it.copy(
                        soundSetting = change.file,
                        customSoundFiles = customSounds
                    )
                }

                is DeleteSoundFile -> {
                    if (viewState.value.soundSetting != change.file) {
                        val customSounds = it.customSoundFiles
                            .toMutableList()
                            .apply { remove(change.file) }
                            .toImmutableList()
                        customSoundOptions.value = customSounds
                        Path.commonInternalPath(
                            nativeApplication = nativeApplication,
                            fileName = "${soundFolderType}/${change.file}"
                        ).commonDelete()
                        it.copy(customSoundFiles = customSounds)
                    } else it
                }
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            ChooseSoundFile -> {
                viewModelScope.launch {
                    FileUtils.selectFile(soundFolderType)
                        ?.also { path -> onEvent(AddSoundFile(path.name)) }
                }
            }

            ToggleAudioPlayerActive ->
                if (localAudioService.isPlayingState.value) {
                    localAudioService.stop()
                } else {
                    playSound(localAudioService)
                }
        }
    }

    fun onPause() {
        localAudioService.stop()
    }

}