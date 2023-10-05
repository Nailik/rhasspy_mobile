package org.rhasspy.mobile.ui.configuration.domains.handle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import org.rhasspy.mobile.data.service.option.HandleDomainOption
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption.Event
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption.Intent
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.list.RadioButtonListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.ui.theme.TonalElevationLevel1
import org.rhasspy.mobile.viewmodel.configuration.domains.handle.HandleDomainConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.domains.handle.HandleDomainConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.domains.handle.HandleDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.handle.HandleDomainConfigurationViewState.HandleDomainConfigurationData

/**
 * content for intent handling configuration
 * drop down to select option
 * http configuration
 * home assistant configuration
 */
@Composable
fun IntentHandlingConfigurationScreen(viewModel: HandleDomainConfigurationViewModel) {

    ScreenContent(
        title = MR.strings.intentHandling.stable,
        viewModel = viewModel,
        tonalElevation = TonalElevationLevel1,
    ) {

        val viewState by viewModel.viewState.collectAsState()

        IntentHandlingEditContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
private fun IntentHandlingEditContent(
    editData: HandleDomainConfigurationData,
    onEvent: (HandleDomainConfigurationUiEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {

        RadioButtonsEnumSelection(
            modifier = Modifier.testTag(TestTag.IntentHandlingOptions),
            selected = editData.handleDomainOption,
            onSelect = { onEvent(SelectHandleDomainOption(it)) },
            values = editData.handleDomainOptionLists
        ) { option ->

            when (option) {
                HandleDomainOption.HomeAssistant ->
                    HomeAssistantOption(
                        editData = editData,
                        onEvent = onEvent
                    )

                else                             -> Unit
            }

        }

    }

}

/**
 * configuration of home assistant intent handling
 * url
 * access token
 * hass event or intent
 */
@Composable
private fun HomeAssistantOption(
    editData: HandleDomainConfigurationData,
    onEvent: (HandleDomainConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //select home assistant event or home assistant intent
        RadioButtonListItem(
            modifier = Modifier.testTag(TestTag.SendIntents),
            text = MR.strings.homeAssistantIntents.stable,
            isChecked = editData.intentHandlingHomeAssistantOption == Intent,
            onClick = { onEvent(SelectHandleDomainHomeAssistantOption(Intent)) }
        )

        RadioButtonListItem(
            modifier = Modifier.testTag(TestTag.SendEvents),
            text = MR.strings.homeAssistantEvents.stable,
            isChecked = editData.intentHandlingHomeAssistantOption == Event,
            onClick = { onEvent(SelectHandleDomainHomeAssistantOption(Event)) }
        )

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = editData.intentHandlingHomeAssistantOption == Event
        ) {

            TextFieldListItem(
                label = MR.strings.intentHandlingTimeout.stable,
                value = editData.homeAssistantEventTimeout,
                onValueChange = { onEvent(UpdateHandleDomainHomeAssistantEventTimeout(it)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

        }

    }

}