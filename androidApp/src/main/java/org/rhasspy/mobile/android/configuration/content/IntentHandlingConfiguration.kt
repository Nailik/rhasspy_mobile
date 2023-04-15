package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenConfig
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreenType
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.android.content.list.RadioButtonListItem
import org.rhasspy.mobile.android.content.list.TextFieldListItem
import org.rhasspy.mobile.android.content.list.TextFieldListItemVisibility
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.ContentPaddingLevel1
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption.Event
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption.Intent
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiAction
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiAction.ChangeIntentHandlingHassAccessToken
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiAction.ChangeIntentHandlingHassEndpoint
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiAction.ChangeIntentHandlingHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiAction.SelectIntentHandlingHassOption
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiAction.SelectIntentHandlingOption
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewState

/**
 * content for intent handling configuration
 * drop down to select option
 * http configuration
 * home assistant configuration
 */
@Preview
@Composable
fun IntentHandlingConfigurationContent(viewModel: IntentHandlingConfigurationViewModel = get()) {

    val viewState by viewModel.viewState.collectAsState()
    val contentViewState by viewState.editViewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreenType.IntentHandlingConfiguration),
        config = ConfigurationScreenConfig(MR.strings.intentHandling.stable),
        viewState = viewState,
        onAction = { viewModel.onAction(it) },
        onConsumed = { viewModel.onConsumed(it) },
        testContent = { TestContent(viewModel) }
    ) {

        item {
            IntentHandlingOptionContent(
                viewState = contentViewState,
                onAction = viewModel::onAction
            )
        }
    }

}

@Composable
private fun IntentHandlingOptionContent(
    viewState: IntentHandlingConfigurationViewState,
    onAction: (IntentHandlingConfigurationUiAction) -> Unit
) {

    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.IntentHandlingOptions),
        selected = viewState.intentHandlingOption,
        onSelect = { onAction(SelectIntentHandlingOption(it)) },
        values = viewState.intentHandlingOptionList
    ) { option ->

        when (option) {
            IntentHandlingOption.HomeAssistant -> HomeAssistantOption(
                intentHandlingHassEndpoint = viewState.intentHandlingHassEndpoint,
                intentHandlingHassAccessToken = viewState.intentHandlingHassAccessToken,
                intentHandlingHassOption = viewState.intentHandlingHassOption,
                onAction = onAction
            )

            IntentHandlingOption.RemoteHTTP -> RemoteHTTPOption(
                intentHandlingHttpEndpoint = viewState.intentHandlingHttpEndpoint,
                onAction = onAction
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
    onAction: (IntentHandlingConfigurationUiAction) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //endpoint input field
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.Endpoint),
            value = intentHandlingHttpEndpoint,
            onValueChange = { onAction(ChangeIntentHandlingHttpEndpoint(it)) },
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
    onAction: (IntentHandlingConfigurationUiAction) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //endpoint url
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.Endpoint),
            value = intentHandlingHassEndpoint,
            onValueChange = { onAction(ChangeIntentHandlingHassEndpoint(it)) },
            label = MR.strings.hassURL.stable,
            isLastItem = false
        )

        //hass access token
        TextFieldListItemVisibility(
            modifier = Modifier.testTag(TestTag.AccessToken),
            value = intentHandlingHassAccessToken,
            onValueChange = { onAction(ChangeIntentHandlingHassAccessToken(it)) },
            label = MR.strings.accessToken.stable
        )

        //select hass event or hass intent
        RadioButtonListItem(
            modifier = Modifier.testTag(TestTag.SendEvents),
            text = MR.strings.homeAssistantEvents.stable,
            isChecked = intentHandlingHassOption == Event,
            onClick = { onAction(SelectIntentHandlingHassOption(Event)) }
        )

        RadioButtonListItem(
            modifier = Modifier.testTag(TestTag.SendIntents),
            text = MR.strings.homeAssistantIntents.stable,
            isChecked = intentHandlingHassOption == Intent,
            onClick = { onAction(SelectIntentHandlingHassOption(Intent)) }
        )

    }

}

/**
 * show test inputs
 */
@Composable
private fun TestContent(viewModel: IntentHandlingConfigurationViewModel) {

    Column {

        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.IntentNameText),
            value = viewModel.testIntentNameText.collectAsState().value,
            onValueChange = viewModel::updateTestIntentNameText,
            label = MR.strings.intentName.stable
        )

        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.IntentText),
            value = viewModel.testIntentText.collectAsState().value,
            onValueChange = viewModel::updateTestIntentText,
            label = MR.strings.intentText.stable
        )

        FilledTonalButtonListItem(
            text = MR.strings.executeHandleIntent.stable,
            onClick = viewModel::testIntentHandling
        )

    }

}