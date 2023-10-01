package org.rhasspy.mobile.settings.migrations

import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.serialization.builtins.ListSerializer
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.domain.*
import org.rhasspy.mobile.data.pipeline.PipelineData
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.*
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.ISetting
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

internal object Migration1To2 : IMigration(1, 2) {

    private enum class DeprecatedSettingsEnum {
        SpeechToTextAudioRecorderChannel,
        SpeechToTextAudioRecorderEncoding,
        SpeechToTextAudioRecorderSampleRate,
        SpeechToTextAudioOutputChannel,
        SpeechToTextAudioOutputEncoding,
        SpeechToTextAudioOutputSampleRate,
        WakeWordAudioRecorderChannel,
        WakeWordAudioRecorderEncoding,
        WakeWordAudioRecorderSampleRate,
        WakeWordAudioOutputChannel,
        WakeWordAudioROutputEncoding,
        WakeWordAudioOutputSampleRate,
        AudioRecorderPauseRecordingOnMedia,
        VoiceActivityDetectionOption,
        AutomaticSilenceDetectionAudioLevel,
        AutomaticSilenceDetectionTime,
        AutomaticSilenceDetectionMinimumTime,
        WakeWordOption,
        WakeWordPorcupineAccessToken,
        WakeWordPorcupineKeywordDefaultSelectedOptions,
        WakeWordPorcupineKeywordCustomOptions,
        WakeWordPorcupineLanguage,
        WakeWordUDPOutputHost,
        WakeWordUDPOutputPort,
        SpeechToTextOption,
        SpeechToTextMqttSilenceDetection,
        IntentRecognitionOption,
        TextToSpeechOption,
        AudioPlayingOption,
        AudioOutputOption,
        AudioPlayingMqttSiteId,
        IntentHandlingOption,
        IsIntentHandlingHassEvent,
        DialogManagementOption,
        DialogManagementLocalAsrTimeout,
        DialogManagementLocalIntentRecognitionTimeout,
        DialogManagementLocalRecordingTimeout
    }

    override fun preMigrate() {
        if (settings[DeprecatedSettingsEnum.IntentHandlingOption.name, ""] == "WithRecognition") {
            settings[DeprecatedSettingsEnum.IntentHandlingOption.name] = IntentHandlingOption.Disabled.name
        }
    }

    private val speechToTextAudioRecorderChannel = ISetting(DeprecatedSettingsEnum.SpeechToTextAudioRecorderChannel, AudioFormatChannelType.default)
    private val speechToTextAudioRecorderEncoding = ISetting(DeprecatedSettingsEnum.SpeechToTextAudioRecorderEncoding, AudioFormatEncodingType.default)
    private val speechToTextAudioRecorderSampleRate = ISetting(DeprecatedSettingsEnum.SpeechToTextAudioRecorderSampleRate, AudioFormatSampleRateType.default)

    private val speechToTextAudioOutputChannel = ISetting(DeprecatedSettingsEnum.SpeechToTextAudioOutputChannel, AudioFormatChannelType.default)
    private val speechToTextAudioOutputEncoding = ISetting(DeprecatedSettingsEnum.SpeechToTextAudioOutputEncoding, AudioFormatEncodingType.default)
    private val speechToTextAudioOutputSampleRate = ISetting(DeprecatedSettingsEnum.SpeechToTextAudioOutputSampleRate, AudioFormatSampleRateType.default)

    private val wakeWordAudioRecorderChannel = ISetting(DeprecatedSettingsEnum.WakeWordAudioRecorderChannel, AudioFormatChannelType.default)
    private val wakeWordAudioRecorderEncoding = ISetting(DeprecatedSettingsEnum.WakeWordAudioRecorderEncoding, AudioFormatEncodingType.default)
    private val wakeWordAudioRecorderSampleRate = ISetting(DeprecatedSettingsEnum.WakeWordAudioRecorderSampleRate, AudioFormatSampleRateType.default)

    private val wakeWordAudioOutputChannel = ISetting(DeprecatedSettingsEnum.WakeWordAudioOutputChannel, AudioFormatChannelType.default)
    private val wakeWordAudioOutputEncoding = ISetting(DeprecatedSettingsEnum.WakeWordAudioROutputEncoding, AudioFormatEncodingType.default)
    private val wakeWordAudioOutputSampleRate = ISetting(DeprecatedSettingsEnum.WakeWordAudioOutputSampleRate, AudioFormatSampleRateType.default)

    private val isPauseRecordingOnMedia = ISetting(DeprecatedSettingsEnum.AudioRecorderPauseRecordingOnMedia, true)


