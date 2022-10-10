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

    val siteId = Setting(SettingsEnum.SiteId, "mobile")

    val isHttpServerEnabled = Setting(SettingsEnum.HttpServerEnabled, true)
    val httpServerPort = Setting(SettingsEnum.HttpServerPort, "12101")
    val isHttpServerSSLEnabled = Setting(SettingsEnum.HttpServerSSL, false)

    val isHttpSSLVerificationEnabled = Setting(SettingsEnum.SSLVerificationDisabled, false)

    val isMqttEnabled = Setting(SettingsEnum.MQTT_ENABLED, false)
    val mqttHost = Setting(SettingsEnum.MQTTHost, "")
    val mqttPort = Setting(SettingsEnum.MQTTPort, "")
    val mqttUserName = Setting(SettingsEnum.MQTTUserName, "")
    val mqttPassword = Setting(SettingsEnum.MQTTPassword, "")
    val isMqttSSLEnabled = Setting(SettingsEnum.MQTT_SSL, false)
    val mqttConnectionTimeout = Setting(SettingsEnum.MQTTConnectionTimeout, "5")
    val mqttKeepAliveInterval = Setting(SettingsEnum.MQTTKeepAliveInterval, "30")
    val mqttRetryInterval = Setting(SettingsEnum.MQTTRetryInterval, "10")

    val isUdpOutputEnabled = Setting(SettingsEnum.UDPOutput, false)
    val udpOutputHost = Setting(SettingsEnum.UDPOutputHost, "")
    val udpOutputPort = Setting(SettingsEnum.UDPOutputPort, "")

    val wakeWordOption = Setting(SettingsEnum.WakeWordOption, WakeWordOption.Disabled)
    val wakeWordPorcupineAccessToken = Setting(SettingsEnum.WakeWordPorcupineAccessToken, "")
    val wakeWordPorcupineKeywordOption = Setting(SettingsEnum.WakeWordPorcupineKeywordOption, 0)
    val wakeWordPorcupineKeywordOptions =
        Setting(SettingsEnum.WakeWordPorcupineKeywordOptions, WakeWordKeywordOption.values().map { it.name }.toSet())
    val wakeWordPorcupineLanguage = Setting(SettingsEnum.WakeWordPorcupineLanguage, PorcupineLanguageOptions.EN)
    val wakeWordPorcupineKeywordSensitivity = Setting(SettingsEnum.WakeWordPorcupineKeywordSensitivity, 0.5f)

    val speechToTextOption = Setting(SettingsEnum.SpeechToTextOption, SpeechToTextOptions.Disabled)
    val speechToTextHttpEndpoint = Setting(SettingsEnum.SpeechToTextHttpEndpoint, "")

    val intentRecognitionOption = Setting(SettingsEnum.IntentRecognitionOption, IntentRecognitionOptions.Disabled)
    val intentRecognitionEndpoint = Setting(SettingsEnum.IntentRecognitionEndpoint, "")

    val textToSpeechOption = Setting(SettingsEnum.TextToSpeechOption, TextToSpeechOptions.Disabled)
    val textToSpeechEndpoint = Setting(SettingsEnum.TextToSpeechEndpoint, "")

    val audioPlayingOption = Setting(SettingsEnum.AudioPlayingOption, AudioPlayingOptions.Disabled)
    val audioPlayingEndpoint = Setting(SettingsEnum.AudioPlayingEndpoint, "")

    val dialogueManagementOption = Setting(SettingsEnum.DialogueManagementOption, DialogueManagementOptions.Local)

    val intentHandlingOption = Setting(SettingsEnum.IntentHandlingOption, IntentHandlingOptions.Disabled)
    val intentHandlingEndpoint = Setting(SettingsEnum.IntentHandlingEndpoint, "")
    val intentHandlingHassUrl = Setting(SettingsEnum.IntentHandlingHassUrl, "")
    val intentHandlingHassAccessToken = Setting(SettingsEnum.IntentHandlingHassAccessToken, "")
    val isIntentHandlingHassEvent = Setting(SettingsEnum.IsIntentHandlingHassEvent, false)

}
