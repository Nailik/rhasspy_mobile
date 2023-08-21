package org.rhasspy.mobile.settings.settingsmigration

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.*
import org.rhasspy.mobile.data.settings.SettingsEnum

/**
 * used by di needs to be called after change to have an effect
 */
object DeprecatedConfigurationSetting {

    val siteId = DeprecatedISetting(SettingsEnum.SiteId, "mobile")

    val isHttpServerEnabled = DeprecatedISetting(SettingsEnum.HttpServerEnabled, true)
    val httpServerPort = DeprecatedISetting(SettingsEnum.HttpServerPort, 12101)
    val isHttpServerSSLEnabledEnabled = DeprecatedISetting(SettingsEnum.HttpServerSSLEnabled, false)
    val httpServerSSLKeyStoreFile = DeprecatedISetting(SettingsEnum.HttpServerSSLKeyStoreFile, null, OkioPathSerializer)
    val httpServerSSLKeyStorePassword = DeprecatedISetting(SettingsEnum.HttpServerSSLKeyStorePassword, "")
    val httpServerSSLKeyAlias = DeprecatedISetting(SettingsEnum.HttpServerSSLKeyAlias, "")
    val httpServerSSLKeyPassword = DeprecatedISetting(SettingsEnum.HttpServerSSLKeyPassword, "")

    val isHttpClientSSLVerificationDisabled = DeprecatedISetting(SettingsEnum.SSLVerificationDisabled, true)
    val httpClientServerEndpointHost = DeprecatedISetting(SettingsEnum.HttpClientServerEndpointHost, "")
    val httpClientServerEndpointPort = DeprecatedISetting(SettingsEnum.HttpClientServerEndpointPort, 12101)
    val httpClientTimeout = DeprecatedISetting(SettingsEnum.HttpClientTimeout, 30000L)

    val isMqttEnabled = DeprecatedISetting(SettingsEnum.MQTTEnabled, false)
    val mqttHost = DeprecatedISetting(SettingsEnum.MQTTHost, "")
    val mqttPort = DeprecatedISetting(SettingsEnum.MQTTPort, 1883)
    val mqttUserName = DeprecatedISetting(SettingsEnum.MQTTUserName, "")
    val mqttPassword = DeprecatedISetting(SettingsEnum.MQTTPassword, "")
    val isMqttSSLEnabled = DeprecatedISetting(SettingsEnum.MQTTSSLEnabled, false)
    val mqttConnectionTimeout = DeprecatedISetting(SettingsEnum.MQTTConnectionTimeout, 5L)
    val mqttKeepAliveInterval = DeprecatedISetting(SettingsEnum.MQTTKeepAliveInterval, 30L)
    val mqttRetryInterval = DeprecatedISetting(SettingsEnum.MQTTRetryInterval, 10L)
    val mqttKeyStoreFile = DeprecatedISetting(SettingsEnum.MQTTKeyStoreFile, null, OkioPathSerializer)


    val wakeWordOption = DeprecatedISetting(SettingsEnum.WakeWordOption, WakeWordOption.Disabled)

    val wakeWordAudioRecorderChannel = DeprecatedISetting(SettingsEnum.WakeWordAudioRecorderChannel, AudioFormatChannelType.default)
    val wakeWordAudioRecorderEncoding = DeprecatedISetting(SettingsEnum.WakeWordAudioRecorderEncoding, AudioFormatEncodingType.default)
    val wakeWordAudioRecorderSampleRate = DeprecatedISetting(SettingsEnum.WakeWordAudioRecorderSampleRate, AudioFormatSampleRateType.default)

    val wakeWordAudioOutputChannel = DeprecatedISetting(SettingsEnum.WakeWordAudioOutputChannel, AudioFormatChannelType.default)
    val wakeWordAudioOutputEncoding = DeprecatedISetting(SettingsEnum.WakeWordAudioROutputEncoding, AudioFormatEncodingType.default)
    val wakeWordAudioOutputSampleRate = DeprecatedISetting(SettingsEnum.WakeWordAudioOutputSampleRate, AudioFormatSampleRateType.default)

    val wakeWordPorcupineAccessToken = DeprecatedISetting(SettingsEnum.WakeWordPorcupineAccessToken, "")
    val wakeWordPorcupineKeywordDefaultOptions = DeprecatedISetting(
        SettingsEnum.WakeWordPorcupineKeywordDefaultSelectedOptions,
        PorcupineKeywordOption.values().map { PorcupineDefaultKeyword(it, false, 0.5f) }.toImmutableList(),
        PorcupineDefaultKeywordSerializer
    )
    val wakeWordPorcupineKeywordCustomOptions = DeprecatedISetting(SettingsEnum.WakeWordPorcupineKeywordCustomOptions, persistentListOf(), PorcupineCustomKeywordSerializer)
    val wakeWordPorcupineLanguage = DeprecatedISetting(SettingsEnum.WakeWordPorcupineLanguage, PorcupineLanguageOption.EN)
    val wakeWordUdpOutputHost = DeprecatedISetting(SettingsEnum.WakeWordUDPOutputHost, "")
    val wakeWordUdpOutputPort = DeprecatedISetting(SettingsEnum.WakeWordUDPOutputPort, 20000)

