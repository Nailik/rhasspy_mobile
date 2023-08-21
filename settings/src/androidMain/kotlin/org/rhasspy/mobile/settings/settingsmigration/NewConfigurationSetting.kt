package org.rhasspy.mobile.settings.settingsmigration

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.*
import org.rhasspy.mobile.settings.settingsmigration.types.*

/**
 * used by di needs to be called after change to have an effect
 */
object NewConfigurationSetting {

    val siteId = NewStringSetting(NewSettingsEnum.SiteId, "mobile")

    val isHttpServerEnabled = NewBooleanSetting(NewSettingsEnum.HttpServerEnabled, true)
    val httpServerPort = NewIntSetting(NewSettingsEnum.HttpServerPort, 12101)
    val isHttpServerSSLEnabledEnabled = NewBooleanSetting(NewSettingsEnum.HttpServerSSLEnabled, false)
    val httpServerSSLKeyStoreFile = NewPathNullableSetting(NewSettingsEnum.HttpServerSSLKeyStoreFile, null)
    val httpServerSSLKeyStorePassword = NewStringSetting(NewSettingsEnum.HttpServerSSLKeyStorePassword, "")
    val httpServerSSLKeyAlias = NewStringSetting(NewSettingsEnum.HttpServerSSLKeyAlias, "")
    val httpServerSSLKeyPassword = NewStringSetting(NewSettingsEnum.HttpServerSSLKeyPassword, "")

    val isHttpClientSSLVerificationDisabled = NewBooleanSetting(NewSettingsEnum.SSLVerificationDisabled, true)
    val httpClientServerEndpointHost = NewStringSetting(NewSettingsEnum.HttpClientServerEndpointHost, "")
    val httpClientServerEndpointPort = NewIntSetting(NewSettingsEnum.HttpClientServerEndpointPort, 12101)
    val httpClientTimeout = NewLongSetting(NewSettingsEnum.HttpClientTimeout, 30000L)

    val isMqttEnabled = NewBooleanSetting(NewSettingsEnum.MQTTEnabled, false)
    val mqttHost = NewStringSetting(NewSettingsEnum.MQTTHost, "")
    val mqttPort = NewIntSetting(NewSettingsEnum.MQTTPort, 1883)
    val mqttUserName = NewStringSetting(NewSettingsEnum.MQTTUserName, "")
    val mqttPassword = NewStringSetting(NewSettingsEnum.MQTTPassword, "")
    val isMqttSSLEnabled = NewBooleanSetting(NewSettingsEnum.MQTTSSLEnabled, false)
    val mqttConnectionTimeout = NewLongSetting(NewSettingsEnum.MQTTConnectionTimeout, 5L)
    val mqttKeepAliveInterval = NewLongSetting(NewSettingsEnum.MQTTKeepAliveInterval, 30L)
    val mqttRetryInterval = NewLongSetting(NewSettingsEnum.MQTTRetryInterval, 10L)
    val mqttKeyStoreFile = NewPathNullableSetting(NewSettingsEnum.MQTTKeyStoreFile, null)


    val wakeWordOption = NewIOptionSetting(NewSettingsEnum.WakeWordOption, WakeWordOption.Disabled)

    val wakeWordAudioRecorderChannel = NewIOptionSetting(NewSettingsEnum.WakeWordAudioRecorderChannel, AudioFormatChannelType.default)
    val wakeWordAudioRecorderEncoding = NewIOptionSetting(NewSettingsEnum.WakeWordAudioRecorderEncoding, AudioFormatEncodingType.default)
    val wakeWordAudioRecorderSampleRate = NewIOptionSetting(NewSettingsEnum.WakeWordAudioRecorderSampleRate, AudioFormatSampleRateType.default)

    val wakeWordAudioOutputChannel = NewIOptionSetting(NewSettingsEnum.WakeWordAudioOutputChannel, AudioFormatChannelType.default)
    val wakeWordAudioOutputEncoding = NewIOptionSetting(NewSettingsEnum.WakeWordAudioROutputEncoding, AudioFormatEncodingType.default)
    val wakeWordAudioOutputSampleRate = NewIOptionSetting(NewSettingsEnum.WakeWordAudioOutputSampleRate, AudioFormatSampleRateType.default)

    val wakeWordPorcupineAccessToken = NewStringSetting(NewSettingsEnum.WakeWordPorcupineAccessToken, "")
    val wakeWordPorcupineKeywordDefaultOptions = NewPorcupineKeywordDefaultListSetting(
        NewSettingsEnum.WakeWordPorcupineKeywordDefaultSelectedOptions,
        PorcupineKeywordOption.values().map { PorcupineDefaultKeyword(it, false, 0.5f) }.toImmutableList(),
    )
    val wakeWordPorcupineKeywordCustomOptions = NewPorcupineKeywordCustomListSetting(NewSettingsEnum.WakeWordPorcupineKeywordCustomOptions, persistentListOf())
    val wakeWordPorcupineLanguage = NewIOptionSetting(NewSettingsEnum.WakeWordPorcupineLanguage, PorcupineLanguageOption.EN)
    val wakeWordUdpOutputHost = NewStringSetting(NewSettingsEnum.WakeWordUDPOutputHost, "")
    val wakeWordUdpOutputPort = NewIntSetting(NewSettingsEnum.WakeWordUDPOutputPort, 20000)

