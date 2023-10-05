package org.rhasspy.mobile.ui.configuration.domains.intent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IntentDomainOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.ui.theme.TonalElevationLevel1
import org.rhasspy.mobile.viewmodel.configuration.domains.intent.IntentDomainConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.domains.intent.IntentDomainConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.domains.intent.IntentDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.intent.IntentDomainConfigurationViewState.IntentDomainConfigurationData

/**
 * configuration content for intent recognition
 * drop down to select option
 * text field for endpoint
 */
@Composable
fun IntentRecognitionConfigurationScreen(viewModel: IntentDomainConfigurationViewModel) {

    ScreenContent(
        title = MR.strings.intentRecognition.stable,
        viewModel = viewModel,
        tonalElevation = TonalElevationLevel1,
    ) {

        val viewState by viewModel.viewState.collectAsState()

        IntentRecognitionEditContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent,
        )

    }

}

@Composable
fun IntentRecognitionEditContent(
    editData: IntentDomainConfigurationData,
    onEvent: (IntentDomainConfigurationUiEvent) -> Unit
) {

    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.IntentRecognitionOptions),
        selected = editData.intentDomainOption,
        onSelect = { onEvent(SelectIntentDomainOption(it)) },
        values = editData.intentDomainOptionLists,
    ) {

        when (it) {
            IntentDomainOption.Rhasspy2HermesHttp ->
                IntentRhasspy2HermesHttp(
                    editData = editData,
                    onEvent = onEvent,
                )

            IntentDomainOption.Rhasspy2HermesMQTT ->
                IntentRhasspy2HermesMQTT(
                    editData = editData,
                    onEvent = onEvent,
                )

            IntentDomainOption.Disabled           -> Unit
        }

    }

}

@Composable
fun IntentRhasspy2HermesHttp(
    editData: IntentDomainConfigurationData,
    onEvent: (IntentDomainConfigurationUiEvent) -> Unit,
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        SwitchListItem(
            text = MR.strings.handleWithRecognition.stable,
            isChecked = editData.isRhasspy2HermesHttpIntentHandleWithRecognition,
            onCheckedChange = { onEvent(SetRhasspy2HttpIntentIntentHandlingEnabled(it)) }
        )

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = editData.isRhasspy2HermesHttpIntentHandleWithRecognition
        ) {
            TextFieldListItem(
                label = MR.strings.intentHandlingTimeout.stable,
                value = editData.rhasspy2HermesHttpIntentHandlingTimeout,
                onValueChange = { onEvent(UpdateRhasspy2HttpIntentHandlingTimeout(it)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

        }
    }

}

@Composable
fun IntentRhasspy2HermesMQTT(
    editData: IntentDomainConfigurationData,
    onEvent: (IntentDomainConfigurationUiEvent) -> Unit,
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        TextFieldListItem(
            label = MR.strings.intentRecognitionTimeoutText.stable,
            value = editData.timeout,
            onValueChange = { onEvent(UpdateVoiceTimeout(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

    }

}
