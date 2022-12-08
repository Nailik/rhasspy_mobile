package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.getViewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.content.list.RadioButtonListItem
import org.rhasspy.mobile.android.content.list.TextFieldListItem
import org.rhasspy.mobile.android.content.list.TextFieldListItemVisibility
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewModels.configuration.IntentHandlingConfigurationViewModel


/**
 * content for intent handling configuration
 * drop down to select option
 * http configuration
 * home assistant configuration
 */
@Preview
@Composable
fun IntentHandlingConfigurationContent(viewModel: IntentHandlingConfigurationViewModel = getViewModel()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreens.IntentHandlingConfiguration),
        title = MR.strings.intentHandling,
        viewModel = viewModel,
        testContent = { TestContent(viewModel) }
    ) {

        //drop down to select option
        RadioButtonsEnumSelection(
            modifier = Modifier.testTag(TestTag.IntentHandlingOptions),
            selected = viewModel.intentHandlingOption.collectAsState().value,
            onSelect = viewModel::selectIntentHandlingOption,
            values = viewModel.intentHandlingOptionsList
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
            label = MR.strings.remoteURL
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
            label = MR.strings.hassURL
        )

        //hass access token
        TextFieldListItemVisibility(
            modifier = Modifier.testTag(TestTag.AccessToken),
            value = viewModel.intentHandlingHassAccessToken.collectAsState().value,
            onValueChange = viewModel::changeIntentHandlingHassAccessToken,
            label = MR.strings.accessToken
        )

        //select hass event or hass intent
        RadioButtonListItem(
            modifier = Modifier.testTag(TestTag.SendEvents),
            text = MR.strings.homeAssistantEvents,
            isChecked = viewModel.isIntentHandlingHassEvent.collectAsState().value,
            onClick = viewModel::selectIntentHandlingHassEvent
        )

        RadioButtonListItem(
            modifier = Modifier.testTag(TestTag.SendIntents),
            text = MR.strings.homeAssistantIntents,
            isChecked = viewModel.isIntentHandlingHassIntent.collectAsState().value,
            onClick = viewModel::selectIntentHandlingHassIntent
        )

    }

}

@Composable
private fun TestContent(
    viewModel: IntentHandlingConfigurationViewModel
) {
    Column {
        //textfield to insert intent json??
        //button to execute intent handling
    }
}