package org.rhasspy.mobile.viewmodel.navigation

import org.rhasspy.mobile.viewmodel.navigation.Screen.ConfigurationScreen.ConfigurationDetailScreen.ConfigurationDetailScreenType.Edit
import org.rhasspy.mobile.viewmodel.navigation.Screen.ConfigurationScreen.ConfigurationDetailScreen.ConfigurationDetailScreenType.Test

sealed interface Screen {
    object HomeScreen : Screen
    sealed interface ConfigurationScreen : Screen {

        object OverviewScreen: ConfigurationScreen

        sealed class ConfigurationDetailScreen(val type: ConfigurationDetailScreenType) : ConfigurationScreen {

            enum class ConfigurationDetailScreenType {
                Edit,
                Test
            }

            sealed class AudioPlayingConfigurationScreen(type: ConfigurationDetailScreenType): ConfigurationDetailScreen(type) {

                object EditScreen: AudioPlayingConfigurationScreen(Edit)

                object TestScreen: AudioPlayingConfigurationScreen(Test)

            }
            sealed class DialogManagementConfigurationScreen(type: ConfigurationDetailScreenType): ConfigurationDetailScreen(type) {

                object EditScreen: DialogManagementConfigurationScreen(Edit)

                object TestScreen: DialogManagementConfigurationScreen(Test)

            }
            sealed class IntentHandlingConfigurationScreen(type: ConfigurationDetailScreenType): ConfigurationDetailScreen(type) {

                object EditScreen: IntentHandlingConfigurationScreen(Edit)

                object TestScreen: IntentHandlingConfigurationScreen(Test)

            }
            sealed class IntentRecognitionConfigurationScreen(type: ConfigurationDetailScreenType): ConfigurationDetailScreen(type) {

                object EditScreen: IntentRecognitionConfigurationScreen(Edit)

                object TestScreen: IntentRecognitionConfigurationScreen(Test)

            }
            sealed class MqttConfigurationScreen(type: ConfigurationDetailScreenType): ConfigurationDetailScreen(type) {

                object EditScreen: MqttConfigurationScreen(Edit)

                object TestScreen: MqttConfigurationScreen(Test)

            }
            sealed class RemoteHermesHttpConfigurationScreen(type: ConfigurationDetailScreenType): ConfigurationDetailScreen(type) {

                object EditScreen: RemoteHermesHttpConfigurationScreen(Edit)

                object TestScreen: RemoteHermesHttpConfigurationScreen(Test)

            }
            sealed class SpeechToTextConfigurationScreen(type: ConfigurationDetailScreenType): ConfigurationDetailScreen(type) {

                object EditScreen: SpeechToTextConfigurationScreen(Edit)

                object TestScreen: SpeechToTextConfigurationScreen(Test)

            }
            sealed class TextToSpeechConfigurationScreen(type: ConfigurationDetailScreenType): ConfigurationDetailScreen(type) {

                object EditScreen: TextToSpeechConfigurationScreen(Edit)

                object TestScreen: TextToSpeechConfigurationScreen(Test)

            }
            sealed class WakeWordConfigurationScreen(type: ConfigurationDetailScreenType): ConfigurationDetailScreen(type) {

                sealed class EditScreen: WakeWordConfigurationScreen(Edit) {

                    object OverViewScreen: EditScreen()

                    object PorcupineLanguageScreen: EditScreen()

                    object PorcupineWakeWordScreen: EditScreen()

                }

                object TestScreen: WakeWordConfigurationScreen(Test)

            }
            sealed class WebServerConfigurationScreen(type: ConfigurationDetailScreenType): ConfigurationDetailScreen(type) {

                object EditScreen: WebServerConfigurationScreen(Edit)

                object TestScreen: WebServerConfigurationScreen(Test)

            }

        }

    }

    sealed interface SettingsScreen : Screen {

        object OverviewScreen: SettingsScreen

        object AboutSettings : SettingsScreen
        object AudioFocusSettings : SettingsScreen
        object AudioRecorderSettings : SettingsScreen
        object AutomaticSilenceDetectionSettings : SettingsScreen
        object BackgroundServiceSettings : SettingsScreen
        object DeviceSettings : SettingsScreen
        sealed interface IndicationSettings : SettingsScreen {

            object Overview: IndicationSettings
            object WakeIndicationSound: IndicationSettings
            object RecordedIndicationSound: IndicationSettings
            object ErrorIndicationSound: IndicationSettings

        }
        object LanguageSettingsScreen : SettingsScreen
        object LogSettings : SettingsScreen
        object MicrophoneOverlaySettings : SettingsScreen
        object SaveAndRestoreSettings : SettingsScreen



    }


    object LogScreen : Screen
}