package org.rhasspy.mobile.android.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.configuration.AudioPlayingConfigurationItem
import org.rhasspy.mobile.android.configuration.AudioRecordingConfigurationItem
import org.rhasspy.mobile.android.configuration.DialogManagementConfigurationItem
import org.rhasspy.mobile.android.configuration.IntentHandlingConfigurationItem
import org.rhasspy.mobile.android.configuration.IntentRecognitionConfigurationItem
import org.rhasspy.mobile.android.configuration.MqttConfigurationItem
import org.rhasspy.mobile.android.configuration.RemoteHermesHttpConfigurationItem
import org.rhasspy.mobile.android.configuration.SpeechToTextConfigurationItem
import org.rhasspy.mobile.android.configuration.TextToSpeechConfigurationItem
import org.rhasspy.mobile.android.configuration.WakeWordConfigurationItem
import org.rhasspy.mobile.android.configuration.WebserverConfigurationItem
import org.rhasspy.mobile.android.utils.CustomDivider
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel

/**
 * configuration screens with list items that open bottom sheet
 */
@Composable
fun ConfigurationScreen(viewModel: ConfigurationScreenViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SiteId(viewModel)
        CustomDivider()
        WebserverConfigurationItem(viewModel)
        CustomDivider()
        RemoteHermesHttpConfigurationItem(viewModel)
        CustomDivider()
        MqttConfigurationItem(viewModel)
        CustomDivider()
        AudioRecordingConfigurationItem(viewModel)
        CustomDivider()
        WakeWordConfigurationItem(viewModel)
        CustomDivider()
        SpeechToTextConfigurationItem(viewModel)
        CustomDivider()
        IntentRecognitionConfigurationItem(viewModel)
        CustomDivider()
        TextToSpeechConfigurationItem(viewModel)
        CustomDivider()
        AudioPlayingConfigurationItem(viewModel)
        CustomDivider()
        DialogManagementConfigurationItem(viewModel)
        CustomDivider()
        IntentHandlingConfigurationItem(viewModel)
        CustomDivider()
    }
}

/**
 * site id element
 */
@Composable
private fun SiteId(viewModel: ConfigurationScreenViewModel) {
    TextFieldListItem(
        value = viewModel.siteId.flow.collectAsState().value,
        onValueChange = viewModel.siteId::set,
        label = MR.strings.siteId,
        paddingValues = PaddingValues(top = 4.dp, bottom = 16.dp),
    )
}