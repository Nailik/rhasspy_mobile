package org.rhasspy.mobile.viewmodel.navigation

import androidx.compose.runtime.Stable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.viewmodel.bottomnavigation.BottomNavigationViewModel
import org.rhasspy.mobile.viewmodel.configuration.asr.AsrConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.mic.MicDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.mic.audioinputformat.AudioRecorderFormatConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.mic.audiooutputformat.AudioOutputFormatConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.vad.VadDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import org.rhasspy.mobile.viewmodel.screen.IScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenViewModel
import org.rhasspy.mobile.viewmodel.settings.appearance.AppearanceSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.audiofocus.AudioFocusSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.ErrorIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.RecordedIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.WakeIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsViewModel

@Stable
sealed class NavigationDestination : KoinComponent {

    @Stable
    sealed class MainScreenNavigationDestination : NavigationDestination() {

        data object HomeScreen : MainScreenNavigationDestination() {
            override val viewModel get() = get<HomeScreenViewModel> { parametersOf(this) }
        }

        data object DialogScreen : MainScreenNavigationDestination() {
            override val viewModel get() = get<DialogScreenViewModel> { parametersOf(this) }
        }

        data object ConfigurationScreen : MainScreenNavigationDestination() {
            override val viewModel get() = get<ConfigurationScreenViewModel> { parametersOf(this) }
        }

        data object SettingsScreen : MainScreenNavigationDestination() {
            override val viewModel get() = get<SettingsScreenViewModel> { parametersOf(this) }
        }

        data object LogScreen : MainScreenNavigationDestination() {
            override val viewModel get() = get<LogScreenViewModel> { parametersOf(this) }
        }

        val bottomViewModel get() = get<BottomNavigationViewModel> { parametersOf(this) }

    }

    @Stable
    sealed class ConfigurationScreenNavigationDestination : NavigationDestination() {

        data object ConnectionsConfigurationScreen : ConfigurationScreenNavigationDestination() {
            override val viewModel get() = get<ConnectionsConfigurationViewModel> { parametersOf(this) }
        }

        data object DialogManagementConfigurationScreen : ConfigurationScreenNavigationDestination() {
            override val viewModel get() = get<DialogManagementConfigurationViewModel> { parametersOf(this) }
        }

        data object AudioInputConfigurationScreen : ConfigurationScreenNavigationDestination() {
            override val viewModel get() = get<MicDomainConfigurationViewModel> { parametersOf(this) }
        }

        data object WakeWordConfigurationScreen : ConfigurationScreenNavigationDestination() {
            override val viewModel get() = get<WakeWordConfigurationViewModel> { parametersOf(this) }
        }

        data object SpeechToTextConfigurationScreen : ConfigurationScreenNavigationDestination() {
            override val viewModel get() = get<AsrConfigurationViewModel> { parametersOf(this) }
        }

        data object VoiceActivityDetectionConfigurationScreen : ConfigurationScreenNavigationDestination() {
            override val viewModel get() = get<VadDomainConfigurationViewModel> { parametersOf(this) }
        }

        data object IntentRecognitionConfigurationScreen : ConfigurationScreenNavigationDestination() {
            override val viewModel get() = get<IntentRecognitionConfigurationViewModel> { parametersOf(this) }
        }

        data object IntentHandlingConfigurationScreen : ConfigurationScreenNavigationDestination() {
            override val viewModel get() = get<IntentHandlingConfigurationViewModel> { parametersOf(this) }
        }

        data object TextToSpeechConfigurationScreen : ConfigurationScreenNavigationDestination() {
            override val viewModel get() = get<TextToSpeechConfigurationViewModel> { parametersOf(this) }
        }

        data object AudioPlayingConfigurationScreen : ConfigurationScreenNavigationDestination() {
            override val viewModel get() = get<AudioPlayingConfigurationViewModel> { parametersOf(this) }
        }

    }

    @Stable
    sealed class ConnectionScreenNavigationDestination : NavigationDestination() {

        data object Rhasspy2HermesConnectionScreen : ConnectionScreenNavigationDestination() {
            override val viewModel get() = get<Rhasspy2HermesConnectionConfigurationViewModel> { parametersOf(this) }
        }

