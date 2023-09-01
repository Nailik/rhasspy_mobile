package org.rhasspy.mobile.viewmodel.navigation

import androidx.compose.runtime.Stable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.data.connection.HttpConnection
import org.rhasspy.mobile.viewmodel.configuration.connections.http.detail.HttpConnectionDetailConfigurationViewModel

@Stable
sealed interface NavigationDestination {

    @Stable
    enum class MainScreenNavigationDestination : NavigationDestination {

        HomeScreen,
        DialogScreen,
        ConfigurationScreen,
        SettingsScreen,
        LogScreen

    }

    @Stable
    enum class ConfigurationScreenNavigationDestination : NavigationDestination {

        ConnectionsConfigurationScreen,
        DialogManagementConfigurationScreen,
        AudioInputConfigurationScreen,
        WakeWordConfigurationScreen,
        SpeechToTextConfigurationScreen,
        VoiceActivityDetectionConfigurationScreen,
        IntentRecognitionConfigurationScreen,
        IntentHandlingConfigurationScreen,
        TextToSpeechConfigurationScreen,
        AudioPlayingConfigurationScreen,

    }

    @Stable
    sealed interface ConnectionScreenNavigationDestination : NavigationDestination {

        data object MqttConnectionScreen : ConnectionScreenNavigationDestination
        data object HttpConnectionListScreen : ConnectionScreenNavigationDestination
        data class HttpConnectionDetailScreen(val id: HttpConnection?) : ConnectionScreenNavigationDestination, KoinComponent {

            val viewModel = get<HttpConnectionDetailConfigurationViewModel> { parametersOf(id) }

        }
        data object WebServerConnectionScreen : ConnectionScreenNavigationDestination

    }

    @Stable
    enum class SettingsScreenDestination : NavigationDestination {

        AboutSettings,
        AudioFocusSettings,
        BackgroundServiceSettings,
        DeviceSettings,
        IndicationSettings,
        AppearanceSettingsScreen,
        LogSettings,
        MicrophoneOverlaySettings,
        SaveAndRestoreSettings

    }

    @Stable
    enum class WakeWordConfigurationScreenDestination : NavigationDestination {

        EditPorcupineLanguageScreen,
        EditPorcupineWakeWordScreen,
        AudioRecorderFormatScreen,
        AudioOutputFormatScreen

    }

    @Stable
    enum class SpeechToTextConfigurationScreenDestination : NavigationDestination {

        AudioRecorderFormatScreen,
        AudioOutputFormatScreen,

    }

    @Stable
    enum class IndicationSettingsScreenDestination : NavigationDestination {

        WakeIndicationSoundScreen,
        RecordedIndicationSoundScreen,
        ErrorIndicationSoundScreen

    }

}