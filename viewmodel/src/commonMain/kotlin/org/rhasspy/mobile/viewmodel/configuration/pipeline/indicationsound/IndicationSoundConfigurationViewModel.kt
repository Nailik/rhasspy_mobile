package org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound

import androidx.compose.runtime.Stable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Path
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.sounds.IndicationSoundOption.Custom
import org.rhasspy.mobile.data.sounds.IndicationSoundType
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalFilePath
import org.rhasspy.mobile.platformspecific.file.FileUtils
import org.rhasspy.mobile.platformspecific.file.FolderType.SoundFolder
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound.IndicationSoundConfigurationUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound.IndicationSoundConfigurationUiEvent.Action.ChooseSoundFile
import org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound.IndicationSoundConfigurationUiEvent.Action.ToggleAudioPlayerActive
import org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound.IndicationSoundConfigurationUiEvent.Change.SetSoundIndicationOption
import org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound.IndicationSoundConfigurationUiEvent.Change.UpdateSoundVolume
import org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound.IndicationSoundConfigurationUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
abstract class IndicationSoundConfigurationViewModel(
    private val indicationSoundType: IndicationSoundType,
    private val userConnection: IUserConnection,
    private val mapper: IndicationSoundConfigurationDataMapper,
    private val nativeApplication: NativeApplication,
    audioPlayerViewStateCreator: AudioPlayerViewStateCreator,
) : ScreenViewModel() {

    private val data
        get() = when (indicationSoundType) {
            IndicationSoundType.Error    -> ConfigurationSetting.pipelineData.value.localPipelineData.errorSound
            IndicationSoundType.Recorded -> ConfigurationSetting.pipelineData.value.localPipelineData.recordedSound
            IndicationSoundType.Wake     -> ConfigurationSetting.pipelineData.value.localPipelineData.wakeSound
        }

    private val soundFolder = when (indicationSoundType) {
        IndicationSoundType.Error    -> SoundFolder.Error
        IndicationSoundType.Wake     -> SoundFolder.Wake
        IndicationSoundType.Recorded -> SoundFolder.Recorded
    }

    private val _viewState = MutableStateFlow(
        IndicationSoundConfigurationViewState(
            editData = mapper(indicationSoundType, ConfigurationSetting.pipelineData.value),
            audioPlayerViewState = audioPlayerViewStateCreator(),
            snackBarText = null,
        )
    )
    val viewState = _viewState.readOnly

    fun onEvent(event: IndicationSoundConfigurationUiEvent) {
        when (event) {
            is Change   -> onChange(event)
            is Action   -> onAction(event)
            is Consumed -> onConsumed(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SetSoundIndicationOption -> copy(option = change.option)
                    is UpdateSoundVolume        -> copy(volume = change.volume)
                }
            })
        }

        (data.option as? Custom)?.deleteFile()

        ConfigurationSetting.pipelineData.value = mapper(indicationSoundType, _viewState.value.editData)
    }

    private fun onAction(action: Action) {
        when (action) {
            ChooseSoundFile         -> {
                viewModelScope.launch {
                    FileUtils.selectFile(soundFolder)?.also { path ->
                        //TODO #466 test
                        onEvent(SetSoundIndicationOption(Custom(path.name)))
                    } ?: run {
                        _viewState.update {
                            it.copy(snackBarText = MR.strings.selectFileFailed.stable)
                        }
                    }
                }
            }

            ToggleAudioPlayerActive -> {
                if (userConnection.isPlayingState.value) {
                    userConnection.stopPlaySound()
                } else {
                    viewModelScope.launch(Dispatchers.IO) {
                        userConnection.playSound(
                            indicationSoundType = indicationSoundType,
                            soundIndicationOutputOption = ConfigurationSetting.pipelineData.value.localPipelineData.soundIndicationOutputOption,
                            indicationSound = _viewState.value.editData.option,
                            volume = _viewState.value.editData.volume,
                        )
                    }
                }
            }
        }
    }

    private fun Custom.deleteFile() {
        Path.commonInternalFilePath(
            nativeApplication = nativeApplication,
            fileName = "$soundFolder/$file"
        ).commonDelete()
    }

    private fun onConsumed(consumed: Consumed) {
        _viewState.update {
            when (consumed) {
                ShowSnackBar -> it.copy(snackBarText = null)
            }
        }
    }


    override fun onDismissed() {
        userConnection.stopPlaySound()
        super.onDismissed()
    }

}