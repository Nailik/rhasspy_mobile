package org.rhasspy.mobile.ui.configuration.domains.snd

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.SndDomainOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.ui.theme.TonalElevationLevel1
import org.rhasspy.mobile.viewmodel.configuration.domains.snd.AudioPlayingConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.domains.snd.AudioPlayingConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.domains.snd.AudioPlayingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.snd.AudioPlayingConfigurationViewState.SndDomainConfigurationData

/**
 * Content to configure audio playing
 * Drop Down of state
 * HTTP Endpoint
 */
@Composable
fun SndDomainConfigurationScreen(viewModel: AudioPlayingConfigurationViewModel) {

    ScreenContent(
        title = MR.strings.audioPlaying.stable,
        viewModel = viewModel,
        tonalElevation = TonalElevationLevel1,
    ) {
        val viewState by viewModel.viewState.collectAsState()

        SndDomainScreenContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
private fun SndDomainScreenContent(
    editData: SndDomainConfigurationData,
    onEvent: (AudioPlayingConfigurationUiEvent) -> Unit
) {

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        //radio buttons list of available values
        RadioButtonsEnumSelection(
            modifier = Modifier.testTag(TestTag.AudioPlayingOptions),
            selected = editData.sndDomainOption,
            onSelect = { onEvent(SelectEditAudioPlayingOption(it)) },
            values = editData.sndDomainOptionLists
        ) { option ->

            when (option) {
                SndDomainOption.Local              ->
                    SndDomainLocal(
                        audioOutputOption = editData.audioOutputOption,
                        audioOutputOptionList = editData.audioOutputOptionList,
                        audioTimeout = editData.audioTimeout,
                        onEvent = onEvent,
                    )

                SndDomainOption.Rhasspy2HermesHttp ->
                    SndDomainRhasspy2HermesHttp(
                        audioTimeout = editData.audioTimeout,
                        onEvent = onEvent,
                    )

                SndDomainOption.Rhasspy2HermesMQTT ->
                    SndDomainRhasspy2HermesMQTT(
                        audioPlayingMqttSiteId = editData.audioPlayingMqttSiteId,
                        timeout = editData.rhasspy2HermesMqttTimeout,
                        audioTimeout = editData.audioTimeout,
                        onEvent = onEvent,
                    )

                else                               -> Unit
            }

        }
    }

}

/**
 * show output options for local audio
 */
@Composable
private fun SndDomainLocal(
    audioOutputOption: AudioOutputOption,
    audioTimeout: String,
    audioOutputOptionList: ImmutableList<AudioOutputOption>,
    onEvent: (AudioPlayingConfigurationUiEvent) -> Unit
) {

    //visibility of local output options
    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        RadioButtonsEnumSelectionList(
            modifier = Modifier.testTag(TestTag.AudioOutputOptions),
            selected = audioOutputOption,
            onSelect = { onEvent(SelectAudioOutputOption(it)) },
            values = audioOutputOptionList
        )


        TextFieldListItem(
            label = MR.strings.audioTimeout.stable,
            value = audioTimeout,
            onValueChange = { onEvent(UpdateAudioTimeout(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

    }

}


@Composable
private fun SndDomainRhasspy2HermesHttp(
    audioTimeout: String,
    onEvent: (AudioPlayingConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        TextFieldListItem(
            label = MR.strings.mqttResultTimeout.stable,
            value = audioTimeout,
            onValueChange = { onEvent(UpdateAudioTimeout(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

    }

}

/**
 * show mqtt site id options
 */
@Composable
private fun SndDomainRhasspy2HermesMQTT(
    audioPlayingMqttSiteId: String,
    audioTimeout: String,
    timeout: String,
    onEvent: (AudioPlayingConfigurationUiEvent) -> Unit
) {

    //visibility of endpoint option
    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //http endpoint input field
        TextFieldListItem(
            label = MR.strings.siteId.stable,
            modifier = Modifier.testTag(TestTag.ConfigurationSiteId),
            value = audioPlayingMqttSiteId,
            onValueChange = { onEvent(ChangeEditAudioPlayingMqttSiteId(it)) },
        )

        TextFieldListItem(
            label = MR.strings.audioTimeout.stable,
            value = audioTimeout,
            onValueChange = { onEvent(UpdateAudioTimeout(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        TextFieldListItem(
            label = MR.strings.mqttResultTimeout.stable,
            value = timeout,
            onValueChange = { onEvent(UpdateRhasspy2HermesMqttTimeout(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

    }

}