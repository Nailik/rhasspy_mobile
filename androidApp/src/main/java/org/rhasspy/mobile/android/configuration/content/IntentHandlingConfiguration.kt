package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
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
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewModel

/**
 * content for intent handling configuration
 * drop down to select option
 * http configuration
 * home assistant configuration
 */
@Preview
@Composable
fun IntentHandlingConfigurationContent(viewModel: IntentHandlingConfigurationViewModel = get()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreenType.IntentHandlingConfiguration),
        title = MR.strings.intentHandling.stable,
        viewState = viewModel.viewState.collectAsState().value,
        onAction = viewModel::onAction,
        testContent = { TestContent(viewModel) }
    ) {

        item {
            //drop down to select option
            RadioButtonsEnumSelection(
                modifier = Modifier.testTag(TestTag.IntentHandlingOptions),
                selected = viewModel.intentHandlingOption.collectAsState().value,
                onSelect = viewModel::selectIntentHandlingOption,
                values = viewModel.intentHandlingOptionList
            ) {
                if (viewModel.isRemoteHttpSettingsVisible(it)) {
                    //http endpoint
                    RemoteHTTPOption(viewModel)
                }

                if (viewModel.isHomeAssistantSettingsVisible(it)) {
                    //home assistant settings
                    HomeAssistantOption(viewModel)
                }
            }
        }
    }

}

/**
 * http configuration for intent handling
 * field to set endpoint
 */
@Composable
private fun RemoteHTTPOption(viewModel: IntentHandlingConfigurationViewModel) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //endpoint input field
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.Endpoint),
            value = viewModel.intentHandlingHttpEndpoint.collectAsState().value,
            onValueChange = viewModel::changeIntentHandlingHttpEndpoint,
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
private fun HomeAssistantOption(viewModel: IntentHandlingConfigurationViewModel) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //endpoint url
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.Endpoint),
            value = viewModel.intentHandlingHassEndpoint.collectAsState().value,
            onValueChange = viewModel::changeIntentHandlingHassEndpoint,
            label = MR.strings.hassURL.stable,
            isLastItem = false
        )

        //hass access token
        TextFieldListItemVisibility(
            modifier = Modifier.testTag(TestTag.AccessToken),
            value = viewModel.intentHandlingHassAccessToken.collectAsState().value,
            onValueChange = viewModel::changeIntentHandlingHassAccessToken,
            label = MR.strings.accessToken.stable
        )

        //select hass event or hass intent
        RadioButtonListItem(
            modifier = Modifier.testTag(TestTag.SendEvents),
            text = MR.strings.homeAssistantEvents.stable,
            isChecked = viewModel.isIntentHandlingHassEvent.collectAsState().value,
            onClick = viewModel::selectIntentHandlingHassEvent
        )

        RadioButtonListItem(
            modifier = Modifier.testTag(TestTag.SendIntents),
            text = MR.strings.homeAssistantIntents.stable,
            isChecked = viewModel.isIntentHandlingHassIntent.collectAsState().value,
            onClick = viewModel::selectIntentHandlingHassIntent
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