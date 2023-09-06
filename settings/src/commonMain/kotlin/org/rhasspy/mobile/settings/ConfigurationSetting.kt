package org.rhasspy.mobile.settings

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.*
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.settings.types.*

/**
 * used by di needs to be called after change to have an effect
 */
object ConfigurationSetting {

    val siteId = StringSetting(SettingsEnum.SiteId, "mobile")

    val httpConnection = HttpConnectionSetting()

    val isHttpServerEnabled = BooleanSetting(SettingsEnum.HttpServerEnabled, true)
    val httpServerPort = IntSetting(SettingsEnum.HttpServerPort, 12101)
    val isHttpServerSSLEnabledEnabled = BooleanSetting(SettingsEnum.HttpServerSSLEnabled, false)
    val httpServerSSLKeyStoreFile = PathNullableSetting(SettingsEnum.HttpServerSSLKeyStoreFile, null)
    val httpServerSSLKeyStorePassword = StringSetting(SettingsEnum.HttpServerSSLKeyStorePassword, "")
    val httpServerSSLKeyAlias = StringSetting(SettingsEnum.HttpServerSSLKeyAlias, "")
    val httpServerSSLKeyPassword = StringSetting(SettingsEnum.HttpServerSSLKeyPassword, "")

    val isMqttEnabled = BooleanSetting(SettingsEnum.MQTTEnabled, false)
    val mqttHost = StringSetting(SettingsEnum.MQTTHost, "")
    val mqttPort = IntSetting(SettingsEnum.MQTTPort, 1883)
    val mqttUserName = StringSetting(SettingsEnum.MQTTUserName, "")
    val mqttPassword = StringSetting(SettingsEnum.MQTTPassword, "")
    val isMqttSSLEnabled = BooleanSetting(SettingsEnum.MQTTSSLEnabled, false)
    val mqttConnectionTimeout = LongSetting(SettingsEnum.MQTTConnectionTimeout, 5L)
    val mqttKeepAliveInterval = LongSetting(SettingsEnum.MQTTKeepAliveInterval, 30L)
    val mqttRetryInterval = LongSetting(SettingsEnum.MQTTRetryInterval, 10L)
    val mqttKeyStoreFile = PathNullableSetting(SettingsEnum.MQTTKeyStoreFile, null)

    val wakeWordOption = IOptionSetting(SettingsEnum.WakeWordOption, WakeWordOption.Disabled)

    val wakeWordAudioRecorderChannel = IOptionSetting(SettingsEnum.WakeWordAudioRecorderChannel, AudioFormatChannelType.default)
    val wakeWordAudioRecorderEncoding = IOptionSetting(SettingsEnum.WakeWordAudioRecorderEncoding, AudioFormatEncodingType.default)
    val wakeWordAudioRecorderSampleRate = IOptionSetting(SettingsEnum.WakeWordAudioRecorderSampleRate, AudioFormatSampleRateType.default)

    val wakeWordAudioOutputChannel = IOptionSetting(SettingsEnum.WakeWordAudioOutputChannel, AudioFormatChannelType.default)
    val wakeWordAudioOutputEncoding = IOptionSetting(SettingsEnum.WakeWordAudioROutputEncoding, AudioFormatEncodingType.default)
    val wakeWordAudioOutputSampleRate = IOptionSetting(SettingsEnum.WakeWordAudioOutputSampleRate, AudioFormatSampleRateType.default)

