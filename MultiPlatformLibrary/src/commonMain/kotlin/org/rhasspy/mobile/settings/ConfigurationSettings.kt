package org.rhasspy.mobile.settings

import org.rhasspy.mobile.data.*
import org.rhasspy.mobile.settings.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.settings.serializer.PorcupineCustomKeywordSerializer
import org.rhasspy.mobile.settings.serializer.PorcupineDefaultKeywordSerializer

/**
 * used by di needs to be called after change to have an effect
 */
internal object ConfigurationSettings {

    val siteId = Setting(SettingsEnum.SiteId, "mobile")

    val isHttpServerEnabled = Setting(SettingsEnum.HttpServerEnabled, true)
    val httpServerPort = Setting(SettingsEnum.HttpServerPort, 12101)
    val isHttpServerSSLEnabled = Setting(SettingsEnum.HttpServerSSL, false)

    val isHttpSSLVerificationDisabled = Setting(SettingsEnum.SSLVerificationDisabled, true)
    val httpServerEndpoint = Setting(SettingsEnum.HttpServerEndpoint, "")

    val isMqttEnabled = Setting(SettingsEnum.MQTT_ENABLED, false)
    val mqttHost = Setting(SettingsEnum.MQTTHost, "")
    val mqttPort = Setting(SettingsEnum.MQTTPort, 1883)
    val mqttUserName = Setting(SettingsEnum.MQTTUserName, "")
    val mqttPassword = Setting(SettingsEnum.MQTTPassword, "")
    val isMqttSSLEnabled = Setting(SettingsEnum.MQTT_SSL, false)
    val mqttConnectionTimeout = Setting(SettingsEnum.MQTTConnectionTimeout, 5)
    val mqttKeepAliveInterval = Setting(SettingsEnum.MQTTKeepAliveInterval, 30)
    val mqttRetryInterval = Setting(SettingsEnum.MQTTRetryInterval, 10L)

    val udpOutputHost = Setting(SettingsEnum.UDPOutputHost, "")
    val udpOutputPort = Setting(SettingsEnum.UDPOutputPort, 12101)

    val wakeWordOption = Setting(SettingsEnum.WakeWordOption, WakeWordOption.Disabled)
    val wakeWordPorcupineAccessToken = Setting(SettingsEnum.WakeWordPorcupineAccessToken, "")
    val wakeWordPorcupineKeywordDefaultOptions = Setting(
        SettingsEnum.WakeWordPorcupineKeywordDefaultSelectedOptions,
        PorcupineKeywordOptions.values().map { PorcupineDefaultKeyword(it, false, 0.5f) }.toSet(),
        PorcupineDefaultKeywordSerializer
    )
    val wakeWordPorcupineKeywordCustomOptions = Setting(
        SettingsEnum.WakeWordPorcupineKeywordCustomOptions,
        setOf(),
        PorcupineCustomKeywordSerializer
    )
    val wakeWordPorcupineLanguage = Setting(SettingsEnum.WakeWordPorcupineLanguage, PorcupineLanguageOptions.EN)


    val dialogManagementOption = Setting(SettingsEnum.DialogManagementOption, DialogManagementOptions.Local)

    val intentRecognitionOption = Setting(SettingsEnum.IntentRecognitionOption, IntentRecognitionOptions.Disabled)
    val isUseCustomIntentRecognitionHttpEndpoint = Setting(SettingsEnum.CustomIntentRecognitionHttpEndpoint, false)
    val intentRecognitionHttpEndpoint = Setting(SettingsEnum.IntentRecognitionHttpEndpoint, "")

    val textToSpeechOption = Setting(SettingsEnum.TextToSpeechOption, TextToSpeechOptions.Disabled)
    val isUseCustomTextToSpeechHttpEndpoint = Setting(SettingsEnum.CustomTextToSpeechOptionHttpEndpoint, false)
    val textToSpeechHttpEndpoint = Setting(SettingsEnum.TextToSpeechHttpEndpoint, "")

    val audioPlayingOption = Setting(SettingsEnum.AudioPlayingOption, AudioPlayingOptions.Disabled)
    val audioOutputOption = Setting(SettingsEnum.AudioOutputOption, AudioOutputOptions.Sound)
    val isUseCustomAudioPlayingHttpEndpoint = Setting(SettingsEnum.CustomAudioPlayingHttpEndpoint, false)
    val audioPlayingHttpEndpoint = Setting(SettingsEnum.AudioPlayingHttpEndpoint, "")

    val speechToTextOption = Setting(SettingsEnum.SpeechToTextOption, SpeechToTextOptions.Disabled)
    val isUseCustomSpeechToTextHttpEndpoint = Setting(SettingsEnum.CustomSpeechToTextEndpoint, false)
    val speechToTextHttpEndpoint = Setting(SettingsEnum.SpeechToTextHttpEndpoint, "")

    val intentHandlingOption = Setting(SettingsEnum.IntentHandlingOption, IntentHandlingOptions.Disabled)
    val intentHandlingHttpEndpoint = Setting(SettingsEnum.IntentHandlingEndpoint, "")

    val intentHandlingHassEndpoint = Setting(SettingsEnum.IntentHandlingHassUrl, "")
    val intentHandlingHassAccessToken = Setting(SettingsEnum.IntentHandlingHassAccessToken, "")
    val intentHandlingHomeAssistantOption = Setting(SettingsEnum.IsIntentHandlingHassEvent, HomeAssistantIntentHandlingOptions.Event)

}
