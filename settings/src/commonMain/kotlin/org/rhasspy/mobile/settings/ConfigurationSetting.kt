package org.rhasspy.mobile.settings

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.data.service.option.PorcupineKeywordOption
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.settings.serializer.OkioPathSerializer
import org.rhasspy.mobile.settings.serializer.PorcupineCustomKeywordSerializer
import org.rhasspy.mobile.settings.serializer.PorcupineDefaultKeywordSerializer

/**
 * used by di needs to be called after change to have an effect
 */
object ConfigurationSetting {

    val siteId = ISetting(SettingsEnum.SiteId, "mobile")

    val isHttpServerEnabled = ISetting(SettingsEnum.HttpServerEnabled, true)
    val httpServerPort = ISetting(SettingsEnum.HttpServerPort, 12101)
    val isHttpServerSSLEnabledEnabled = ISetting(SettingsEnum.HttpServerSSLEnabled, false)
    val httpServerSSLKeyStoreFile = ISetting(
        SettingsEnum.HttpServerSSLKeyStoreFile,
        null,
        OkioPathSerializer
    )
    val httpServerSSLKeyStorePassword = ISetting(SettingsEnum.HttpServerSSLKeyStorePassword, "")
    val httpServerSSLKeyAlias = ISetting(SettingsEnum.HttpServerSSLKeyAlias, "")
    val httpServerSSLKeyPassword = ISetting(SettingsEnum.HttpServerSSLKeyPassword, "")

    val isHttpClientSSLVerificationDisabled = ISetting(SettingsEnum.SSLVerificationDisabled, true)
    val httpClientServerEndpointHost = ISetting(SettingsEnum.HttpClientServerEndpointHost, "")
    val httpClientServerEndpointPort = ISetting(SettingsEnum.HttpClientServerEndpointPort, 12101)
    val httpClientTimeout = ISetting(SettingsEnum.HttpClientTimeout, 30000L)

    val isMqttEnabled = ISetting(SettingsEnum.MQTTEnabled, false)
    val mqttHost = ISetting(SettingsEnum.MQTTHost, "")
    val mqttPort = ISetting(SettingsEnum.MQTTPort, 1883)
    val mqttUserName = ISetting(SettingsEnum.MQTTUserName, "")
    val mqttPassword = ISetting(SettingsEnum.MQTTPassword, "")
    val isMqttSSLEnabled = ISetting(SettingsEnum.MQTTSSLEnabled, false)
    val mqttConnectionTimeout = ISetting(SettingsEnum.MQTTConnectionTimeout, 5L)
    val mqttKeepAliveInterval = ISetting(SettingsEnum.MQTTKeepAliveInterval, 30L)
    val mqttRetryInterval = ISetting(SettingsEnum.MQTTRetryInterval, 10L)
    val mqttKeyStoreFile = ISetting(
        SettingsEnum.MQTTKeyStoreFile,
        null,
        OkioPathSerializer
    )


    val wakeWordOption = ISetting(SettingsEnum.WakeWordOption, WakeWordOption.Disabled)
    val wakeWordPorcupineAccessToken = ISetting(SettingsEnum.WakeWordPorcupineAccessToken, "")
    val wakeWordPorcupineAudioRecorderSettings =
        ISetting(SettingsEnum.WakeWordPorcupineAudioRecorderSettings, false)
    val wakeWordPorcupineKeywordDefaultOptions = ISetting(
        SettingsEnum.WakeWordPorcupineKeywordDefaultSelectedOptions,
        PorcupineKeywordOption.values().map { PorcupineDefaultKeyword(it, false, 0.5f) }
            .toImmutableList(),
        PorcupineDefaultKeywordSerializer
    )
    val wakeWordPorcupineKeywordCustomOptions = ISetting(
        SettingsEnum.WakeWordPorcupineKeywordCustomOptions,
        persistentListOf(),
        PorcupineCustomKeywordSerializer
    )
    val wakeWordPorcupineLanguage =
        ISetting(SettingsEnum.WakeWordPorcupineLanguage, PorcupineLanguageOption.EN)
    val wakeWordUdpOutputHost = ISetting(SettingsEnum.WakeWordUDPOutputHost, "")
    val wakeWordUdpOutputPort = ISetting(SettingsEnum.WakeWordUDPOutputPort, 20000)

    val dialogManagementOption =
        ISetting(SettingsEnum.DialogManagementOption, DialogManagementOption.Local)
    val textAsrTimeout = ISetting(SettingsEnum.DialogManagementLocalAsrTimeout, 10000L)
    val intentRecognitionTimeout =
        ISetting(SettingsEnum.DialogManagementLocalIntentRecognitionTimeout, 10000L)
    val recordingTimeout = ISetting(SettingsEnum.DialogManagementLocalRecordingTimeout, 10000L)

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
    val audioPlayingMqttSiteId = ISetting(SettingsEnum.AudioPlayingMqttSiteId, "")

    val speechToTextOption = ISetting(SettingsEnum.SpeechToTextOption, SpeechToTextOption.Disabled)
    val isUseCustomSpeechToTextHttpEndpoint =
        ISetting(SettingsEnum.CustomSpeechToTextEndpoint, false)
    val isUseSpeechToTextMqttSilenceDetection =
        ISetting(SettingsEnum.SpeechToTextMqttSilenceDetection, true)
    val speechToTextHttpEndpoint = ISetting(SettingsEnum.SpeechToTextHttpEndpoint, "")

    val intentHandlingOption =
        ISetting(SettingsEnum.IntentHandlingOption, IntentHandlingOption.Disabled)
    val intentHandlingHttpEndpoint = ISetting(SettingsEnum.IntentHandlingEndpoint, "")

    val intentHandlingHomeAssistantEndpoint = ISetting(SettingsEnum.IntentHandlingHassUrl, "")
    val intentHandlingHomeAssistantAccessToken =
        ISetting(SettingsEnum.IntentHandlingHassAccessToken, "")
    val intentHandlingHomeAssistantOption =
        ISetting(SettingsEnum.IsIntentHandlingHassEvent, HomeAssistantIntentHandlingOption.Intent)

}