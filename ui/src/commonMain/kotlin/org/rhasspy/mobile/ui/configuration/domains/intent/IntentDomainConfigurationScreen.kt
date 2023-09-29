package org.rhasspy.mobile.ui.configuration.domains.intent

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
import org.rhasspy.mobile.ui.main.SettingsScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Change.SelectIntentRecognitionOption
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewState.IntentRecognitionConfigurationData

/**
 * configuration content for intent recognition
 * drop down to select option
 * text field for endpoint
 */
@Composable
fun IntentRecognitionConfigurationScreen(viewModel: IntentRecognitionConfigurationViewModel) {

    ScreenContent(
        screenViewModel = viewModel
    ) {
        SettingsScreenItemContent(
            title = MR.strings.intentRecognition.stable,
            onBackClick = { viewModel.onEvent(BackClick) }
        ) {

            val viewState by viewModel.viewState.collectAsState()

            IntentRecognitionEditContent(
                editData = viewState.editData,
                onEvent = viewModel::onEvent
            )

        }
    }

}

@Composable
fun IntentRecognitionEditContent(
    editData: IntentRecognitionConfigurationData,
    onEvent: (IntentRecognitionConfigurationUiEvent) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {

        item {
            IntentRecognitionOptionContent(
                editData = editData,
                onEvent = onEvent
            )
        }

    }

}

@Composable
private fun IntentRecognitionOptionContent(
    editData: IntentRecognitionConfigurationData,
    onEvent: (IntentRecognitionConfigurationUiEvent) -> Unit
) {

    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.IntentRecognitionOptions),
        selected = editData.intentRecognitionOption,
        onSelect = { onEvent(SelectIntentRecognitionOption(it)) },
        values = editData.intentRecognitionOptionList
    )

}