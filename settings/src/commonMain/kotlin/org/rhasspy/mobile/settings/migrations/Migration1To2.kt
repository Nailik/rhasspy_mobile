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
            settings[DeprecatedSettingsEnum.IntentHandlingOption.name] = HandleDomainOption.Disabled.name
        }
    }


    override fun migrate() {
        val speechToTextAudioRecorderChannel = ISetting(
            key = DeprecatedSettingsEnum.SpeechToTextAudioRecorderChannel,
            initial = AudioFormatChannelType.default,
            serializer = AudioFormatChannelType.serializer(),
        )
        val speechToTextAudioRecorderEncoding = ISetting(
            key = DeprecatedSettingsEnum.SpeechToTextAudioRecorderEncoding,
            initial = AudioFormatEncodingType.default,
            serializer = AudioFormatEncodingType.serializer(),
        )
        val speechToTextAudioRecorderSampleRate = ISetting(
            key = DeprecatedSettingsEnum.SpeechToTextAudioRecorderSampleRate,
            initial = AudioFormatSampleRateType.default,
            serializer = AudioFormatSampleRateType.serializer(),
        )

        val speechToTextAudioOutputChannel = ISetting(
            key = DeprecatedSettingsEnum.SpeechToTextAudioOutputChannel,
            initial = AudioFormatChannelType.default,
            serializer = AudioFormatChannelType.serializer(),
        )
        val speechToTextAudioOutputEncoding = ISetting(
            key = DeprecatedSettingsEnum.SpeechToTextAudioOutputEncoding,
            initial = AudioFormatEncodingType.default,
            serializer = AudioFormatEncodingType.serializer(),
        )
        val speechToTextAudioOutputSampleRate = ISetting(
            key = DeprecatedSettingsEnum.SpeechToTextAudioOutputSampleRate,
            initial = AudioFormatSampleRateType.default,
            serializer = AudioFormatSampleRateType.serializer(),
        )

        val wakeWordAudioRecorderChannel = ISetting(
            key = DeprecatedSettingsEnum.WakeWordAudioRecorderChannel,
            initial = AudioFormatChannelType.default,
            serializer = AudioFormatChannelType.serializer(),
        )
        val wakeWordAudioRecorderEncoding = ISetting(
            key = DeprecatedSettingsEnum.WakeWordAudioRecorderEncoding,
            initial = AudioFormatEncodingType.default,
            serializer = AudioFormatEncodingType.serializer(),
        )
        val wakeWordAudioRecorderSampleRate = ISetting(
            key = DeprecatedSettingsEnum.WakeWordAudioRecorderSampleRate,
            initial = AudioFormatSampleRateType.default,
            serializer = AudioFormatSampleRateType.serializer(),
        )

        val wakeWordAudioOutputChannel = ISetting(
            key = DeprecatedSettingsEnum.WakeWordAudioOutputChannel,
            initial = AudioFormatChannelType.default,
            serializer = AudioFormatChannelType.serializer(),
        )
        val wakeWordAudioOutputEncoding = ISetting(
            key = DeprecatedSettingsEnum.WakeWordAudioROutputEncoding,
            initial = AudioFormatEncodingType.default,
            serializer = AudioFormatEncodingType.serializer(),
        )
        val wakeWordAudioOutputSampleRate = ISetting(
            key = DeprecatedSettingsEnum.WakeWordAudioOutputSampleRate,
            initial = AudioFormatSampleRateType.default,
            serializer = AudioFormatSampleRateType.serializer(),
        )

        val isPauseRecordingOnMedia = ISetting(DeprecatedSettingsEnum.AudioRecorderPauseRecordingOnMedia, true)

        val vadDomainOption = ISetting(
            key = DeprecatedSettingsEnum.VoiceActivityDetectionOption,
            initial = VadDomainOption.Disabled,
            serializer = VadDomainOption.serializer(),
        )
        val automaticSilenceDetectionAudioLevel = ISetting(DeprecatedSettingsEnum.AutomaticSilenceDetectionAudioLevel, 40f)
        val automaticSilenceDetectionTime = ISetting<Long?>(DeprecatedSettingsEnum.AutomaticSilenceDetectionTime, 2000L)
        val automaticSilenceDetectionMinimumTime = ISetting<Long?>(DeprecatedSettingsEnum.AutomaticSilenceDetectionMinimumTime, 2000L)

        val wakeDomainOption = ISetting(
            key = DeprecatedSettingsEnum.WakeWordOption,
            initial = WakeDomainOption.Disabled,
            serializer = WakeDomainOption.serializer(),
        )
        val wakeWordPorcupineAccessToken = ISetting(DeprecatedSettingsEnum.WakeWordPorcupineAccessToken, "")
        val wakeWordPorcupineKeywordDefaultOptions = ISetting(
            key = DeprecatedSettingsEnum.WakeWordPorcupineKeywordDefaultSelectedOptions,
            initial = PorcupineKeywordOption.entries.map { PorcupineDefaultKeyword(it, false, 0.5) },
            serializer = ListSerializer(PorcupineDefaultKeyword.serializer())
        )
        val wakeWordPorcupineKeywordCustomOptions = ISetting(
            key = DeprecatedSettingsEnum.WakeWordPorcupineKeywordCustomOptions,
            initial = emptyList(),
            serializer = ListSerializer(PorcupineCustomKeyword.serializer())
        )
        val wakeWordPorcupineLanguage = ISetting(
            key = DeprecatedSettingsEnum.WakeWordPorcupineLanguage,
            initial = PorcupineLanguageOption.EN,
            serializer = PorcupineLanguageOption.serializer(),
        )
        val wakeWordUdpOutputHost = ISetting(DeprecatedSettingsEnum.WakeWordUDPOutputHost, "")
        val wakeWordUdpOutputPort = ISetting(DeprecatedSettingsEnum.WakeWordUDPOutputPort, 20000)

        val asrDomainOption = ISetting(
            key = DeprecatedSettingsEnum.SpeechToTextOption,
            initial = AsrDomainOption.Disabled,
            serializer = AsrDomainOption.serializer(),
        )
        val isUseSpeechToTextMqttSilenceDetection = ISetting(DeprecatedSettingsEnum.SpeechToTextMqttSilenceDetection, true)
        val handleDomainOption = ISetting(
            key = DeprecatedSettingsEnum.IntentHandlingOption,
            initial = HandleDomainOption.Disabled,
            serializer = HandleDomainOption.serializer(),
        )
        val intentHandlingHomeAssistantOption = ISetting(
            key = DeprecatedSettingsEnum.IsIntentHandlingHassEvent,
            initial = HomeAssistantIntentHandlingOption.Intent,
            serializer = HomeAssistantIntentHandlingOption.serializer(),
        )
        val intentDomainOption = ISetting(
            key = DeprecatedSettingsEnum.IntentRecognitionOption,
            initial = IntentDomainOption.Disabled,
            serializer = IntentDomainOption.serializer(),
        )
        val sndDomainOption = ISetting(
            key = DeprecatedSettingsEnum.AudioPlayingOption,
            initial = SndDomainOption.Local,
            serializer = SndDomainOption.serializer(),
        )
        val audioOutputOption = ISetting(
            key = DeprecatedSettingsEnum.AudioOutputOption,
            initial = AudioOutputOption.Sound,
            serializer = AudioOutputOption.serializer(),
        )
        val audioPlayingMqttSiteId = ISetting(DeprecatedSettingsEnum.AudioPlayingMqttSiteId, "")
        val ttsDomainOption = ISetting(
            key = DeprecatedSettingsEnum.TextToSpeechOption,
            initial = TtsDomainOption.Disabled,
            serializer = TtsDomainOption.serializer(),
        )

        val pipelineManagerOption = ISetting(
            key = DeprecatedSettingsEnum.DialogManagementOption,
            initial = PipelineManagerOption.Local,
            serializer = PipelineManagerOption.serializer(),
        )
        val textAsrTimeout = ISetting(DeprecatedSettingsEnum.DialogManagementLocalAsrTimeout, 10000L)
        val intentRecognitionTimeout = ISetting(DeprecatedSettingsEnum.DialogManagementLocalIntentRecognitionTimeout, 10000L)
        val recordingTimeout = ISetting(DeprecatedSettingsEnum.DialogManagementLocalRecordingTimeout, 10000L)

        ConfigurationSetting.micDomainData.value = MicDomainData(
            audioInputChannel = speechToTextAudioRecorderChannel.value,
            audioInputEncoding = speechToTextAudioRecorderEncoding.value,
            audioInputSampleRate = speechToTextAudioRecorderSampleRate.value,
            audioOutputChannel = speechToTextAudioOutputChannel.value,
            audioOutputEncoding = speechToTextAudioOutputEncoding.value,
            audioOutputSampleRate = speechToTextAudioOutputSampleRate.value,
            isPauseRecordingOnMediaPlayback = isPauseRecordingOnMedia.value,
        )

        ConfigurationSetting.vadDomainData.value = VadDomainData(
            option = vadDomainOption.value,
            automaticSilenceDetectionAudioLevel = automaticSilenceDetectionAudioLevel.value,
            automaticSilenceDetectionTime = automaticSilenceDetectionTime.value?.milliseconds ?: 2.seconds,
            automaticSilenceDetectionMinimumTime = automaticSilenceDetectionMinimumTime.value?.milliseconds ?: 2.seconds,
        )

        ConfigurationSetting.wakeDomainData.value = WakeDomainData(
            wakeDomainOption = wakeDomainOption.value,
            wakeWordPorcupineAccessToken = wakeWordPorcupineAccessToken.value,
            wakeWordPorcupineKeywordDefaultOptions = wakeWordPorcupineKeywordDefaultOptions.value,
            wakeWordPorcupineKeywordCustomOptions = wakeWordPorcupineKeywordCustomOptions.value,
            wakeWordPorcupineLanguage = wakeWordPorcupineLanguage.value,
            wakeWordUdpOutputHost = wakeWordUdpOutputHost.value,
            wakeWordUdpOutputPort = wakeWordUdpOutputPort.value,
        )

        ConfigurationSetting.asrDomainData.value = AsrDomainData(
            option = asrDomainOption.value,
            isUseSpeechToTextMqttSilenceDetection = isUseSpeechToTextMqttSilenceDetection.value,
            voiceTimeout = 20.seconds,
            mqttResultTimeout = 20.seconds,
        )

        ConfigurationSetting.handleDomainData.value = HandleDomainData(
            option = handleDomainOption.value,
            homeAssistantIntentHandlingOption = intentHandlingHomeAssistantOption.value,
            homeAssistantEventTimeout = 20.seconds,
        )

        ConfigurationSetting.intentDomainData.value = IntentDomainData(
            option = intentDomainOption.value,
            isRhasspy2HermesHttpHandleWithRecognition = true,
            rhasspy2HermesHttpHandleTimeout = 20.seconds,
            rhasspy2HermesMqttHandleTimeout = 20.seconds,
        )

        ConfigurationSetting.sndDomainData.value = SndDomainData(
            option = sndDomainOption.value,
            localOutputOption = audioOutputOption.value,
            mqttSiteId = audioPlayingMqttSiteId.value,
            audioTimeout = 20.seconds,
            rhasspy2HermesMqttTimeout = 20.seconds,
        )

        ConfigurationSetting.ttsDomainData.value = TtsDomainData(
            option = ttsDomainOption.value,
            rhasspy2HermesMqttTimeout = 20.seconds,
        )

        ConfigurationSetting.pipelineData.value = PipelineData(
            option = pipelineManagerOption.value,
        )


        speechToTextAudioRecorderChannel.delete()
        speechToTextAudioRecorderEncoding.delete()
        speechToTextAudioRecorderSampleRate.delete()

        speechToTextAudioOutputChannel.delete()
        speechToTextAudioOutputEncoding.delete()
        speechToTextAudioOutputSampleRate.delete()

        isPauseRecordingOnMedia.delete()

        vadDomainOption.delete()
        automaticSilenceDetectionAudioLevel.delete()
        automaticSilenceDetectionTime.delete()
        automaticSilenceDetectionMinimumTime.delete()

        wakeDomainOption.delete()
        wakeWordPorcupineAccessToken.delete()
        wakeWordPorcupineKeywordDefaultOptions.delete()
        wakeWordPorcupineKeywordCustomOptions.delete()
        wakeWordPorcupineLanguage.delete()
        wakeWordUdpOutputHost.delete()
        wakeWordUdpOutputPort.delete()

        asrDomainOption.delete()
        isUseSpeechToTextMqttSilenceDetection.delete()

        handleDomainOption.delete()
        intentHandlingHomeAssistantOption.delete()

        intentDomainOption.delete()

        sndDomainOption.delete()
        audioOutputOption.delete()
        audioPlayingMqttSiteId.delete()

        ttsDomainOption.delete()

        pipelineManagerOption.delete()
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