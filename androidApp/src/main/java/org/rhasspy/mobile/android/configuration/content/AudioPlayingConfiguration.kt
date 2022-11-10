package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.settings.SettingsScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.ContentPaddingLevel1
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.viewModels.configuration.AudioPlayingConfigurationViewModel

/**
 * Content to configure audio playing
 * Drop Down of state
 * HTTP Endpoint
 */
@Preview(showBackground = true)
@Composable
fun AudioPlayingConfigurationContent(viewModel: AudioPlayingConfigurationViewModel = viewModel()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreens.AudioPlayingConfiguration),
        title = MR.strings.audioPlaying,
        hasUnsavedChanges = viewModel.hasUnsavedChanges,
        onSave = viewModel::save,
        onTest = viewModel::test,
        onDiscard = viewModel::discard
    ) { onNavigate ->

        val audioPlayingOption by viewModel.audioPlayingOption.collectAsState()

        //radio buttons list of available values
        RadioButtonsEnumSelection(
            modifier = Modifier.testTag(TestTag.AudioPlayingOptions),
            selected = audioPlayingOption,
            onSelect = viewModel::selectAudioPlayingOption,
            values = viewModel.audioPlayingOptionsList
        ) {
            if (viewModel.isAudioPlayingHttpEndpointSettingsVisible(it)) {
                HttpEndpointConfigurationContent(viewModel)
            }
        }


        //custom sounds
        ListElement(
            modifier = Modifier
                .clickable {
                    onNavigate(SettingsScreens.IndicationSettings.name)
                }
                .testTag(SettingsScreens.IndicationSettings),
            icon = { Icon(Icons.Filled.Info, contentDescription = MR.strings.info) },
            text = { Text(MR.strings.sounds) },
            secondaryText = { Text(MR.strings.soundsInfo) }
        )
    }

}

@Composable
fun HttpEndpointConfigurationContent(viewModel: AudioPlayingConfigurationViewModel) {

    //visibility of endpoint option
    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //switch to use custom
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.CustomEndpointSwitch),
            text = MR.strings.useCustomEndpoint,
            isChecked = viewModel.isUseCustomAudioPlayingHttpEndpoint.collectAsState().value,
            onCheckedChange = viewModel::toggleUseCustomHttpEndpoint
        )

        //http endpoint input field
        TextFieldListItem(
            enabled = viewModel.isAudioPlayingHttpEndpointChangeEnabled.collectAsState().value,
            modifier = Modifier.testTag(TestTag.Endpoint),
            value = viewModel.audioPlayingHttpEndpoint.collectAsState().value,
            onValueChange = viewModel::changeAudioPlayingHttpEndpoint,
            label = MR.strings.audioOutputURL
        )

    }

}