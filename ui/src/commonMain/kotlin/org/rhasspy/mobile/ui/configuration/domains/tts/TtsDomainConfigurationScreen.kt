package org.rhasspy.mobile.ui.configuration.domains.tts

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.TonalElevationLevel1
import org.rhasspy.mobile.viewmodel.configuration.domains.tts.TtsDomainConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.domains.tts.TtsDomainConfigurationUiEvent.Change.SelectTtsDomainOption
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {

        item {
            TextToSpeechOptionContent(
                editData = editData,
                onEvent = onEvent
            )
        }

    }

}


@Composable
private fun TextToSpeechOptionContent(
    editData: TtsDomainConfigurationData,
    onEvent: (TtsDomainConfigurationUiEvent) -> Unit
) {

    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.TextToSpeechOptions),
        selected = editData.ttsDomainOption,
        onSelect = { onEvent(SelectTtsDomainOption(it)) },
        values = editData.ttsDomainOptions
    )

}