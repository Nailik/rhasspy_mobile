package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.CardPaddingLevel1
import org.rhasspy.mobile.android.utils.RadioButtonListItem
import org.rhasspy.mobile.android.utils.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.android.utils.TextFieldListItemVisibility
import org.rhasspy.mobile.viewModels.configuration.IntentHandlingConfigurationViewModel


/**
 * content for intent handling configuration
 * drop down to select option
 * http configuration
 * home assistant configuration
 */
@Preview
@Composable
fun IntentHandlingConfigurationContent(viewModel: IntentHandlingConfigurationViewModel = viewModel()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreens.IntentHandlingConfiguration),
        title = MR.strings.intentHandling,
        hasUnsavedChanges = viewModel.hasUnsavedChanges,
        onSave = viewModel::save,
        onTest = viewModel::test,
        onDiscard = { }
    ) {

        //drop down to select option
        RadioButtonsEnumSelection(
            modifier = Modifier.testTag(TestTag.IntentHandlingOptions),
            selected = viewModel.intentHandlingOption.collectAsState().value,
            onSelect = viewModel::selectIntentHandlingOption,
            values = viewModel.intentHandlingOptionsList
        ) {
            if (viewModel.isRemoteHttpSettingsVisible(it)) {
                Card(
                    modifier = Modifier.padding(CardPaddingLevel1),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    //http endpoint
                    RemoteHTTPOption(viewModel)
                }
            }

            if (viewModel.isHomeAssistantSettingsVisible(it)) {
                Card(
                    modifier = Modifier.padding(CardPaddingLevel1),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
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

    //endpoint input field
    TextFieldListItem(
        modifier = Modifier.testTag(TestTag.Endpoint),
        value = viewModel.intentHandlingHttpEndpoint.collectAsState().value,
        onValueChange = viewModel::changeIntentHandlingHttpEndpoint,
        label = MR.strings.remoteURL
    )

}

/**
 * configuration of home assistant intent handling
 * url
 * access token
 * hass event or intent
 */
@Composable
private fun HomeAssistantOption(viewModel: IntentHandlingConfigurationViewModel) {

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