package org.rhasspy.mobile.settings

import kotlinx.serialization.builtins.ListSerializer
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.connection.HttpConnectionData
import org.rhasspy.mobile.data.connection.LocalWebserverConnectionData
import org.rhasspy.mobile.data.connection.MqttConnectionData
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.*
import org.rhasspy.mobile.data.settings.SettingsEnum

/**
 * used by di needs to be called after change to have an effect
 */
object ConfigurationSetting {

    val siteId = ISetting(SettingsEnum.SiteId, "mobile")

    val rhasspy2Connection = ISetting(
        key = SettingsEnum.Rhasspy2Connection,
        initial = HttpConnectionData(
            host = "",
            timeout = 30000L,
            bearerToken = "",
            isSSLVerificationDisabled = false
        ),
        serializer = HttpConnectionData.serializer()
    )

    val rhasspy3Connection = ISetting(
        key = SettingsEnum.Rhasspy3Connection,
        initial = HttpConnectionData(
            host = "",
            timeout = 30000L,
            bearerToken = "",
            isSSLVerificationDisabled = false
        ),
        serializer = HttpConnectionData.serializer()
    )

    val homeAssistantConnection = ISetting(
        key = SettingsEnum.HomeAssistantConnection,
        initial = HttpConnectionData(
            host = "",
            timeout = 30000L,
            bearerToken = "",
            isSSLVerificationDisabled = false
        ),
        serializer = HttpConnectionData.serializer()
    )

    val mqttConnection = ISetting(
        key = SettingsEnum.MqttConnection,
        initial = MqttConnectionData(
            isEnabled = false,
            host = "tcp://<server>:1883",
            userName = "",
            password = "",
            isSSLEnabled = false,
            connectionTimeout = 5,
            keepAliveInterval = 30,
            retryInterval = 10L,
            keystoreFile = null
        ),
        serializer = MqttConnectionData.serializer()
    )

    val localWebserverConnection = ISetting(
        key = SettingsEnum.LocalWebserverConnection,
        initial = LocalWebserverConnectionData(
            isEnabled = true,
            port = 12101,
            isSSLEnabled = false,
            keyStoreFile = null,
            keyStorePassword = "",
            keyAlias = "",
            keyPassword = "",
        ),
        serializer = LocalWebserverConnectionData.serializer()
    )

    val wakeWordOption = ISetting(SettingsEnum.WakeWordOption, WakeWordOption.Disabled, WakeWordOption.serializer())

    val wakeWordAudioRecorderChannel = ISetting(SettingsEnum.WakeWordAudioRecorderChannel, AudioFormatChannelType.default, AudioFormatChannelType.serializer())
    val wakeWordAudioRecorderEncoding = ISetting(SettingsEnum.WakeWordAudioRecorderEncoding, AudioFormatEncodingType.default, AudioFormatEncodingType.serializer())
    val wakeWordAudioRecorderSampleRate = ISetting(SettingsEnum.WakeWordAudioRecorderSampleRate, AudioFormatSampleRateType.default, AudioFormatSampleRateType.serializer())

    val wakeWordAudioOutputChannel = ISetting(SettingsEnum.WakeWordAudioOutputChannel, AudioFormatChannelType.default, AudioFormatChannelType.serializer())
    val wakeWordAudioOutputEncoding = ISetting(SettingsEnum.WakeWordAudioROutputEncoding, AudioFormatEncodingType.default, AudioFormatEncodingType.serializer())
    val wakeWordAudioOutputSampleRate = ISetting(SettingsEnum.WakeWordAudioOutputSampleRate, AudioFormatSampleRateType.default, AudioFormatSampleRateType.serializer())

    val wakeWordPorcupineAccessToken = ISetting(SettingsEnum.WakeWordPorcupineAccessToken, "")
    val wakeWordPorcupineKeywordDefaultOptions = ISetting(
        key = SettingsEnum.WakeWordPorcupineKeywordDefaultSelectedOptions,
        initial = PorcupineKeywordOption.entries.map { PorcupineDefaultKeyword(it, false, 0.5) },
        serializer = ListSerializer(PorcupineDefaultKeyword.serializer())
    )
    val wakeWordPorcupineKeywordCustomOptions = ISetting(
        key = SettingsEnum.WakeWordPorcupineKeywordCustomOptions,
        initial = emptyList(),
        serializer = ListSerializer(PorcupineCustomKeyword.serializer())
    )
    val wakeWordPorcupineLanguage = ISetting(SettingsEnum.WakeWordPorcupineLanguage, PorcupineLanguageOption.EN, PorcupineLanguageOption.serializer())
    val wakeWordUdpOutputHost = ISetting(SettingsEnum.WakeWordUDPOutputHost, "")
    val wakeWordUdpOutputPort = ISetting(SettingsEnum.WakeWordUDPOutputPort, 20000)