    val dialogManagementOption = NewIOptionSetting(NewSettingsEnum.DialogManagementOption, DialogManagementOption.Local)
    val textAsrTimeout = NewLongSetting(NewSettingsEnum.DialogManagementLocalAsrTimeout, 10000L)
    val intentRecognitionTimeout = NewLongSetting(NewSettingsEnum.DialogManagementLocalIntentRecognitionTimeout, 10000L)
    val recordingTimeout = NewLongSetting(NewSettingsEnum.DialogManagementLocalRecordingTimeout, 10000L)

    val intentRecognitionOption = NewIOptionSetting(NewSettingsEnum.IntentRecognitionOption, IntentRecognitionOption.Disabled)
    val isUseCustomIntentRecognitionHttpEndpoint = NewBooleanSetting(NewSettingsEnum.CustomIntentRecognitionHttpEndpoint, false)
    val intentRecognitionHttpEndpoint = NewStringSetting(NewSettingsEnum.IntentRecognitionHttpEndpoint, "")

    val textToSpeechOption = NewIOptionSetting(NewSettingsEnum.TextToSpeechOption, TextToSpeechOption.Disabled)
    val isUseCustomTextToSpeechHttpEndpoint = NewBooleanSetting(NewSettingsEnum.CustomTextToSpeechOptionHttpEndpoint, false)
    val textToSpeechHttpEndpoint = NewStringSetting(NewSettingsEnum.TextToSpeechHttpEndpoint, "")

    val audioPlayingOption = NewIOptionSetting(NewSettingsEnum.AudioPlayingOption, AudioPlayingOption.Local)
    val audioOutputOption = NewIOptionSetting(NewSettingsEnum.AudioOutputOption, AudioOutputOption.Sound)
    val isUseCustomAudioPlayingHttpEndpoint = NewBooleanSetting(NewSettingsEnum.CustomAudioPlayingHttpEndpoint, false)
    val audioPlayingHttpEndpoint = NewStringSetting(NewSettingsEnum.AudioPlayingHttpEndpoint, "")
    val audioPlayingMqttSiteId = NewStringSetting(NewSettingsEnum.AudioPlayingMqttSiteId, "")

    val speechToTextOption = NewIOptionSetting(NewSettingsEnum.SpeechToTextOption, SpeechToTextOption.Disabled)

    val speechToTextAudioRecorderChannel = NewIOptionSetting(NewSettingsEnum.SpeechToTextAudioRecorderChannel, AudioFormatChannelType.default)
    val speechToTextAudioRecorderEncoding = NewIOptionSetting(NewSettingsEnum.SpeechToTextAudioRecorderEncoding, AudioFormatEncodingType.default)
    val speechToTextAudioRecorderSampleRate = NewIOptionSetting(NewSettingsEnum.SpeechToTextAudioRecorderSampleRate, AudioFormatSampleRateType.default)

    val speechToTextAudioOutputChannel = NewIOptionSetting(NewSettingsEnum.SpeechToTextAudioOutputChannel, AudioFormatChannelType.default)
    val speechToTextAudioOutputEncoding = NewIOptionSetting(NewSettingsEnum.SpeechToTextAudioOutputEncoding, AudioFormatEncodingType.default)
    val speechToTextAudioOutputSampleRate = NewIOptionSetting(NewSettingsEnum.SpeechToTextAudioOutputSampleRate, AudioFormatSampleRateType.default)

    val isUseCustomSpeechToTextHttpEndpoint = NewBooleanSetting(NewSettingsEnum.CustomSpeechToTextEndpoint, false)
    val isUseSpeechToTextMqttSilenceDetection = NewBooleanSetting(NewSettingsEnum.SpeechToTextMqttSilenceDetection, true)
    val speechToTextHttpEndpoint = NewStringSetting(NewSettingsEnum.SpeechToTextHttpEndpoint, "")

    val intentHandlingOption = NewIOptionSetting(NewSettingsEnum.IntentHandlingOption, IntentHandlingOption.Disabled)
    val intentHandlingHttpEndpoint = NewStringSetting(NewSettingsEnum.IntentHandlingEndpoint, "")

    val intentHandlingHomeAssistantEndpoint = NewStringSetting(NewSettingsEnum.IntentHandlingHassUrl, "")
    val intentHandlingHomeAssistantAccessToken = NewStringSetting(NewSettingsEnum.IntentHandlingHassAccessToken, "")
    val intentHandlingHomeAssistantOption = NewIOptionSetting(NewSettingsEnum.IsIntentHandlingHassEvent, HomeAssistantIntentHandlingOption.Intent)

}