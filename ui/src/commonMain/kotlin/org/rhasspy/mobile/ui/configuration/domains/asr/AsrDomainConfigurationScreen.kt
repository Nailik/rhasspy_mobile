package org.rhasspy.mobile.ui.configuration.domains.asr

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
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.AsrDomainOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.ui.theme.TonalElevationLevel1
import org.rhasspy.mobile.viewmodel.configuration.asr.AsrConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.asr.AsrConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.asr.AsrConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.asr.AsrConfigurationViewState.AsrConfigurationData

/**
 * Content to configure speech to text
 * Drop Down of state
 * HTTP Endpoint
 */
@Composable
fun SpeechToTextConfigurationScreen(viewModel: AsrConfigurationViewModel) {

    ScreenContent(
        title = MR.strings.speechToText.stable,
        viewModel = viewModel,
        tonalElevation = TonalElevationLevel1,
    ) {
        val viewState by viewModel.viewState.collectAsState()

        SpeechToTextOptionEditContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
private fun SpeechToTextOptionEditContent(
    editData: AsrConfigurationData,
    onEvent: (AsrConfigurationUiEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {

        SpeechToTextOption(
            editData = editData,
            onEvent = onEvent
        )

    }

}


@Composable
private fun SpeechToTextOption(
    editData: AsrConfigurationData,
    onEvent: (AsrConfigurationUiEvent) -> Unit
) {
    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.SpeechToTextOptions),
        selected = editData.asrDomainOption,
        onSelect = { onEvent(SelectAsrOption(it)) },
        values = editData.asrDomainOptions
    ) {

        when (it) {
            AsrDomainOption.Rhasspy2HermesHttp ->
                SpeechToTextRhasspy2HermesHttp(
                    editData = editData,
                    onEvent = onEvent,
                )

            AsrDomainOption.Rhasspy2HermesMQTT ->
                SpeechToTextRhasspy2HermesMQTT(
                    editData = editData,
                    onEvent = onEvent,
                )

            else                               -> Unit
        }

    }
}

@Composable
private fun SpeechToTextRhasspy2HermesHttp(
    editData: AsrConfigurationData,
    onEvent: (AsrConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        TextFieldListItem(
            label = MR.strings.asrVoiceTimeout.stable,
            modifier = Modifier,
            value = editData.voiceTimeoutText,
            onValueChange = { onEvent(UpdateVoiceTimeout(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

    }

}

/**
 * mqtt silence detection settings
 */
@Composable
private fun SpeechToTextRhasspy2HermesMQTT(
    editData: AsrConfigurationData,
    onEvent: (AsrConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        TextFieldListItem(
            label = MR.strings.asrVoiceTimeout.stable,
            modifier = Modifier,
            value = editData.voiceTimeoutText,
            onValueChange = { onEvent(UpdateVoiceTimeout(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        TextFieldListItem(
            label = MR.strings.mqttResultTimeout.stable,
            modifier = Modifier,
            value = editData.mqttResultTimeoutText,
            onValueChange = { onEvent(UpdateMqttResultTimeout(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        //switch to use silence detection
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.MqttSilenceDetectionSwitch),
            text = MR.strings.useMqttSilenceDetection.stable,
            isChecked = editData.isUseSpeechToTextMqttSilenceDetection,
            onCheckedChange = { onEvent(SetUseAsrMqttSilenceDetection(it)) }
        )

    }

}