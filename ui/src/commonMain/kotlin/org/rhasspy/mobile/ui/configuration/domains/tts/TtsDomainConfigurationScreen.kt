package org.rhasspy.mobile.ui.configuration.domains.tts

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
import org.rhasspy.mobile.data.service.option.TtsDomainOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.ui.theme.TonalElevationLevel1
import org.rhasspy.mobile.viewmodel.configuration.domains.tts.TtsDomainConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.domains.tts.TtsDomainConfigurationUiEvent.Change.SelectTtsDomainOption
import org.rhasspy.mobile.viewmodel.configuration.domains.tts.TtsDomainConfigurationUiEvent.Change.UpdateRhasspy2HermesMqttTimeout
import org.rhasspy.mobile.viewmodel.configuration.domains.tts.TtsDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.tts.TtsDomainConfigurationViewState.TtsDomainConfigurationData

/**
 * Content to configure text to speech
 * Drop Down of state
 * HTTP Endpoint
 */
@Composable
fun TextToSpeechConfigurationScreen(viewModel: TtsDomainConfigurationViewModel) {

    ScreenContent(
        title = MR.strings.textToSpeech.stable,
        viewModel = viewModel,
        tonalElevation = TonalElevationLevel1,
    ) {

        val viewState by viewModel.viewState.collectAsState()

        TextToSpeechEditContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
private fun TextToSpeechEditContent(
    editData: TtsDomainConfigurationData,
    onEvent: (TtsDomainConfigurationUiEvent) -> Unit
) {

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {

        RadioButtonsEnumSelection(
            modifier = Modifier.testTag(TestTag.TextToSpeechOptions),
            selected = editData.ttsDomainOption,
            onSelect = { onEvent(SelectTtsDomainOption(it)) },
            values = editData.ttsDomainOptions
        ) {

            when (it) {
                TtsDomainOption.Rhasspy2HermesHttp -> Unit
                TtsDomainOption.Rhasspy2HermesMQTT ->
                    TtsRhasspy2HermesMQTT(
                        timeout = editData.rhasspy2HermesMqttTimeout,
                        onEvent = onEvent,
                    )

                TtsDomainOption.Disabled           -> Unit
            }

        }

    }

}

@Composable
private fun TtsRhasspy2HermesMQTT(
    timeout: String,
    onEvent: (TtsDomainConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        TextFieldListItem(
            label = MR.strings.mqttResultTimeout.stable,
            value = timeout,
            onValueChange = { onEvent(UpdateRhasspy2HermesMqttTimeout(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

    }

}