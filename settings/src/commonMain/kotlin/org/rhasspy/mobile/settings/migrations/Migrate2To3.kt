@file:OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)

package org.rhasspy.mobile.settings.migrations

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.get
import com.russhwolf.settings.serialization.encodeValue
import com.russhwolf.settings.set
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import org.koin.core.component.get
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.domain.*
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.data.log.LogLevel
import org.rhasspy.mobile.data.pipeline.PipelineData
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.*
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.data.sounds.IndicationSoundOption
import org.rhasspy.mobile.data.sounds.IndicationSoundType
import org.rhasspy.mobile.data.theme.ThemeType
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.language.ILanguageUtils
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.ISetting
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

internal object Migrate2To3 : IMigration(2, 3) {

    private enum class MigrationSettingsEnum {
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
        DialogManagementLocalRecordingTimeout,
        SoundIndication,
        SoundIndicationOutput,
        WakeSoundVolume,
        RecordedSoundVolume,
        ErrorSoundVolume,
        WakeSound,
        RecordedSound,
        ErrorSound,
        CustomWakeSounds,
        CustomRecordedSounds,
        CustomErrorSounds,
    }

    override fun preMigrate() {
        if (settings[MigrationSettingsEnum.IntentHandlingOption.name, ""] == "WithRecognition") {
            settings[MigrationSettingsEnum.IntentHandlingOption.name] = HandleDomainOption.Disabled.name
        }
    }

