package org.rhasspy.mobile.settings.settingsmigration

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.*

/**
 * used by di needs to be called after change to have an effect
 */
object DeprecatedConfigurationSetting {

    val siteId = DeprecatedISetting(DeprecatedSettingsEnum.SiteId, "mobile")

    val isHttpServerEnabled = DeprecatedISetting(DeprecatedSettingsEnum.HttpServerEnabled, true)
    val httpServerPort = DeprecatedISetting(DeprecatedSettingsEnum.HttpServerPort, 12101)
    val isHttpServerSSLEnabledEnabled = DeprecatedISetting(DeprecatedSettingsEnum.HttpServerSSLEnabled, false)
    val httpServerSSLKeyStoreFile = DeprecatedISetting(DeprecatedSettingsEnum.HttpServerSSLKeyStoreFile, null, OkioPathSerializer)
    val httpServerSSLKeyStorePassword = DeprecatedISetting(DeprecatedSettingsEnum.HttpServerSSLKeyStorePassword, "")
    val httpServerSSLKeyAlias = DeprecatedISetting(DeprecatedSettingsEnum.HttpServerSSLKeyAlias, "")
    val httpServerSSLKeyPassword = DeprecatedISetting(DeprecatedSettingsEnum.HttpServerSSLKeyPassword, "")

    val isHttpClientSSLVerificationDisabled = DeprecatedISetting(DeprecatedSettingsEnum.SSLVerificationDisabled, true)
    val httpClientServerEndpointHost = DeprecatedISetting(DeprecatedSettingsEnum.HttpClientServerEndpointHost, "")
    val httpClientServerEndpointPort = DeprecatedISetting(DeprecatedSettingsEnum.HttpClientServerEndpointPort, 12101)
    val httpClientTimeout = DeprecatedISetting(DeprecatedSettingsEnum.HttpClientTimeout, 30000L)

    val isMqttEnabled = DeprecatedISetting(DeprecatedSettingsEnum.MQTTEnabled, false)
    val mqttHost = DeprecatedISetting(DeprecatedSettingsEnum.MQTTHost, "")
    val mqttPort = DeprecatedISetting(DeprecatedSettingsEnum.MQTTPort, 1883)
    val mqttUserName = DeprecatedISetting(DeprecatedSettingsEnum.MQTTUserName, "")
    val mqttPassword = DeprecatedISetting(DeprecatedSettingsEnum.MQTTPassword, "")
    val isMqttSSLEnabled = DeprecatedISetting(DeprecatedSettingsEnum.MQTTSSLEnabled, false)
    val mqttConnectionTimeout = DeprecatedISetting(DeprecatedSettingsEnum.MQTTConnectionTimeout, 5L)
    val mqttKeepAliveInterval = DeprecatedISetting(DeprecatedSettingsEnum.MQTTKeepAliveInterval, 30L)
    val mqttRetryInterval = DeprecatedISetting(DeprecatedSettingsEnum.MQTTRetryInterval, 10L)
    val mqttKeyStoreFile = DeprecatedISetting(DeprecatedSettingsEnum.MQTTKeyStoreFile, null, OkioPathSerializer)


    val wakeWordOption = DeprecatedISetting(DeprecatedSettingsEnum.WakeWordOption, WakeWordOption.Disabled)

    val wakeWordAudioRecorderChannel = DeprecatedISetting(DeprecatedSettingsEnum.WakeWordAudioRecorderChannel, AudioFormatChannelType.default)
    val wakeWordAudioRecorderEncoding = DeprecatedISetting(DeprecatedSettingsEnum.WakeWordAudioRecorderEncoding, AudioFormatEncodingType.default)
    val wakeWordAudioRecorderSampleRate = DeprecatedISetting(DeprecatedSettingsEnum.WakeWordAudioRecorderSampleRate, AudioFormatSampleRateType.default)

    val wakeWordAudioOutputChannel = DeprecatedISetting(DeprecatedSettingsEnum.WakeWordAudioOutputChannel, AudioFormatChannelType.default)
    val wakeWordAudioOutputEncoding = DeprecatedISetting(DeprecatedSettingsEnum.WakeWordAudioROutputEncoding, AudioFormatEncodingType.default)
    val wakeWordAudioOutputSampleRate = DeprecatedISetting(DeprecatedSettingsEnum.WakeWordAudioOutputSampleRate, AudioFormatSampleRateType.default)

    val wakeWordPorcupineAccessToken = DeprecatedISetting(DeprecatedSettingsEnum.WakeWordPorcupineAccessToken, "")
    val wakeWordPorcupineKeywordDefaultOptions = DeprecatedISetting(
        DeprecatedSettingsEnum.WakeWordPorcupineKeywordDefaultSelectedOptions,
        PorcupineKeywordOption.values().map { PorcupineDefaultKeyword(it, false, 0.5) }.toImmutableList(),
        PorcupineDefaultKeywordSerializer
    )
    val wakeWordPorcupineKeywordCustomOptions = DeprecatedISetting(DeprecatedSettingsEnum.WakeWordPorcupineKeywordCustomOptions, persistentListOf(), PorcupineCustomKeywordSerializer)
    val wakeWordPorcupineLanguage = DeprecatedISetting(DeprecatedSettingsEnum.WakeWordPorcupineLanguage, PorcupineLanguageOption.EN)
    val wakeWordUdpOutputHost = DeprecatedISetting(DeprecatedSettingsEnum.WakeWordUDPOutputHost, "")
    val wakeWordUdpOutputPort = DeprecatedISetting(DeprecatedSettingsEnum.WakeWordUDPOutputPort, 20000)

