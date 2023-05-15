package org.rhasspy.mobile.viewmodel.navigation

sealed interface Screen {
    object HomeScreen : Screen
    sealed interface ConfigurationScreen : Screen {

        object OverviewScreen: ConfigurationScreen
        sealed interface DetailScreen : ConfigurationScreen {

            sealed interface AudioPlayingConfigurationScreen: DetailScreen {

                object EditScreen: AudioPlayingConfigurationScreen

                object TestScreen: AudioPlayingConfigurationScreen

            }
            sealed interface DialogManagementConfigurationScreen: DetailScreen {

                object EditScreen: DialogManagementConfigurationScreen

                object TestScreen: DialogManagementConfigurationScreen

            }
            sealed interface IntentHandlingConfigurationScreen: DetailScreen {

                object EditScreen: IntentHandlingConfigurationScreen

                object TestScreen: IntentHandlingConfigurationScreen

            }
            sealed interface IntentRecognitionConfigurationScreen: DetailScreen {

                object EditScreen: IntentRecognitionConfigurationScreen

                object TestScreen: IntentRecognitionConfigurationScreen

            }
            sealed interface RemoteHermesHttpConfigurationScreen: DetailScreen {

                object EditScreen: RemoteHermesHttpConfigurationScreen

                object TestScreen: RemoteHermesHttpConfigurationScreen

            }
            sealed interface SpeechToTextConfigurationScreen: DetailScreen {

                object EditScreen: SpeechToTextConfigurationScreen

                object TestScreen: SpeechToTextConfigurationScreen

            }
            sealed interface TextToSpeechConfigurationScreen: DetailScreen {

                object EditScreen: TextToSpeechConfigurationScreen

                object TestScreen: TextToSpeechConfigurationScreen

            }
            sealed interface WakeWordConfigurationScreen: DetailScreen {

                sealed interface EditScreen: WakeWordConfigurationScreen {

                    object OverViewScreen: EditScreen

                    object PorcupineLanguageScreen: EditScreen

                    object PorcupineWakeWordScreen: EditScreen

                }

                object TestScreen: WakeWordConfigurationScreen

            }
            sealed interface WebServerConfigurationScreen: DetailScreen {

                object EditScreen: WebServerConfigurationScreen

                object TestScreen: WebServerConfigurationScreen

            }

        }

    }

    sealed interface SettingsScreen : Screen {

        object LanguageSettingsScreen : SettingsScreen

        object BackgroundServiceSettings : SettingsScreen

        object MicrophoneOverlaySettings : SettingsScreen

        object IndicationSettings : SettingsScreen

        object DeviceSettings : SettingsScreen

        object AudioRecorderSettings : SettingsScreen

        object AutomaticSilenceDetectionSettings : SettingsScreen

        object LogSettings : SettingsScreen

        object SaveAndRestoreSettings : SettingsScreen

        object AboutSettings : SettingsScreen

    }


    object LogScreen : Screen
}