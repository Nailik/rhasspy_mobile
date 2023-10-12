package org.rhasspy.mobile.ui.configuration.pipeline

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.PipelineManagerOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.SecondaryContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.ui.theme.TonalElevationLevel1
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationUiEvent.Change.SelectPipelineOption
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationUiEvent.PipelineLocalUiEvent.Change.SelectSoundIndicationOutputOption
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationUiEvent.PipelineLocalUiEvent.Change.SetSoundIndicationEnabled
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationViewState.PipelineConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationViewState.PipelineConfigurationData.PipelineLocalConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationViewState.PipelineConfigurationData.PipelineLocalConfigurationData.IndicationSoundOptionType
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.PipelineConfigurationLocalIndicationSoundDestination.*

/**
 * DropDown to select dialog management option
 */
@Composable
fun DialogManagementConfigurationScreen(viewModel: PipelineConfigurationViewModel) {

    ScreenContent(
        title = MR.strings.dialog_pipeline.stable,
        viewModel = viewModel,
        tonalElevation = TonalElevationLevel1,
    ) {

        val viewState by viewModel.viewState.collectAsState()

        DialogManagementScreenContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
private fun DialogManagementScreenContent(
    editData: PipelineConfigurationData,
    onEvent: (PipelineConfigurationUiEvent) -> Unit
) {

    //drop down to select option
    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.DialogManagementOptions),
        selected = editData.pipelineManagerOption,
        onSelect = { onEvent(SelectPipelineOption(it)) },
        values = editData.pipelineManagerOptionList
    ) { option ->
        when (option) {
            PipelineManagerOption.Local -> TODO()
            PipelineManagerOption.Rhasspy2HermesMQTT -> TODO()
            PipelineManagerOption.Disabled -> TODO()
        }
    }

}

@Composable
private fun DialogManagementLocalScreenContent(
    editData: PipelineLocalConfigurationData,
    onEvent: (PipelineConfigurationUiEvent) -> Unit
) {


//sound indication
    SwitchListItem(
        modifier = Modifier.testTag(TestTag.SoundIndicationEnabled),
        text = MR.strings.wakeWordAudioIndication.stable,
        isChecked = editData.isSoundIndicationEnabled,
        onCheckedChange = { onEvent(SetSoundIndicationEnabled(it)) }
    )


    //visibility of sounds settings
    SecondaryContent(visible = editData.isSoundIndicationEnabled) {

        SoundIndicationSettingsOverview(
            soundIndicationOutputOption = editData.soundIndicationOutputOption,
            audioOutputOptionList = editData.audioOutputOptionList,
            wakeSound = editData.wakeSound,
            recordedSound = editData.recordedSound,
            errorSound = editData.errorSound,
            onEvent = onEvent,
        )

    }

}


/**
 * overview page for indication settings
 */
@Composable
private fun SoundIndicationSettingsOverview(
    soundIndicationOutputOption: AudioOutputOption,
    audioOutputOptionList: ImmutableList<AudioOutputOption>,
    wakeSound: IndicationSoundOptionType,
    recordedSound: IndicationSoundOptionType,
    errorSound: IndicationSoundOptionType,
    onEvent: (PipelineConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        RadioButtonsEnumSelectionList(
            modifier = Modifier.testTag(TestTag.AudioOutputOptions),
            selected = soundIndicationOutputOption,
            onSelect = { onEvent(SelectSoundIndicationOutputOption(it)) },
            values = audioOutputOptionList
        )

        //opens page for sounds

        //wake sound
        ListElement(
            modifier = Modifier
                .testTag(WakeIndicationSoundScreen)
                .clickable { onEvent(Navigate(WakeIndicationSoundScreen)) },
            text = { Text(MR.strings.wakeWord.stable) },
            secondaryText = { Text(resource = wakeSound.title) }
        )

        //recorded sound
        ListElement(
            modifier = Modifier
                .testTag(RecordedIndicationSoundScreen)
                .clickable { onEvent(Navigate(RecordedIndicationSoundScreen)) },
            text = { Text(MR.strings.recordedSound.stable) },
            secondaryText = { Text(resource = recordedSound.title) }
        )

        //error sound
        ListElement(
            modifier = Modifier
                .testTag(ErrorIndicationSoundScreen)
                .clickable { onEvent(Navigate(ErrorIndicationSoundScreen)) },
            text = { Text(MR.strings.errorSound.stable) },
            secondaryText = { Text(resource = errorSound.title) }
        )

    }

}