    val dialogManagementOption = DeprecatedISetting(DeprecatedSettingsEnum.DialogManagementOption, DialogManagementOption.Local)
    val textAsrTimeout = DeprecatedISetting(DeprecatedSettingsEnum.DialogManagementLocalAsrTimeout, 10000L)
    val intentRecognitionTimeout = DeprecatedISetting(DeprecatedSettingsEnum.DialogManagementLocalIntentRecognitionTimeout, 10000L)
    val recordingTimeout = DeprecatedISetting(DeprecatedSettingsEnum.DialogManagementLocalRecordingTimeout, 10000L)

    val intentRecognitionOption = DeprecatedISetting(DeprecatedSettingsEnum.IntentRecognitionOption, IntentRecognitionOption.Disabled)
    val isUseCustomIntentRecognitionHttpEndpoint = DeprecatedISetting(DeprecatedSettingsEnum.CustomIntentRecognitionHttpEndpoint, false)
    val intentRecognitionHttpEndpoint = DeprecatedISetting(DeprecatedSettingsEnum.IntentRecognitionHttpEndpoint, "")

    val textToSpeechOption = DeprecatedISetting(DeprecatedSettingsEnum.TextToSpeechOption, TextToSpeechOption.Disabled)
    val isUseCustomTextToSpeechHttpEndpoint = DeprecatedISetting(DeprecatedSettingsEnum.CustomTextToSpeechOptionHttpEndpoint, false)
    val textToSpeechHttpEndpoint = DeprecatedISetting(DeprecatedSettingsEnum.TextToSpeechHttpEndpoint, "")

    val audioPlayingOption = DeprecatedISetting(DeprecatedSettingsEnum.AudioPlayingOption, AudioPlayingOption.Local)
    val audioOutputOption = DeprecatedISetting(DeprecatedSettingsEnum.AudioOutputOption, AudioOutputOption.Sound)
    val isUseCustomAudioPlayingHttpEndpoint = DeprecatedISetting(DeprecatedSettingsEnum.CustomAudioPlayingHttpEndpoint, false)
    val audioPlayingHttpEndpoint = DeprecatedISetting(DeprecatedSettingsEnum.AudioPlayingHttpEndpoint, "")
    val audioPlayingMqttSiteId = DeprecatedISetting(DeprecatedSettingsEnum.AudioPlayingMqttSiteId, "")

    val speechToTextOption = DeprecatedISetting(DeprecatedSettingsEnum.SpeechToTextOption, SpeechToTextOption.Disabled)

    val speechToTextAudioRecorderChannel = DeprecatedISetting(DeprecatedSettingsEnum.SpeechToTextAudioRecorderChannel, AudioFormatChannelType.default)
    val speechToTextAudioRecorderEncoding = DeprecatedISetting(DeprecatedSettingsEnum.SpeechToTextAudioRecorderEncoding, AudioFormatEncodingType.default)
    val speechToTextAudioRecorderSampleRate = DeprecatedISetting(DeprecatedSettingsEnum.SpeechToTextAudioRecorderSampleRate, AudioFormatSampleRateType.default)

    val speechToTextAudioOutputChannel = DeprecatedISetting(DeprecatedSettingsEnum.SpeechToTextAudioOutputChannel, AudioFormatChannelType.default)
    val speechToTextAudioOutputEncoding = DeprecatedISetting(DeprecatedSettingsEnum.SpeechToTextAudioOutputEncoding, AudioFormatEncodingType.default)
    val speechToTextAudioOutputSampleRate = DeprecatedISetting(DeprecatedSettingsEnum.SpeechToTextAudioOutputSampleRate, AudioFormatSampleRateType.default)

    val isUseCustomSpeechToTextHttpEndpoint = DeprecatedISetting(DeprecatedSettingsEnum.CustomSpeechToTextEndpoint, false)
    val isUseSpeechToTextMqttSilenceDetection = DeprecatedISetting(DeprecatedSettingsEnum.SpeechToTextMqttSilenceDetection, true)
    val speechToTextHttpEndpoint = DeprecatedISetting(DeprecatedSettingsEnum.SpeechToTextHttpEndpoint, "")

    val intentHandlingOption = DeprecatedISetting(DeprecatedSettingsEnum.IntentHandlingOption, IntentHandlingOption.Disabled)
    val intentHandlingHttpEndpoint = DeprecatedISetting(DeprecatedSettingsEnum.IntentHandlingEndpoint, "")

    val intentHandlingHomeAssistantEndpoint = DeprecatedISetting(DeprecatedSettingsEnum.IntentHandlingHassUrl, "")
    val intentHandlingHomeAssistantAccessToken = DeprecatedISetting(DeprecatedSettingsEnum.IntentHandlingHassAccessToken, "")
    val intentHandlingHomeAssistantOption = DeprecatedISetting(DeprecatedSettingsEnum.IsIntentHandlingHassEvent, HomeAssistantIntentHandlingOption.Intent)

}