    val wakeWordPorcupineAccessToken = StringSetting(SettingsEnum.WakeWordPorcupineAccessToken, "")
    val wakeWordPorcupineKeywordDefaultOptions = PorcupineKeywordDefaultListSetting(
        SettingsEnum.WakeWordPorcupineKeywordDefaultSelectedOptions,
        PorcupineKeywordOption.entries.map { PorcupineDefaultKeyword(it, false, 0.5) }.toImmutableList(),
    )
    val wakeWordPorcupineKeywordCustomOptions = PorcupineKeywordCustomListSetting(SettingsEnum.WakeWordPorcupineKeywordCustomOptions, persistentListOf())
    val wakeWordPorcupineLanguage = IOptionSetting(SettingsEnum.WakeWordPorcupineLanguage, PorcupineLanguageOption.EN)
    val wakeWordUdpOutputHost = StringSetting(SettingsEnum.WakeWordUDPOutputHost, "")
    val wakeWordUdpOutputPort = IntSetting(SettingsEnum.WakeWordUDPOutputPort, 20000)

    val dialogManagementOption = IOptionSetting(SettingsEnum.DialogManagementOption, DialogManagementOption.Local)
    val textAsrTimeout = LongSetting(SettingsEnum.DialogManagementLocalAsrTimeout, 10000L)
    val intentRecognitionTimeout = LongSetting(SettingsEnum.DialogManagementLocalIntentRecognitionTimeout, 10000L)
    val recordingTimeout = LongSetting(SettingsEnum.DialogManagementLocalRecordingTimeout, 10000L)

    val intentRecognitionOption = IOptionSetting(SettingsEnum.IntentRecognitionOption, IntentRecognitionOption.Disabled)

    val textToSpeechOption = IOptionSetting(SettingsEnum.TextToSpeechOption, TextToSpeechOption.Disabled)

    val audioPlayingOption = IOptionSetting(SettingsEnum.AudioPlayingOption, AudioPlayingOption.Local)
    val audioOutputOption = IOptionSetting(SettingsEnum.AudioOutputOption, AudioOutputOption.Sound)

    val audioPlayingMqttSiteId = StringSetting(SettingsEnum.AudioPlayingMqttSiteId, "")

    val speechToTextOption = IOptionSetting(SettingsEnum.SpeechToTextOption, SpeechToTextOption.Disabled)

    val speechToTextAudioRecorderChannel = IOptionSetting(SettingsEnum.SpeechToTextAudioRecorderChannel, AudioFormatChannelType.default)
    val speechToTextAudioRecorderEncoding = IOptionSetting(SettingsEnum.SpeechToTextAudioRecorderEncoding, AudioFormatEncodingType.default)
    val speechToTextAudioRecorderSampleRate = IOptionSetting(SettingsEnum.SpeechToTextAudioRecorderSampleRate, AudioFormatSampleRateType.default)

    val speechToTextAudioOutputChannel = IOptionSetting(SettingsEnum.SpeechToTextAudioOutputChannel, AudioFormatChannelType.default)
    val speechToTextAudioOutputEncoding = IOptionSetting(SettingsEnum.SpeechToTextAudioOutputEncoding, AudioFormatEncodingType.default)
    val speechToTextAudioOutputSampleRate = IOptionSetting(SettingsEnum.SpeechToTextAudioOutputSampleRate, AudioFormatSampleRateType.default)

    val isUseSpeechToTextMqttSilenceDetection = BooleanSetting(SettingsEnum.SpeechToTextMqttSilenceDetection, true)

    val intentHandlingOption = IOptionSetting(SettingsEnum.IntentHandlingOption, IntentHandlingOption.Disabled)

    val intentHandlingHomeAssistantOption = IOptionSetting(SettingsEnum.IsIntentHandlingHassEvent, HomeAssistantIntentHandlingOption.Intent)

    val voiceActivityDetectionOption = IOptionSetting(SettingsEnum.VoiceActivityDetectionOption, VoiceActivityDetectionOption.Disabled)
    val automaticSilenceDetectionAudioLevel = FloatSetting(SettingsEnum.AutomaticSilenceDetectionAudioLevel, 40f)
    val automaticSilenceDetectionTime = LongNullableSetting(SettingsEnum.AutomaticSilenceDetectionTime, 2000)
    val automaticSilenceDetectionMinimumTime = LongNullableSetting(SettingsEnum.AutomaticSilenceDetectionMinimumTime, 2000)

}