package org.rhasspy.mobile.ui.configuration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption.Event
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption.Intent
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.list.RadioButtonListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItemVisibility
import org.rhasspy.mobile.ui.main.ConfigurationScreenItemEdit
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewState.IntentHandlingConfigurationData

/**
 * content for intent handling configuration
 * drop down to select option
 * http configuration
 * home assistant configuration
 */
@Composable
fun IntentHandlingEditConfigurationScreen() {

    val viewModel: IntentHandlingConfigurationViewModel = LocalViewModelFactory.current.getViewModel()

    val configurationEditViewState by viewModel.configurationEditViewState.collectAsState()

    ConfigurationScreenItemEdit(
        modifier = Modifier,
        kViewModel = viewModel,
        title = MR.strings.intentHandling.stable,
        viewState = configurationEditViewState,
        onEvent = viewModel::onEvent
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
        selected = editData.intentHandlingOption,
        onSelect = { onEvent(SelectIntentHandlingOption(it)) },
        values = editData.intentHandlingOptionList
    ) { option ->

        when (option) {
            IntentHandlingOption.HomeAssistant -> HomeAssistantOption(
                intentHandlingHassEndpoint = editData.intentHandlingHomeAssistantEndpoint,
                intentHandlingHassAccessToken = editData.intentHandlingHomeAssistantAccessToken,
                intentHandlingHassOption = editData.intentHandlingHomeAssistantOption,
                onEvent = onEvent
            )

            IntentHandlingOption.RemoteHTTP -> RemoteHTTPOption(
                intentHandlingHttpEndpoint = editData.intentHandlingHttpEndpoint,
                onEvent = onEvent
            )

            else -> {}
        }

    }

}

/**
 * http configuration for intent handling
 * field to set endpoint
 */
@Composable
private fun RemoteHTTPOption(
    intentHandlingHttpEndpoint: String,
    onEvent: (IntentHandlingConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //endpoint input field
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.Endpoint),
            value = intentHandlingHttpEndpoint,
            onValueChange = { onEvent(ChangeIntentHandlingHttpEndpoint(it)) },
            label = MR.strings.remoteURL.stable
        )

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
    intentHandlingHassEndpoint: String,
    intentHandlingHassAccessToken: String,
    intentHandlingHassOption: HomeAssistantIntentHandlingOption,
    onEvent: (IntentHandlingConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //endpoint url
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.Endpoint),
            value = intentHandlingHassEndpoint,
            onValueChange = { onEvent(ChangeIntentHandlingHomeAssistantEndpoint(it)) },
            label = MR.strings.hassURL.stable,
            isLastItem = false
        )

        //hass access token
        TextFieldListItemVisibility(
            modifier = Modifier.testTag(TestTag.AccessToken),
            value = intentHandlingHassAccessToken,
            onValueChange = { onEvent(ChangeIntentHandlingHomeAssistantAccessToken(it)) },
            label = MR.strings.accessToken.stable
        )

        //select hass event or hass intent
        RadioButtonListItem(
            modifier = Modifier.testTag(TestTag.SendEvents),
            text = MR.strings.homeAssistantEvents.stable,
            isChecked = intentHandlingHassOption == Event,
            onClick = { onEvent(SelectIntentHandlingHomeAssistantOption(Event)) }
        )

        RadioButtonListItem(
            modifier = Modifier.testTag(TestTag.SendIntents),
            text = MR.strings.homeAssistantIntents.stable,
            isChecked = intentHandlingHassOption == Intent,
            onClick = { onEvent(SelectIntentHandlingHomeAssistantOption(Intent)) }
        )

    }

}