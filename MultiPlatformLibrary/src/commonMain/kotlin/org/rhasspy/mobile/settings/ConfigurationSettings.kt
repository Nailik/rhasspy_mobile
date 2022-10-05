package org.rhasspy.mobile.settings

import org.rhasspy.mobile.data.AudioPlayingOptions
import org.rhasspy.mobile.data.DialogueManagementOptions
import org.rhasspy.mobile.data.IntentHandlingOptions
import org.rhasspy.mobile.data.IntentRecognitionOptions
import org.rhasspy.mobile.data.PorcupineLanguageOptions
import org.rhasspy.mobile.data.SpeechToTextOptions
import org.rhasspy.mobile.data.TextToSpeechOptions
import org.rhasspy.mobile.data.WakeWordKeywordOption
import org.rhasspy.mobile.data.WakeWordOption

internal object ConfigurationSettings {

    val siteId = ConfigurationSetting(SettingsEnum.SiteId, "mobile")

    val isHttpServerEnabled = ConfigurationSetting(SettingsEnum.HttpServerEnabled, true)
    val httpServerPort = ConfigurationSetting(SettingsEnum.HttpServerPort, "12101")
    val isHttpServerSSLEnabled = ConfigurationSetting(SettingsEnum.HttpServerSSL, false)

    val isHttpSSLVerificationEnabled = ConfigurationSetting(SettingsEnum.SSLVerificationDisabled, false)

    val isMqttEnabled = ConfigurationSetting(SettingsEnum.MQTT_ENABLED, false)
    val mqttHost = ConfigurationSetting(SettingsEnum.MQTTHost, "")
    val mqttPort = ConfigurationSetting(SettingsEnum.MQTTPort, "")
    val mqttUserName = ConfigurationSetting(SettingsEnum.MQTTUserName, "")
    val mqttPassword = ConfigurationSetting(SettingsEnum.MQTTPassword, "")
    val isMqttSSLEnabled = ConfigurationSetting(SettingsEnum.MQTT_SSL, false)
    val mqttConnectionTimeout = ConfigurationSetting(SettingsEnum.MQTTConnectionTimeout, "5")
    val mqttKeepAliveInterval = ConfigurationSetting(SettingsEnum.MQTTKeepAliveInterval, "30")
    val mqttRetryInterval = ConfigurationSetting(SettingsEnum.MQTTRetryInterval, "10")

    val isUdpOutputEnabled = ConfigurationSetting(SettingsEnum.UDPOutput, false)
    val udpOutputHost = ConfigurationSetting(SettingsEnum.UDPOutputHost, "")
    val udpOutputPort = ConfigurationSetting(SettingsEnum.UDPOutputPort, "")

    val wakeWordOption = ConfigurationSetting(SettingsEnum.WakeWordOption, WakeWordOption.Disabled)
    val wakeWordPorcupineAccessToken = ConfigurationSetting(SettingsEnum.WakeWordPorcupineAccessToken, "")
    val wakeWordPorcupineKeywordOption = ConfigurationSetting(SettingsEnum.WakeWordPorcupineKeywordOption, 0)
    val wakeWordPorcupineKeywordOptions =
        ConfigurationSetting(SettingsEnum.WakeWordPorcupineKeywordOptions, WakeWordKeywordOption.values().map { it.name }.toSet())
    val wakeWordPorcupineLanguage = ConfigurationSetting(SettingsEnum.WakeWordPorcupineLanguage, PorcupineLanguageOptions.EN)
    val wakeWordPorcupineKeywordSensitivity = ConfigurationSetting(SettingsEnum.WakeWordPorcupineKeywordSensitivity, 0.5f)

    val speechToTextOption = ConfigurationSetting(SettingsEnum.SpeechToTextOption, SpeechToTextOptions.Disabled)
    val speechToTextHttpEndpoint = ConfigurationSetting(SettingsEnum.SpeechToTextHttpEndpoint, "")

    val intentRecognitionOption = ConfigurationSetting(SettingsEnum.IntentRecognitionOption, IntentRecognitionOptions.Disabled)
    val intentRecognitionEndpoint = ConfigurationSetting(SettingsEnum.IntentRecognitionEndpoint, "")

    val textToSpeechOption = ConfigurationSetting(SettingsEnum.TextToSpeechOption, TextToSpeechOptions.Disabled)
    val textToSpeechEndpoint = ConfigurationSetting(SettingsEnum.TextToSpeechEndpoint, "")

    val audioPlayingOption = ConfigurationSetting(SettingsEnum.AudioPlayingOption, AudioPlayingOptions.Disabled)
    val audioPlayingEndpoint = ConfigurationSetting(SettingsEnum.AudioPlayingEndpoint, "")

    val dialogueManagementOption = ConfigurationSetting(SettingsEnum.DialogueManagementOption, DialogueManagementOptions.Local)

    val intentHandlingOption = ConfigurationSetting(SettingsEnum.IntentHandlingOption, IntentHandlingOptions.Disabled)
    val intentHandlingEndpoint = ConfigurationSetting(SettingsEnum.IntentHandlingEndpoint, "")
    val intentHandlingHassUrl = ConfigurationSetting(SettingsEnum.IntentHandlingHassUrl, "")
    val intentHandlingHassAccessToken = ConfigurationSetting(SettingsEnum.IntentHandlingHassAccessToken, "")
    val isIntentHandlingHassEvent = ConfigurationSetting(SettingsEnum.IsIntentHandlingHassEvent, false)

}
