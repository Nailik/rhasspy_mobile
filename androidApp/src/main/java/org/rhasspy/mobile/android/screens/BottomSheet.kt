package org.rhasspy.mobile.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.android.configuration.AudioPlayingConfigurationContent
import org.rhasspy.mobile.android.configuration.AudioRecordingConfigurationContent
import org.rhasspy.mobile.android.configuration.DialogManagementConfigurationContent
import org.rhasspy.mobile.android.configuration.IntentHandlingConfigurationContent
import org.rhasspy.mobile.android.configuration.IntentRecognitionConfigurationContent
import org.rhasspy.mobile.android.configuration.MqttConfigurationContent
import org.rhasspy.mobile.android.configuration.RemoteHermesHttpConfigurationContent
import org.rhasspy.mobile.android.configuration.SpeechToTextConfigurationContent
import org.rhasspy.mobile.android.configuration.TextToSpeechConfigurationContent
import org.rhasspy.mobile.android.configuration.WakeWordConfigurationContent
import org.rhasspy.mobile.android.configuration.WebserverConfigurationContent
import org.rhasspy.mobile.android.screens.navigation.LocalModalBottomSheetScreen
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel
import org.rhasspy.mobile.viewModels.SettingsScreenViewModel

/**
 * Configuration settings
 */
enum class BottomSheetScreens {
    Default,
    Webserver,
    RemoteHermesHTTP,
    Mqtt,
    AudioRecording,
    WakeWord,
    SpeechToText,
    IntentRecognition,
    TextToSpeech,
    AudioPlaying,
    DialogueManagement,
    IntentHandling
}

/**
 * bottom sheet content depending on latest element
 */
@Composable
fun BottomSheet(configurationViewModel: ConfigurationScreenViewModel = viewModel(), settingsViewModel: SettingsScreenViewModel = viewModel()) {
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
            BottomSheetScreens.Default -> Box(Modifier.defaultMinSize(minHeight = 1.dp))
            BottomSheetScreens.Webserver -> WebserverConfigurationContent(configurationViewModel)
            BottomSheetScreens.RemoteHermesHTTP -> RemoteHermesHttpConfigurationContent(configurationViewModel)
            BottomSheetScreens.Mqtt -> MqttConfigurationContent(configurationViewModel)
            BottomSheetScreens.AudioRecording -> AudioRecordingConfigurationContent(configurationViewModel)
            BottomSheetScreens.WakeWord -> WakeWordConfigurationContent(configurationViewModel)
            BottomSheetScreens.SpeechToText -> SpeechToTextConfigurationContent(configurationViewModel)
            BottomSheetScreens.IntentRecognition -> IntentRecognitionConfigurationContent(configurationViewModel)
            BottomSheetScreens.TextToSpeech -> TextToSpeechConfigurationContent(configurationViewModel)
            BottomSheetScreens.AudioPlaying -> AudioPlayingConfigurationContent(configurationViewModel)
            BottomSheetScreens.DialogueManagement -> DialogManagementConfigurationContent(configurationViewModel)
            BottomSheetScreens.IntentHandling -> IntentHandlingConfigurationContent(configurationViewModel)
        }
    }
}
