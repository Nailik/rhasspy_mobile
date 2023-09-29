package org.rhasspy.mobile.ui.configuration.domains.asr

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.main.SettingsScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SelectSpeechToTextOption
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SetUseSpeechToTextMqttSilenceDetection
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewState.SpeechToTextConfigurationData

/**
 * Content to configure speech to text
 * Drop Down of state
 * HTTP Endpoint
 */
@Composable
fun SpeechToTextConfigurationScreen(viewModel: SpeechToTextConfigurationViewModel) {

    ScreenContent(
        screenViewModel = viewModel
    ) {
        SettingsScreenItemContent(
            title = MR.strings.speechToText.stable,
            onBackClick = { viewModel.onEvent(BackClick) }
        ) {

            val viewState by viewModel.viewState.collectAsState()

            SpeechToTextOptionEditContent(
                editData = viewState.editData,
                onEvent = viewModel::onEvent
            )

        }
    }

}

@Composable
private fun SpeechToTextOptionEditContent(
    editData: SpeechToTextConfigurationData,
    onEvent: (SpeechToTextConfigurationUiEvent) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {

        item {
            SpeechToTextOption(
                editData = editData,
                onEvent = onEvent
            )
        }

    }

}


@Composable
private fun SpeechToTextOption(
    editData: SpeechToTextConfigurationData,
    onEvent: (SpeechToTextConfigurationUiEvent) -> Unit
) {
    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.SpeechToTextOptions),
        selected = editData.speechToTextOption,
        onSelect = { onEvent(SelectSpeechToTextOption(it)) },
        values = editData.speechToTextOptions
    ) {

        when (it) {
            SpeechToTextOption.Rhasspy2HermesHttp -> Unit

            SpeechToTextOption.Rhasspy2HermesMQTT -> SpeechToTextMqtt(
                isUseSpeechToTextMqttSilenceDetection = editData.isUseSpeechToTextMqttSilenceDetection,
                onEvent = onEvent,
            )

            else                                  -> Unit
        }

    }
}


/**
 * mqtt silence detection settings
 */
@Composable
private fun SpeechToTextMqtt(
    isUseSpeechToTextMqttSilenceDetection: Boolean,
    onEvent: (SpeechToTextConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //switch to use silence detection
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.MqttSilenceDetectionSwitch),
            text = MR.strings.useMqttSilenceDetection.stable,
            isChecked = isUseSpeechToTextMqttSilenceDetection,
            onCheckedChange = { onEvent(SetUseSpeechToTextMqttSilenceDetection(it)) }
        )

    }

}