    val dialogManagementOption = DeprecatedISetting(SettingsEnum.DialogManagementOption, DialogManagementOption.Local)
    val textAsrTimeout = DeprecatedISetting(SettingsEnum.DialogManagementLocalAsrTimeout, 10000L)
    val intentRecognitionTimeout = DeprecatedISetting(SettingsEnum.DialogManagementLocalIntentRecognitionTimeout, 10000L)
    val recordingTimeout = DeprecatedISetting(SettingsEnum.DialogManagementLocalRecordingTimeout, 10000L)

    val intentRecognitionOption = DeprecatedISetting(SettingsEnum.IntentRecognitionOption, IntentRecognitionOption.Disabled)
    val isUseCustomIntentRecognitionHttpEndpoint = DeprecatedISetting(SettingsEnum.CustomIntentRecognitionHttpEndpoint, false)
    val intentRecognitionHttpEndpoint = DeprecatedISetting(SettingsEnum.IntentRecognitionHttpEndpoint, "")

    val textToSpeechOption = DeprecatedISetting(SettingsEnum.TextToSpeechOption, TextToSpeechOption.Disabled)
    val isUseCustomTextToSpeechHttpEndpoint = DeprecatedISetting(SettingsEnum.CustomTextToSpeechOptionHttpEndpoint, false)
    val textToSpeechHttpEndpoint = DeprecatedISetting(SettingsEnum.TextToSpeechHttpEndpoint, "")

    val audioPlayingOption = DeprecatedISetting(SettingsEnum.AudioPlayingOption, AudioPlayingOption.Local)
    val audioOutputOption = DeprecatedISetting(SettingsEnum.AudioOutputOption, AudioOutputOption.Sound)
    val isUseCustomAudioPlayingHttpEndpoint = DeprecatedISetting(SettingsEnum.CustomAudioPlayingHttpEndpoint, false)
    val audioPlayingHttpEndpoint = DeprecatedISetting(SettingsEnum.AudioPlayingHttpEndpoint, "")
    val audioPlayingMqttSiteId = DeprecatedISetting(SettingsEnum.AudioPlayingMqttSiteId, "")

    val speechToTextOption = DeprecatedISetting(SettingsEnum.SpeechToTextOption, SpeechToTextOption.Disabled)

    val speechToTextAudioRecorderChannel = DeprecatedISetting(SettingsEnum.SpeechToTextAudioRecorderChannel, AudioFormatChannelType.default)
    val speechToTextAudioRecorderEncoding = DeprecatedISetting(SettingsEnum.SpeechToTextAudioRecorderEncoding, AudioFormatEncodingType.default)
    val speechToTextAudioRecorderSampleRate = DeprecatedISetting(SettingsEnum.SpeechToTextAudioRecorderSampleRate, AudioFormatSampleRateType.default)

    val speechToTextAudioOutputChannel = DeprecatedISetting(SettingsEnum.SpeechToTextAudioOutputChannel, AudioFormatChannelType.default)
    val speechToTextAudioOutputEncoding = DeprecatedISetting(SettingsEnum.SpeechToTextAudioOutputEncoding, AudioFormatEncodingType.default)
    val speechToTextAudioOutputSampleRate = DeprecatedISetting(SettingsEnum.SpeechToTextAudioOutputSampleRate, AudioFormatSampleRateType.default)

    val isUseCustomSpeechToTextHttpEndpoint = DeprecatedISetting(SettingsEnum.CustomSpeechToTextEndpoint, false)
    val isUseSpeechToTextMqttSilenceDetection = DeprecatedISetting(SettingsEnum.SpeechToTextMqttSilenceDetection, true)
    val speechToTextHttpEndpoint = DeprecatedISetting(SettingsEnum.SpeechToTextHttpEndpoint, "")

    val intentHandlingOption = DeprecatedISetting(SettingsEnum.IntentHandlingOption, IntentHandlingOption.Disabled)
    val intentHandlingHttpEndpoint = DeprecatedISetting(SettingsEnum.IntentHandlingEndpoint, "")

    val intentHandlingHomeAssistantEndpoint = DeprecatedISetting(SettingsEnum.IntentHandlingHassUrl, "")
    val intentHandlingHomeAssistantAccessToken = DeprecatedISetting(SettingsEnum.IntentHandlingHassAccessToken, "")
    val intentHandlingHomeAssistantOption = DeprecatedISetting(SettingsEnum.IsIntentHandlingHassEvent, HomeAssistantIntentHandlingOption.Intent)

}