    override fun migrate() {
        val speechToTextAudioRecorderChannel = ISetting(
            key = MigrationSettingsEnum.SpeechToTextAudioRecorderChannel,
            initial = AudioFormatChannelType.default,
            serializer = AudioFormatChannelType.serializer(),
        )
        val speechToTextAudioRecorderEncoding = ISetting(
            key = MigrationSettingsEnum.SpeechToTextAudioRecorderEncoding,
            initial = AudioFormatEncodingType.default,
            serializer = AudioFormatEncodingType.serializer(),
        )
        val speechToTextAudioRecorderSampleRate = ISetting(
            key = MigrationSettingsEnum.SpeechToTextAudioRecorderSampleRate,
            initial = AudioFormatSampleRateType.default,
            serializer = AudioFormatSampleRateType.serializer(),
        )

        val speechToTextAudioOutputChannel = ISetting(
            key = MigrationSettingsEnum.SpeechToTextAudioOutputChannel,
            initial = AudioFormatChannelType.default,
            serializer = AudioFormatChannelType.serializer(),
        )
        val speechToTextAudioOutputEncoding = ISetting(
            key = MigrationSettingsEnum.SpeechToTextAudioOutputEncoding,
            initial = AudioFormatEncodingType.default,
            serializer = AudioFormatEncodingType.serializer(),
        )
        val speechToTextAudioOutputSampleRate = ISetting(
            key = MigrationSettingsEnum.SpeechToTextAudioOutputSampleRate,
            initial = AudioFormatSampleRateType.default,
            serializer = AudioFormatSampleRateType.serializer(),
        )

        val wakeWordAudioRecorderChannel = ISetting(
            key = MigrationSettingsEnum.WakeWordAudioRecorderChannel,
            initial = AudioFormatChannelType.default,
            serializer = AudioFormatChannelType.serializer(),
        )
        val wakeWordAudioRecorderEncoding = ISetting(
            key = MigrationSettingsEnum.WakeWordAudioRecorderEncoding,
            initial = AudioFormatEncodingType.default,
            serializer = AudioFormatEncodingType.serializer(),
        )
        val wakeWordAudioRecorderSampleRate = ISetting(
            key = MigrationSettingsEnum.WakeWordAudioRecorderSampleRate,
            initial = AudioFormatSampleRateType.default,
            serializer = AudioFormatSampleRateType.serializer(),
        )

        val wakeWordAudioOutputChannel = ISetting(
            key = MigrationSettingsEnum.WakeWordAudioOutputChannel,
            initial = AudioFormatChannelType.default,
            serializer = AudioFormatChannelType.serializer(),
        )
        val wakeWordAudioOutputEncoding = ISetting(
            key = MigrationSettingsEnum.WakeWordAudioROutputEncoding,
            initial = AudioFormatEncodingType.default,
            serializer = AudioFormatEncodingType.serializer(),
        )
        val wakeWordAudioOutputSampleRate = ISetting(
            key = MigrationSettingsEnum.WakeWordAudioOutputSampleRate,
            initial = AudioFormatSampleRateType.default,
            serializer = AudioFormatSampleRateType.serializer(),
        )

        val isPauseRecordingOnMedia = ISetting(MigrationSettingsEnum.AudioRecorderPauseRecordingOnMedia, true)

        val vadDomainOption = ISetting(
            key = MigrationSettingsEnum.VoiceActivityDetectionOption,
            initial = VadDomainOption.Disabled,
            serializer = VadDomainOption.serializer(),
        )
        val automaticSilenceDetectionAudioLevel = ISetting(MigrationSettingsEnum.AutomaticSilenceDetectionAudioLevel, 40f)
        val automaticSilenceDetectionTime = ISetting<Long?>(MigrationSettingsEnum.AutomaticSilenceDetectionTime, 2000L)
        val automaticSilenceDetectionMinimumTime = ISetting<Long?>(MigrationSettingsEnum.AutomaticSilenceDetectionMinimumTime, 2000L)

        val wakeDomainOption = ISetting(
            key = MigrationSettingsEnum.WakeWordOption,
            initial = WakeDomainOption.Disabled,
            serializer = WakeDomainOption.serializer(),
        )
        val wakeWordPorcupineAccessToken = ISetting(MigrationSettingsEnum.WakeWordPorcupineAccessToken, "")
        val wakeWordPorcupineKeywordDefaultOptions = ISetting(
            key = MigrationSettingsEnum.WakeWordPorcupineKeywordDefaultSelectedOptions,
            initial = PorcupineKeywordOption.entries.map { PorcupineDefaultKeyword(it, false, 0.5) },
            serializer = ListSerializer(PorcupineDefaultKeyword.serializer())
        )
        val wakeWordPorcupineKeywordCustomOptions = ISetting(
            key = MigrationSettingsEnum.WakeWordPorcupineKeywordCustomOptions,
            initial = emptyList(),
            serializer = ListSerializer(PorcupineCustomKeyword.serializer())
        )
        val wakeWordPorcupineLanguage = ISetting(
            key = MigrationSettingsEnum.WakeWordPorcupineLanguage,
            initial = PorcupineLanguageOption.EN,
            serializer = PorcupineLanguageOption.serializer(),
        )
        val wakeWordUdpOutputHost = ISetting(MigrationSettingsEnum.WakeWordUDPOutputHost, "")
        val wakeWordUdpOutputPort = ISetting(MigrationSettingsEnum.WakeWordUDPOutputPort, 20000)

        val asrDomainOption = ISetting(
            key = MigrationSettingsEnum.SpeechToTextOption,
            initial = AsrDomainOption.Disabled,
            serializer = AsrDomainOption.serializer(),
        )
        val isUseSpeechToTextMqttSilenceDetection = ISetting(MigrationSettingsEnum.SpeechToTextMqttSilenceDetection, true)
        val handleDomainOption = ISetting(
            key = MigrationSettingsEnum.IntentHandlingOption,
            initial = HandleDomainOption.Disabled,
            serializer = HandleDomainOption.serializer(),
        )
        val intentHandlingHomeAssistantOption = ISetting(
            key = MigrationSettingsEnum.IsIntentHandlingHassEvent,
            initial = HomeAssistantIntentHandlingOption.Intent,
            serializer = HomeAssistantIntentHandlingOption.serializer(),
        )
        val intentDomainOption = ISetting(
            key = MigrationSettingsEnum.IntentRecognitionOption,
            initial = IntentDomainOption.Disabled,
            serializer = IntentDomainOption.serializer(),
        )
        val sndDomainOption = ISetting(
            key = MigrationSettingsEnum.AudioPlayingOption,
            initial = SndDomainOption.Local,
            serializer = SndDomainOption.serializer(),
        )
        val audioOutputOption = ISetting(
            key = MigrationSettingsEnum.AudioOutputOption,
            initial = AudioOutputOption.Sound,
            serializer = AudioOutputOption.serializer(),
        )
        val audioPlayingMqttSiteId = ISetting(MigrationSettingsEnum.AudioPlayingMqttSiteId, "")
        val ttsDomainOption = ISetting(
            key = MigrationSettingsEnum.TextToSpeechOption,
            initial = TtsDomainOption.Disabled,
            serializer = TtsDomainOption.serializer(),
        )

        val pipelineManagerOption = ISetting(
            key = MigrationSettingsEnum.DialogManagementOption,
            initial = PipelineManagerOption.Local,
            serializer = PipelineManagerOption.serializer(),
        )
        val textAsrTimeout = ISetting(MigrationSettingsEnum.DialogManagementLocalAsrTimeout, 10000L)
        val intentRecognitionTimeout = ISetting(MigrationSettingsEnum.DialogManagementLocalIntentRecognitionTimeout, 10000L)
        val recordingTimeout = ISetting(MigrationSettingsEnum.DialogManagementLocalRecordingTimeout, 10000L)


        val isSoundIndicationEnabled = ISetting(MigrationSettingsEnum.SoundIndication, true)
        val soundIndicationOutputOption = ISetting(
            key = MigrationSettingsEnum.SoundIndicationOutput,
            initial = AudioOutputOption.Notification,
            serializer = AudioOutputOption.serializer(),
        )


        val wakeSoundVolume = ISetting(MigrationSettingsEnum.WakeSoundVolume, 0.5F)
        val recordedSoundVolume = ISetting(MigrationSettingsEnum.RecordedSoundVolume, 0.5F)
        val errorSoundVolume = ISetting(MigrationSettingsEnum.ErrorSoundVolume, 0.5F)

        val wakeSound = ISetting(
            key = MigrationSettingsEnum.WakeSound,
            initial = "Default",
        )
        val recordedSound = ISetting(
            key = MigrationSettingsEnum.RecordedSound,
            initial = "Default",
        )
        val errorSound = ISetting(
            key = MigrationSettingsEnum.ErrorSound,
            initial = "Default",
        )

        //saves sound as pair, first is fileName as String, second is used and indicates if this custom sound file is used
        val customWakeSounds = ISetting(
            key = MigrationSettingsEnum.CustomWakeSounds,
            initial = emptyList(),
            serializer = ListSerializer(String.serializer()),
        )
        val customRecordedSounds = ISetting(
            key = MigrationSettingsEnum.CustomRecordedSounds,
            initial = emptyList(),
            serializer = ListSerializer(String.serializer()),
        )
        val customErrorSounds = ISetting(
            key = MigrationSettingsEnum.CustomErrorSounds,
            initial = emptyList(),
            serializer = ListSerializer(String.serializer()),
        )


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
            voiceTimeout = 20.seconds,
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
            rhasspy2HermesHttpIntentHandlingTimeout = 20.seconds,
            timeout = 20.seconds,
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
            localPipelineData = PipelineData.LocalPipelineData(
                isSoundIndicationEnabled = isSoundIndicationEnabled.value,
                soundIndicationOutputOption = soundIndicationOutputOption.value,
                wakeSound = PipelineData.LocalPipelineData.IndicationSoundOptionData(
                    type = IndicationSoundType.Wake,
                    volume = wakeSoundVolume.value,
                    option = when (val value = wakeSound.value) {
                        "Default"  -> IndicationSoundOption.Default
                        "Disabled" -> IndicationSoundOption.Disabled
                        else       -> IndicationSoundOption.Custom("${FolderType.SoundFolder.Wake}/$value")
                    },
                ),
                errorSound = PipelineData.LocalPipelineData.IndicationSoundOptionData(
                    type = IndicationSoundType.Error,
                    volume = errorSoundVolume.value,
                    option = when (val value = errorSound.value) {
                        "Default"  -> IndicationSoundOption.Default
                        "Disabled" -> IndicationSoundOption.Disabled
                        else       -> IndicationSoundOption.Custom("${FolderType.SoundFolder.Recorded}/$value")
                    },
                ),
                recordedSound = PipelineData.LocalPipelineData.IndicationSoundOptionData(
                    type = IndicationSoundType.Recorded,
                    volume = recordedSoundVolume.value,
                    option = when (val value = recordedSound.value) {
                        "Default"  -> IndicationSoundOption.Default
                        "Disabled" -> IndicationSoundOption.Disabled
                        else       -> IndicationSoundOption.Custom("${FolderType.SoundFolder.Error}/$value")
                    },
                ),
            )
        )

        /*
        TODO
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

                isSoundIndicationEnabled.delete()
                soundIndicationOutputOption.delete()
                wakeSoundVolume.delete()
                recordedSoundVolume.delete()
                errorSoundVolume.delete()
                wakeSound.delete()
                recordedSound.delete()
                errorSound.delete()
                customWakeSounds.delete()
                customRecordedSounds.delete()
                customErrorSounds.delete()

         */
    }


}