    private val voiceActivityDetectionOption = ISetting(DeprecatedSettingsEnum.VoiceActivityDetectionOption, VoiceActivityDetectionOption.Disabled)
    private val automaticSilenceDetectionAudioLevel = ISetting(DeprecatedSettingsEnum.AutomaticSilenceDetectionAudioLevel, 40f)
    private val automaticSilenceDetectionTime = ISetting<Long?>(DeprecatedSettingsEnum.AutomaticSilenceDetectionTime, 2000L)
    private val automaticSilenceDetectionMinimumTime = ISetting<Long?>(DeprecatedSettingsEnum.AutomaticSilenceDetectionMinimumTime, 2000L)

    private val wakeWordOption = ISetting(DeprecatedSettingsEnum.WakeWordOption, WakeWordOption.Disabled)
    private val wakeWordPorcupineAccessToken = ISetting(DeprecatedSettingsEnum.WakeWordPorcupineAccessToken, "")
    private val wakeWordPorcupineKeywordDefaultOptions = ISetting(
        key = DeprecatedSettingsEnum.WakeWordPorcupineKeywordDefaultSelectedOptions,
        initial = PorcupineKeywordOption.entries.map { PorcupineDefaultKeyword(it, false, 0.5) },
        serializer = ListSerializer(PorcupineDefaultKeyword.serializer())
    )
    private val wakeWordPorcupineKeywordCustomOptions = ISetting(
        key = DeprecatedSettingsEnum.WakeWordPorcupineKeywordCustomOptions,
        initial = emptyList(),
        serializer = ListSerializer(PorcupineCustomKeyword.serializer())
    )
    private val wakeWordPorcupineLanguage = ISetting(DeprecatedSettingsEnum.WakeWordPorcupineLanguage, PorcupineLanguageOption.EN)
    private val wakeWordUdpOutputHost = ISetting(DeprecatedSettingsEnum.WakeWordUDPOutputHost, "")
    private val wakeWordUdpOutputPort = ISetting(DeprecatedSettingsEnum.WakeWordUDPOutputPort, 20000)

    private val speechToTextOption = ISetting(DeprecatedSettingsEnum.SpeechToTextOption, SpeechToTextOption.Disabled)
    private val isUseSpeechToTextMqttSilenceDetection = ISetting(DeprecatedSettingsEnum.SpeechToTextMqttSilenceDetection, true)
    private val intentHandlingOption = ISetting(DeprecatedSettingsEnum.IntentHandlingOption, IntentHandlingOption.Disabled)
    private val intentHandlingHomeAssistantOption = ISetting(DeprecatedSettingsEnum.IsIntentHandlingHassEvent, HomeAssistantIntentHandlingOption.Intent)
    private val intentRecognitionOption = ISetting(DeprecatedSettingsEnum.IntentRecognitionOption, IntentRecognitionOption.Disabled)
    private val audioPlayingOption = ISetting(DeprecatedSettingsEnum.AudioPlayingOption, AudioPlayingOption.Local)
    private val audioOutputOption = ISetting(DeprecatedSettingsEnum.AudioOutputOption, AudioOutputOption.Sound)
    private val audioPlayingMqttSiteId = ISetting(DeprecatedSettingsEnum.AudioPlayingMqttSiteId, "")
    private val textToSpeechOption = ISetting(DeprecatedSettingsEnum.TextToSpeechOption, TextToSpeechOption.Disabled)

    private val dialogManagementOption = ISetting(DeprecatedSettingsEnum.DialogManagementOption, DialogManagementOption.Local)
    private val textAsrTimeout = ISetting(DeprecatedSettingsEnum.DialogManagementLocalAsrTimeout, 10000L)
    private val intentRecognitionTimeout = ISetting(DeprecatedSettingsEnum.DialogManagementLocalIntentRecognitionTimeout, 10000L)
    private val recordingTimeout = ISetting(DeprecatedSettingsEnum.DialogManagementLocalRecordingTimeout, 10000L)