        data object Rhasspy3WyomingConnectionScreen : ConnectionScreenNavigationDestination() {
            override val viewModel get() = get<Rhasspy3WyomingConnectionConfigurationViewModel> { parametersOf(this) }
        }

        data object HomeAssistantConnectionScreen : ConnectionScreenNavigationDestination() {
            override val viewModel get() = get<HomeAssistantConnectionConfigurationViewModel> { parametersOf(this) }
        }

        data object MqttConnectionScreen : ConnectionScreenNavigationDestination() {
            override val viewModel get() = get<MqttConnectionConfigurationViewModel> { parametersOf(this) }
        }

        data object WebServerConnectionScreen : ConnectionScreenNavigationDestination() {
            override val viewModel get() = get<WebServerConnectionConfigurationViewModel> { parametersOf(this) }
        }

    }

    @Stable
    sealed class SettingsScreenDestination : NavigationDestination() {

        data object AboutSettings : SettingsScreenDestination() {
            override val viewModel get() = get<AboutScreenViewModel> { parametersOf(this) }
        }

        data object AudioFocusSettings : SettingsScreenDestination() {
            override val viewModel get() = get<AudioFocusSettingsViewModel> { parametersOf(this) }
        }

        data object BackgroundServiceSettings : SettingsScreenDestination() {
            override val viewModel get() = get<BackgroundServiceSettingsViewModel> { parametersOf(this) }
        }

        data object DeviceSettings : SettingsScreenDestination() {
            override val viewModel get() = get<DeviceSettingsViewModel> { parametersOf(this) }
        }

        data object IndicationSettings : SettingsScreenDestination() {
            override val viewModel get() = get<IndicationSettingsViewModel> { parametersOf(this) }
        }

        data object AppearanceSettingsScreen : SettingsScreenDestination() {
            override val viewModel get() = get<AppearanceSettingsViewModel> { parametersOf(this) }
        }

        data object LogSettings : SettingsScreenDestination() {
            override val viewModel get() = get<LogSettingsViewModel> { parametersOf(this) }
        }

        data object MicrophoneOverlaySettings : SettingsScreenDestination() {
            override val viewModel get() = get<MicrophoneOverlaySettingsViewModel> { parametersOf(this) }
        }

        data object SaveAndRestoreSettings : SettingsScreenDestination() {
            override val viewModel get() = get<SaveAndRestoreSettingsViewModel> { parametersOf(this) }
        }

    }

    @Stable
    sealed class WakeWordConfigurationScreenDestination : NavigationDestination() {

        data object EditPorcupineLanguageScreen : WakeWordConfigurationScreenDestination() {
            override val viewModel get() = get<WakeWordConfigurationViewModel> { parametersOf(this) }
        }

        data object EditPorcupineWakeWordScreen : WakeWordConfigurationScreenDestination() {
            override val viewModel get() = get<WakeWordConfigurationViewModel> { parametersOf(this) }
        }

    }

    @Stable
    sealed class IndicationSettingsScreenDestination : NavigationDestination() {

        data object WakeIndicationSoundScreen : IndicationSettingsScreenDestination() {
            override val viewModel get() = get<WakeIndicationSoundSettingsViewModel> { parametersOf(this) }
        }

        data object RecordedIndicationSoundScreen : IndicationSettingsScreenDestination() {
            override val viewModel get() = get<RecordedIndicationSoundSettingsViewModel> { parametersOf(this) }
        }

        data object ErrorIndicationSoundScreen : IndicationSettingsScreenDestination() {
            override val viewModel get() = get<ErrorIndicationSoundSettingsViewModel> { parametersOf(this) }
        }

    }

    @Stable
    sealed class AudioInputDomainScreenDestination : NavigationDestination() {

        data object AudioInputFormatScreen : AudioInputDomainScreenDestination() {
            override val viewModel get() = get<AudioRecorderFormatConfigurationViewModel> { parametersOf(this) }
        }

        data object AudioOutputFormatScreen : AudioInputDomainScreenDestination() {
            override val viewModel get() = get<AudioOutputFormatConfigurationViewModel> { parametersOf(this) }
        }
    }

    abstract val viewModel: IScreenViewModel


}