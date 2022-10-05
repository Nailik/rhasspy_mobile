package org.rhasspy.mobile.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.configuration.AudioPlayingConfigurationContent
import org.rhasspy.mobile.android.configuration.AudioPlayingConfigurationItem
import org.rhasspy.mobile.android.configuration.AudioRecordingConfigurationContent
import org.rhasspy.mobile.android.configuration.AudioRecordingConfigurationItem
import org.rhasspy.mobile.android.configuration.DialogManagementConfigurationContent
import org.rhasspy.mobile.android.configuration.DialogManagementConfigurationItem
import org.rhasspy.mobile.android.configuration.IntentHandlingConfigurationContent
import org.rhasspy.mobile.android.configuration.IntentHandlingConfigurationItem
import org.rhasspy.mobile.android.configuration.IntentRecognitionConfigurationContent
import org.rhasspy.mobile.android.configuration.IntentRecognitionConfigurationItem
import org.rhasspy.mobile.android.configuration.MqttConfigurationContent
import org.rhasspy.mobile.android.configuration.MqttConfigurationItem
import org.rhasspy.mobile.android.configuration.RemoteHermesHttpConfigurationContent
import org.rhasspy.mobile.android.configuration.RemoteHermesHttpConfigurationItem
import org.rhasspy.mobile.android.configuration.SpeechToTextConfigurationContent
import org.rhasspy.mobile.android.configuration.SpeechToTextConfigurationItem
import org.rhasspy.mobile.android.configuration.TextToSpeechConfigurationContent
import org.rhasspy.mobile.android.configuration.TextToSpeechConfigurationItem
import org.rhasspy.mobile.android.configuration.WakeWordConfigurationContent
import org.rhasspy.mobile.android.configuration.WakeWordConfigurationItem
import org.rhasspy.mobile.android.configuration.WebserverConfigurationContent
import org.rhasspy.mobile.android.configuration.WebserverConfigurationItem
import org.rhasspy.mobile.android.utils.CustomDivider
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel

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

@Composable
fun BottomSheet(viewModel: ConfigurationScreenViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        //Drag Handle
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 22.dp)
                .width(32.dp)
                .height(4.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant.copy(alpha = 0.5f)
                )
        )

        val screen by LocalModalBottomSheetScreen.current

        when (screen) {
            ConfigurationScreens.Default -> Box(Modifier.defaultMinSize(minHeight = 1.dp))
            ConfigurationScreens.Webserver -> WebserverConfigurationContent(viewModel)
            ConfigurationScreens.RemoteHermesHTTP -> RemoteHermesHttpConfigurationContent(viewModel)
            ConfigurationScreens.Mqtt -> MqttConfigurationContent(viewModel)
            ConfigurationScreens.AudioRecording -> AudioRecordingConfigurationContent(viewModel)
            ConfigurationScreens.WakeWord -> WakeWordConfigurationContent(viewModel)
            ConfigurationScreens.SpeechToText -> SpeechToTextConfigurationContent(viewModel)
            ConfigurationScreens.IntentRecognition -> IntentRecognitionConfigurationContent(viewModel)
            ConfigurationScreens.TextToSpeech -> TextToSpeechConfigurationContent(viewModel)
            ConfigurationScreens.AudioPlaying -> AudioPlayingConfigurationContent(viewModel)
            ConfigurationScreens.DialogueManagement -> DialogManagementConfigurationContent(viewModel)
            ConfigurationScreens.IntentHandling -> IntentHandlingConfigurationContent(viewModel)
        }
    }
}


@Composable
private fun SiteId(viewModel: ConfigurationScreenViewModel) {

    TextFieldListItem(
        value = viewModel.siteId.flow.collectAsState().value,
        onValueChange = viewModel.siteId::set,
        label = MR.strings.siteId,
        paddingValues = PaddingValues(top = 4.dp, bottom = 16.dp),
    )
}