    override fun migrate() {
        ConfigurationSetting.micDomainData.value = MicDomainData(
            audioInputChannel = speechToTextAudioRecorderChannel.value,
            audioInputEncoding = speechToTextAudioRecorderEncoding.value,
            audioInputSampleRate = speechToTextAudioRecorderSampleRate.value,
            audioOutputChannel = speechToTextAudioOutputChannel.value,
            audioOutputEncoding = speechToTextAudioOutputEncoding.value,
            audioOutputSampleRate = speechToTextAudioOutputSampleRate.value,
            isUseAutomaticGainControl = false,
            isPauseRecordingOnMediaPlayback = isPauseRecordingOnMedia.value,
        )

        ConfigurationSetting.vadDomainData.value = VadDomainData(
            option = voiceActivityDetectionOption.value,
            timeout = 20.seconds,
            automaticSilenceDetectionAudioLevel = automaticSilenceDetectionAudioLevel.value,
            automaticSilenceDetectionTime = automaticSilenceDetectionTime.value?.milliseconds ?: 2.seconds,
            automaticSilenceDetectionMinimumTime = automaticSilenceDetectionMinimumTime.value?.milliseconds ?: 2.seconds,
        )

        ConfigurationSetting.wakeDomainData.value = WakeDomainData(
            wakeWordOption = wakeWordOption.value,
            wakeWordPorcupineAccessToken = wakeWordPorcupineAccessToken.value,
            wakeWordPorcupineKeywordDefaultOptions = wakeWordPorcupineKeywordDefaultOptions.value,
            wakeWordPorcupineKeywordCustomOptions = wakeWordPorcupineKeywordCustomOptions.value,
            wakeWordPorcupineLanguage = wakeWordPorcupineLanguage.value,
            wakeWordUdpOutputHost = wakeWordUdpOutputHost.value,
            wakeWordUdpOutputPort = wakeWordUdpOutputPort.value,
        )

        ConfigurationSetting.asrDomainData.value = AsrDomainData(
            option = speechToTextOption.value,
            isUseSpeechToTextMqttSilenceDetection = isUseSpeechToTextMqttSilenceDetection.value,
            mqttTimeout = 20.seconds,
        )

        ConfigurationSetting.handleDomainData.value = HandleDomainData(
            option = intentHandlingOption.value,
            homeAssistantIntentHandlingOption = intentHandlingHomeAssistantOption.value,
            homeAssistantEventTimeout = 20.seconds,
        )

        ConfigurationSetting.intentDomainData.value = IntentDomainData(
            option = intentRecognitionOption.value,
            isRhasspy2HermesHttpHandleWithRecognition = true,
            rhasspy2HermesHttpHandleTimeout = 20.seconds,
            rhasspy2HermesMqttHandleTimeout = 20.seconds,
        )

        ConfigurationSetting.sndDomainData.value = SndDomainData(
            option = audioPlayingOption.value,
            localOutputOption = audioOutputOption.value,
            mqttSiteId = audioPlayingMqttSiteId.value,
            audioTimeout = 20.seconds,
            rhasspy2HermesMqttTimeout = 20.seconds,
        )

        ConfigurationSetting.ttsDomainData.value = TtsDomainData(
            option = textToSpeechOption.value,
            rhasspy2HermesMqttTimeout = 20.seconds,
        )

        ConfigurationSetting.pipelineData.value = PipelineData(
            option = dialogManagementOption.value,
        )


        speechToTextAudioRecorderChannel.delete()
        speechToTextAudioRecorderEncoding.delete()
        speechToTextAudioRecorderSampleRate.delete()

        speechToTextAudioOutputChannel.delete()
        speechToTextAudioOutputEncoding.delete()
        speechToTextAudioOutputSampleRate.delete()

        isPauseRecordingOnMedia.delete()

        voiceActivityDetectionOption.delete()
        automaticSilenceDetectionAudioLevel.delete()
        automaticSilenceDetectionTime.delete()
        automaticSilenceDetectionMinimumTime.delete()

        wakeWordOption.delete()
        wakeWordPorcupineAccessToken.delete()
        wakeWordPorcupineKeywordDefaultOptions.delete()
        wakeWordPorcupineKeywordCustomOptions.delete()
        wakeWordPorcupineLanguage.delete()
        wakeWordUdpOutputHost.delete()
        wakeWordUdpOutputPort.delete()

        speechToTextOption.delete()
        isUseSpeechToTextMqttSilenceDetection.delete()

        intentHandlingOption.delete()
        intentHandlingHomeAssistantOption.delete()

        intentRecognitionOption.delete()

        audioPlayingOption.delete()
        audioOutputOption.delete()
        audioPlayingMqttSiteId.delete()

        textToSpeechOption.delete()

        dialogManagementOption.delete()
        textAsrTimeout.delete()
        intentRecognitionTimeout.delete()
        recordingTimeout.delete()

        wakeWordAudioRecorderChannel.delete()
        wakeWordAudioRecorderEncoding.delete()
        wakeWordAudioRecorderSampleRate.delete()

        wakeWordAudioOutputChannel.delete()
        wakeWordAudioOutputEncoding.delete()
        wakeWordAudioOutputSampleRate.delete()
    }


}