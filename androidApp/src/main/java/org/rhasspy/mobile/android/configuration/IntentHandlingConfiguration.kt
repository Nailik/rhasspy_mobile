package org.rhasspy.mobile.android.configuration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.screens.BottomSheetScreens
import org.rhasspy.mobile.android.utils.ConfigurationListContent
import org.rhasspy.mobile.android.utils.ConfigurationListItem
import org.rhasspy.mobile.android.utils.DropDownEnumListItem
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.RadioButtonListItem
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.data.IntentHandlingOptions
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel

/**
 * List element for intent handling configuration
 * shows which option is selected
 */
@Composable
fun IntentHandlingConfigurationItem(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.intentHandling,
        secondaryText = viewModel.intentHandlingOption.flow.collectAsState().value.text,
        screen = BottomSheetScreens.IntentHandling
    )

}

/**
 * content for intent handling configuration
 * drop down to select option
 * http configuration
 * home assistant configuration
 */
@Composable
fun IntentHandlingConfigurationContent(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListContent(MR.strings.intentHandling) {

        DropDownEnumListItem(
            selected = viewModel.intentHandlingOption.flow.collectAsState().value,
            onSelect = viewModel.intentHandlingOption::set,
            values = IntentHandlingOptions::values
        )

        RemoteHTTPOption(viewModel)

        HomeAssistantOption(viewModel)

    }

}

/**
 * http configuration for intent handling
 * field to set endpoint
 */
@Composable
private fun RemoteHTTPOption(viewModel: ConfigurationScreenViewModel) {
    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = viewModel.intentHandlingOption.flow.collectAsState().value == IntentHandlingOptions.RemoteHTTP
    ) {

        TextFieldListItem(
            value = viewModel.intentHandlingEndpoint.flow.collectAsState().value,
            onValueChange = viewModel.intentHandlingEndpoint::set,
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
private fun HomeAssistantOption(viewModel: ConfigurationScreenViewModel) {
    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = viewModel.intentHandlingOption.flow.collectAsState().value == IntentHandlingOptions.HomeAssistant
    ) {

        Column {

            TextFieldListItem(
                value = viewModel.intentHandlingHassUrl.flow.collectAsState().value,
                onValueChange = viewModel.intentHandlingHassUrl::set,
                label = MR.strings.hassURL
            )

            var isShowAccessToken by rememberSaveable { mutableStateOf(false) }

            TextFieldListItem(
                value = viewModel.intentHandlingHassAccessToken.flow.collectAsState().value,
                onValueChange = viewModel.intentHandlingHassAccessToken::set,
                label = MR.strings.accessToken,
                visualTransformation = if (isShowAccessToken) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isShowAccessToken = !isShowAccessToken }) {
                        Icon(
                            if (isShowAccessToken) {
                                Icons.Filled.Visibility
                            } else {
                                Icons.Filled.VisibilityOff
                            },
                            contentDescription = MR.strings.visibility,
                        )
                    }
                },
            )

            val isIntentHandlingHassEvent by viewModel.isIntentHandlingHassEvent.flow.collectAsState()

            RadioButtonListItem(
                text = MR.strings.homeAssistantEvents,
                isChecked = isIntentHandlingHassEvent,
                onClick = { viewModel.isIntentHandlingHassEvent.set(true) })

            RadioButtonListItem(
                text = MR.strings.homeAssistantIntents,
                isChecked = !isIntentHandlingHassEvent,
                onClick = { viewModel.isIntentHandlingHassEvent.set(false) })
        }
    }
}