package org.rhasspy.mobile.ui.configuration.domains.handle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.HandleDomainOption
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption.Event
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption.Intent
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.list.RadioButtonListItem
import org.rhasspy.mobile.ui.main.SettingsScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Change.SelectIntentHandlingHomeAssistantOption
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Change.SelectIntentHandlingOption
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewState.IntentHandlingConfigurationData

/**
 * content for intent handling configuration
 * drop down to select option
 * http configuration
 * home assistant configuration
 */
@Composable
fun IntentHandlingConfigurationScreen(viewModel: IntentHandlingConfigurationViewModel) {

    ScreenContent(
        screenViewModel = viewModel
    ) {
        SettingsScreenItemContent(
            title = MR.strings.intentHandling.stable,
            onBackClick = { viewModel.onEvent(BackClick) }
        ) {

            val viewState by viewModel.viewState.collectAsState()

            IntentHandlingEditContent(
                editData = viewState.editData,
                onEvent = viewModel::onEvent
            )

        }

    }
}

@Composable
private fun IntentHandlingEditContent(
    editData: IntentHandlingConfigurationData,
    onEvent: (IntentHandlingConfigurationUiEvent) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {

        item {
            IntentHandlingOptionContent(
                editData = editData,
                onEvent = onEvent
            )
        }

    }

}

@Composable
private fun IntentHandlingOptionContent(
    editData: IntentHandlingConfigurationData,
    onEvent: (IntentHandlingConfigurationUiEvent) -> Unit
) {

    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.IntentHandlingOptions),
        selected = editData.handleDomainOption,
        onSelect = { onEvent(SelectIntentHandlingOption(it)) },
        values = editData.handleDomainOptionLists
    ) { option ->

        when (option) {
            HandleDomainOption.HomeAssistant -> HomeAssistantOption(
                intentHandlingHomeAssistantOption = editData.intentHandlingHomeAssistantOption,
                onEvent = onEvent
            )

            else -> Unit
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
    intentHandlingHomeAssistantOption: HomeAssistantIntentHandlingOption,
    onEvent: (IntentHandlingConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //select home assistant event or home assistant intent
        RadioButtonListItem(
            modifier = Modifier.testTag(TestTag.SendEvents),
            text = MR.strings.homeAssistantEvents.stable,
            isChecked = intentHandlingHomeAssistantOption == Event,
            onClick = { onEvent(SelectIntentHandlingHomeAssistantOption(Event)) }
        )

        RadioButtonListItem(
            modifier = Modifier.testTag(TestTag.SendIntents),
            text = MR.strings.homeAssistantIntents.stable,
            isChecked = intentHandlingHomeAssistantOption == Intent,
            onClick = { onEvent(SelectIntentHandlingHomeAssistantOption(Intent)) }
        )

    }

}