    val dialogManagementOption = ISetting(SettingsEnum.DialogManagementOption, DialogManagementOption.Local, DialogManagementOption.serializer())
    val textAsrTimeout = ISetting(SettingsEnum.DialogManagementLocalAsrTimeout, 10000L)
    val intentRecognitionTimeout = ISetting(SettingsEnum.DialogManagementLocalIntentRecognitionTimeout, 10000L)
    val recordingTimeout = ISetting(SettingsEnum.DialogManagementLocalRecordingTimeout, 10000L)

    val intentRecognitionOption = ISetting(SettingsEnum.IntentRecognitionOption, IntentRecognitionOption.Disabled, IntentRecognitionOption.serializer())

    val textToSpeechOption = ISetting(SettingsEnum.TextToSpeechOption, TextToSpeechOption.Disabled, TextToSpeechOption.serializer())

    val audioPlayingOption = ISetting(SettingsEnum.AudioPlayingOption, AudioPlayingOption.Local, AudioPlayingOption.serializer())
    val audioOutputOption = ISetting(SettingsEnum.AudioOutputOption, AudioOutputOption.Sound, AudioOutputOption.serializer())

    val audioPlayingMqttSiteId = ISetting(SettingsEnum.AudioPlayingMqttSiteId, "")

    val speechToTextOption = ISetting(SettingsEnum.SpeechToTextOption, SpeechToTextOption.Disabled, SpeechToTextOption.serializer())

    val speechToTextAudioRecorderChannel = ISetting(SettingsEnum.SpeechToTextAudioRecorderChannel, AudioFormatChannelType.default, AudioFormatChannelType.serializer())
    val speechToTextAudioRecorderEncoding = ISetting(SettingsEnum.SpeechToTextAudioRecorderEncoding, AudioFormatEncodingType.default, AudioFormatEncodingType.serializer())
    val speechToTextAudioRecorderSampleRate = ISetting(SettingsEnum.SpeechToTextAudioRecorderSampleRate, AudioFormatSampleRateType.default, AudioFormatSampleRateType.serializer())

    val speechToTextAudioOutputChannel = ISetting(SettingsEnum.SpeechToTextAudioOutputChannel, AudioFormatChannelType.default, AudioFormatChannelType.serializer())
    val speechToTextAudioOutputEncoding = ISetting(SettingsEnum.SpeechToTextAudioOutputEncoding, AudioFormatEncodingType.default, AudioFormatEncodingType.serializer())
    val speechToTextAudioOutputSampleRate = ISetting(SettingsEnum.SpeechToTextAudioOutputSampleRate, AudioFormatSampleRateType.default, AudioFormatSampleRateType.serializer())

    val isUseSpeechToTextMqttSilenceDetection = ISetting(SettingsEnum.SpeechToTextMqttSilenceDetection, true)

    val intentHandlingOption = ISetting(SettingsEnum.IntentHandlingOption, IntentHandlingOption.Disabled, IntentHandlingOption.serializer())

    val intentHandlingHomeAssistantOption = ISetting(SettingsEnum.IsIntentHandlingHassEvent, HomeAssistantIntentHandlingOption.Intent, HomeAssistantIntentHandlingOption.serializer())

    val voiceActivityDetectionOption = ISetting(SettingsEnum.VoiceActivityDetectionOption, VoiceActivityDetectionOption.Disabled, VoiceActivityDetectionOption.serializer())
    val automaticSilenceDetectionAudioLevel = ISetting(SettingsEnum.AutomaticSilenceDetectionAudioLevel, 40f)
    val automaticSilenceDetectionTime = ISetting<Long?>(SettingsEnum.AutomaticSilenceDetectionTime, 2000L)
    val automaticSilenceDetectionMinimumTime = ISetting<Long?>(SettingsEnum.AutomaticSilenceDetectionMinimumTime, 2000L)

}