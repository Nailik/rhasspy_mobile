package org.rhasspy.mobile.settings

import org.rhasspy.mobile.settings.option.*
import org.rhasspy.mobile.settings.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.settings.serializer.PorcupineCustomKeywordSerializer
import org.rhasspy.mobile.settings.serializer.PorcupineDefaultKeywordSerializer

/**
 * used by di needs to be called after change to have an effect
 */
internal object ConfigurationSetting {

    val siteId = ISetting(SettingsEnum.SiteId, "mobile")

    val isHttpServerEnabled = ISetting(SettingsEnum.HttpServerEnabled, true)
    val httpServerPort = ISetting(SettingsEnum.HttpServerPort, 12101)
    val isHttpServerSSLEnabledEnabled = ISetting(SettingsEnum.HttpServerSSLEnabled, false)
    val httpServerSSLKeyStoreFile = ISetting(SettingsEnum.HttpServerSSLKeyStoreFile, "")
    val httpServerSSLKeyStorePassword = ISetting(SettingsEnum.HttpServerSSLKeyStorePassword, "")
    val httpServerSSLKeyAlias = ISetting(SettingsEnum.HttpServerSSLKeyAlias, "")
    val httpServerSSLKeyPassword = ISetting(SettingsEnum.HttpServerSSLKeyPassword, "")

    val isHttpClientSSLVerificationDisabled = ISetting(SettingsEnum.SSLVerificationDisabled, true)
    val httpClientServerEndpointHost = ISetting(SettingsEnum.HttpClientServerEndpointHost, "")
    val httpClientServerEndpointPort = ISetting(SettingsEnum.HttpClientServerEndpointPort, 12101)
    val httpClientTimeout = ISetting<Long?>(SettingsEnum.HttpClientTimeout, 30000L)

    val isMqttEnabled = ISetting(SettingsEnum.MQTTEnabled, false)
    val mqttHost = ISetting(SettingsEnum.MQTTHost, "")
    val mqttPort = ISetting(SettingsEnum.MQTTPort, 1883)
    val mqttUserName = ISetting(SettingsEnum.MQTTUserName, "")
    val mqttPassword = ISetting(SettingsEnum.MQTTPassword, "")
    val isMqttSSLEnabled = ISetting(SettingsEnum.MQTTSSLEnabled, false)
    val mqttConnectionTimeout = ISetting(SettingsEnum.MQTTConnectionTimeout, 5)
    val mqttKeepAliveInterval = ISetting(SettingsEnum.MQTTKeepAliveInterval, 30)
    val mqttRetryInterval = ISetting(SettingsEnum.MQTTRetryInterval, 10L)
    val mqttKeyStoreFile = ISetting(SettingsEnum.MQTTKeyStoreFile, "")

    val wakeWordOption = ISetting(SettingsEnum.WakeWordOption, WakeWordOption.Disabled)
    val wakeWordPorcupineAccessToken = ISetting(SettingsEnum.WakeWordPorcupineAccessToken, "")
    val wakeWordPorcupineKeywordDefaultOptions = ISetting(
        SettingsEnum.WakeWordPorcupineKeywordDefaultSelectedOptions,
        PorcupineKeywordOption.values().map { PorcupineDefaultKeyword(it, false, 0.5f) }.toSet(),
        PorcupineDefaultKeywordSerializer
    )
    val wakeWordPorcupineKeywordCustomOptions = ISetting(
        SettingsEnum.WakeWordPorcupineKeywordCustomOptions,
        setOf(),
        PorcupineCustomKeywordSerializer
    )
    val wakeWordPorcupineLanguage =
        ISetting(SettingsEnum.WakeWordPorcupineLanguage, PorcupineLanguageOption.EN)
    val wakeWordUdpOutputHost = ISetting(SettingsEnum.WakeWordUDPOutputHost, "")
    val wakeWordUdpOutputPort = ISetting(SettingsEnum.WakeWordUDPOutputPort, 12333)


    val dialogManagementOption =
        ISetting(SettingsEnum.DialogManagementOption, DialogManagementOption.Local)

    val intentRecognitionOption =
        ISetting(SettingsEnum.IntentRecognitionOption, IntentRecognitionOption.Disabled)
    val isUseCustomIntentRecognitionHttpEndpoint =
        ISetting(SettingsEnum.CustomIntentRecognitionHttpEndpoint, false)
    val intentRecognitionHttpEndpoint = ISetting(SettingsEnum.IntentRecognitionHttpEndpoint, "")

    val textToSpeechOption = ISetting(SettingsEnum.TextToSpeechOption, TextToSpeechOption.Disabled)
    val isUseCustomTextToSpeechHttpEndpoint =
        ISetting(SettingsEnum.CustomTextToSpeechOptionHttpEndpoint, false)
    val textToSpeechHttpEndpoint = ISetting(SettingsEnum.TextToSpeechHttpEndpoint, "")

    val audioPlayingOption = ISetting(SettingsEnum.AudioPlayingOption, AudioPlayingOption.Local)
    val audioOutputOption = ISetting(SettingsEnum.AudioOutputOption, AudioOutputOption.Sound)
    val isUseCustomAudioPlayingHttpEndpoint =
        ISetting(SettingsEnum.CustomAudioPlayingHttpEndpoint, false)
    val audioPlayingHttpEndpoint = ISetting(SettingsEnum.AudioPlayingHttpEndpoint, "")

    val speechToTextOption = ISetting(SettingsEnum.SpeechToTextOption, SpeechToTextOption.Disabled)
    val isUseCustomSpeechToTextHttpEndpoint =
        ISetting(SettingsEnum.CustomSpeechToTextEndpoint, false)
    val isUseSpeechToTextMqttSilenceDetection =
        ISetting(SettingsEnum.SpeechToTextMqttSilenceDetection, true)
    val speechToTextHttpEndpoint = ISetting(SettingsEnum.SpeechToTextHttpEndpoint, "")

    val intentHandlingOption =
        ISetting(SettingsEnum.IntentHandlingOption, IntentHandlingOption.Disabled)
    val intentHandlingHttpEndpoint = ISetting(SettingsEnum.IntentHandlingEndpoint, "")

    val intentHandlingHassEndpoint = ISetting(SettingsEnum.IntentHandlingHassUrl, "")
    val intentHandlingHassAccessToken = ISetting(SettingsEnum.IntentHandlingHassAccessToken, "")
    val intentHandlingHomeAssistantOption =
        ISetting(SettingsEnum.IsIntentHandlingHassEvent, HomeAssistantIntentHandlingOption.Event)

}
