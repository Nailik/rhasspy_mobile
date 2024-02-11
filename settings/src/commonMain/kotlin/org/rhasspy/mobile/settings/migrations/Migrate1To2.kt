package org.rhasspy.mobile.settings.migrations

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.get
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.core.component.get
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.data.log.LogLevel
import org.rhasspy.mobile.data.service.option.*
import org.rhasspy.mobile.data.theme.ThemeType
import org.rhasspy.mobile.platformspecific.language.ILanguageUtils
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting

internal object Migrate1To2 : IMigration(1, 2) {

    private enum class MigrationSettingsEnum {
        WakeWordOption,
        WakeWordAudioRecorderChannel,
        WakeWordAudioRecorderEncoding,
        WakeWordAudioRecorderSampleRate,
        WakeWordAudioOutputChannel,
        WakeWordAudioROutputEncoding,
        WakeWordAudioOutputSampleRate,
        WakeWordPorcupineLanguage,
        DialogManagementOption,
        IntentRecognitionOption,
        TextToSpeechOption,
        AudioPlayingOption,
        AudioOutputOption,
        SpeechToTextOption,
        SpeechToTextAudioRecorderChannel,
        SpeechToTextAudioRecorderEncoding,
        SpeechToTextAudioRecorderSampleRate,
        SpeechToTextAudioOutputChannel,
        SpeechToTextAudioOutputEncoding,
        SpeechToTextAudioOutputSampleRate,
        IntentHandlingOption,
        IsIntentHandlingHassEvent,
        VoiceActivityDetectionOption,
        LanguageOption,
        ThemeOption,
        MicrophoneOverlaySize,
        SoundIndicationOutput,
        LogLevel,
        AudioFocusOption,
    }
    
    private lateinit var wakeWordOption: String
    private lateinit var wakeWordAudioRecorderChannel: String
    private lateinit var wakeWordAudioRecorderEncoding: String
    private lateinit var wakeWordAudioRecorderSampleRate: String

    private lateinit var wakeWordAudioOutputChannel: String
    private lateinit var wakeWordAudioOutputEncoding: String
    private lateinit var wakeWordAudioOutputSampleRate: String

    private lateinit var wakeWordPorcupineLanguage: String
    private lateinit var dialogManagementOption: String
    private lateinit var intentRecognitionOption: String

    private lateinit var textToSpeechOption: String
    private lateinit var audioPlayingOption: String
    private lateinit var audioOutputOption: String
    private lateinit var speechToTextOption: String

    private lateinit var speechToTextAudioRecorderChannel: String
    private lateinit var speechToTextAudioRecorderEncoding: String
    private lateinit var speechToTextAudioRecorderSampleRate: String

    private lateinit var speechToTextAudioOutputChannel: String
    private lateinit var speechToTextAudioOutputEncoding: String
    private lateinit var speechToTextAudioOutputSampleRate: String

    private lateinit var intentHandlingOption: String
    private lateinit var intentHandlingHomeAssistantOption: String
    private lateinit var voiceActivityDetectionOption: String

    private lateinit var languageType: String
    private lateinit var themeType: String

    private lateinit var microphoneOverlaySizeOption: String
    private lateinit var soundIndicationOutputOption: String

    private lateinit var logLevel: String
    private lateinit var audioFocusOption: String

    override fun preMigrate() {
        wakeWordOption = settings[MigrationSettingsEnum.WakeWordOption.name, ""]
        settings.remove(MigrationSettingsEnum.WakeWordOption.name)

        wakeWordAudioRecorderChannel = settings[MigrationSettingsEnum.WakeWordAudioRecorderChannel.name, ""]
        settings.remove(MigrationSettingsEnum.WakeWordAudioRecorderChannel.name)
        wakeWordAudioRecorderEncoding = settings[MigrationSettingsEnum.WakeWordAudioRecorderEncoding.name, ""]
        settings.remove(MigrationSettingsEnum.WakeWordAudioRecorderEncoding.name)
        wakeWordAudioRecorderSampleRate = settings[MigrationSettingsEnum.WakeWordAudioRecorderSampleRate.name, ""]
        settings.remove(MigrationSettingsEnum.WakeWordAudioRecorderSampleRate.name)

        wakeWordAudioOutputChannel = settings[MigrationSettingsEnum.WakeWordAudioOutputChannel.name, ""]
        settings.remove(MigrationSettingsEnum.WakeWordAudioOutputChannel.name)
        wakeWordAudioOutputEncoding = settings[MigrationSettingsEnum.WakeWordAudioROutputEncoding.name, ""]
        settings.remove(MigrationSettingsEnum.WakeWordAudioROutputEncoding.name)
        wakeWordAudioOutputSampleRate = settings[MigrationSettingsEnum.WakeWordAudioOutputSampleRate.name, ""]
        settings.remove(MigrationSettingsEnum.WakeWordAudioOutputSampleRate.name)

        wakeWordPorcupineLanguage = settings[MigrationSettingsEnum.WakeWordPorcupineLanguage.name, ""]
        settings.remove(MigrationSettingsEnum.WakeWordPorcupineLanguage.name)
        dialogManagementOption = settings[MigrationSettingsEnum.DialogManagementOption.name, ""]
        settings.remove(MigrationSettingsEnum.DialogManagementOption.name)
        intentRecognitionOption = settings[MigrationSettingsEnum.IntentRecognitionOption.name, ""]
        settings.remove(MigrationSettingsEnum.IntentRecognitionOption.name)

        textToSpeechOption = settings[MigrationSettingsEnum.TextToSpeechOption.name, ""]
        settings.remove(MigrationSettingsEnum.TextToSpeechOption.name)
        audioPlayingOption = settings[MigrationSettingsEnum.AudioPlayingOption.name, ""]
        settings.remove(MigrationSettingsEnum.AudioPlayingOption.name)
        audioOutputOption = settings[MigrationSettingsEnum.AudioOutputOption.name, ""]
        settings.remove(MigrationSettingsEnum.AudioOutputOption.name)
        speechToTextOption = settings[MigrationSettingsEnum.SpeechToTextOption.name, ""]
        settings.remove(MigrationSettingsEnum.SpeechToTextOption.name)

        speechToTextAudioRecorderChannel = settings[MigrationSettingsEnum.SpeechToTextAudioRecorderChannel.name, ""]
        settings.remove(MigrationSettingsEnum.SpeechToTextAudioRecorderChannel.name)
        speechToTextAudioRecorderEncoding = settings[MigrationSettingsEnum.SpeechToTextAudioRecorderEncoding.name, ""]
        settings.remove(MigrationSettingsEnum.SpeechToTextAudioRecorderEncoding.name)
        speechToTextAudioRecorderSampleRate = settings[MigrationSettingsEnum.SpeechToTextAudioRecorderSampleRate.name, ""]
        settings.remove(MigrationSettingsEnum.SpeechToTextAudioRecorderSampleRate.name)

        speechToTextAudioOutputChannel = settings[MigrationSettingsEnum.SpeechToTextAudioOutputChannel.name, ""]
        settings.remove(MigrationSettingsEnum.SpeechToTextAudioOutputChannel.name)
        speechToTextAudioOutputEncoding = settings[MigrationSettingsEnum.SpeechToTextAudioOutputEncoding.name, ""]
        settings.remove(MigrationSettingsEnum.SpeechToTextAudioOutputEncoding.name)
        speechToTextAudioOutputSampleRate = settings[MigrationSettingsEnum.SpeechToTextAudioOutputSampleRate.name, ""]
        settings.remove(MigrationSettingsEnum.SpeechToTextAudioOutputSampleRate.name)

        intentHandlingOption = settings[MigrationSettingsEnum.IntentHandlingOption.name, ""]
        settings.remove(MigrationSettingsEnum.IntentHandlingOption.name)
        intentHandlingHomeAssistantOption = settings[MigrationSettingsEnum.IsIntentHandlingHassEvent.name, ""]
        settings.remove(MigrationSettingsEnum.IsIntentHandlingHassEvent.name)
        voiceActivityDetectionOption = settings[MigrationSettingsEnum.VoiceActivityDetectionOption.name, ""]
        settings.remove(MigrationSettingsEnum.VoiceActivityDetectionOption.name)

        languageType = settings[MigrationSettingsEnum.LanguageOption.name, ""]
        settings.remove(MigrationSettingsEnum.LanguageOption.name)

        themeType = settings[MigrationSettingsEnum.ThemeOption.name, ""]
        settings.remove(MigrationSettingsEnum.ThemeOption.name)
        microphoneOverlaySizeOption = settings[MigrationSettingsEnum.MicrophoneOverlaySize.name, ""]
        settings.remove(MigrationSettingsEnum.MicrophoneOverlaySize.name)
        soundIndicationOutputOption = settings[MigrationSettingsEnum.SoundIndicationOutput.name, ""]
        settings.remove(MigrationSettingsEnum.SoundIndicationOutput.name)

        logLevel = settings[MigrationSettingsEnum.LogLevel.name, ""]
        settings.remove(MigrationSettingsEnum.LogLevel.name)
        audioFocusOption = settings[MigrationSettingsEnum.AudioFocusOption.name, ""]
        settings.remove(MigrationSettingsEnum.AudioFocusOption.name)
    }


    override fun migrate() {/*
        settings.encodeValue(WakeWordOption.serializer(), MigrationSettingsEnum.WakeWordOption.name,
            runCatching { WakeWordOption.valueOf(wakeWordOption) }.getOrNull() ?: WakeWordOption.Disabled
        )

        settings.encodeValue(AudioFormatChannelType.serializer(), MigrationSettingsEnum.WakeWordAudioRecorderChannel.name,
            runCatching { AudioFormatChannelType.valueOf(wakeWordAudioRecorderChannel) }.getOrNull() ?: AudioFormatChannelType.default
        )
        settings.encodeValue(AudioFormatEncodingType.serializer(), MigrationSettingsEnum.WakeWordAudioRecorderEncoding.name,
            runCatching { AudioFormatEncodingType.valueOf(wakeWordAudioRecorderEncoding) }.getOrNull() ?: AudioFormatEncodingType.default
        )
        settings.encodeValue(AudioFormatSampleRateType.serializer(), MigrationSettingsEnum.WakeWordAudioRecorderSampleRate.name,
            runCatching { AudioFormatSampleRateType.valueOf(wakeWordAudioRecorderSampleRate) }.getOrNull() ?: AudioFormatSampleRateType.default
        )


        settings.encodeValue(AudioFormatChannelType.serializer(), MigrationSettingsEnum.WakeWordAudioOutputChannel.name,
            runCatching { AudioFormatChannelType.valueOf(wakeWordAudioOutputChannel) }.getOrNull() ?: AudioFormatChannelType.default
        )
        settings.encodeValue(AudioFormatEncodingType.serializer(), MigrationSettingsEnum.WakeWordAudioOutputEncoding.name,
            runCatching { AudioFormatEncodingType.valueOf(wakeWordAudioOutputEncoding) }.getOrNull() ?: AudioFormatEncodingType.default
        )
        settings.encodeValue(AudioFormatSampleRateType.serializer(), MigrationSettingsEnum.WakeWordAudioOutputSampleRate.name,
            runCatching { AudioFormatSampleRateType.valueOf(wakeWordAudioOutputSampleRate) }.getOrNull() ?: AudioFormatSampleRateType.default
        )


        settings.encodeValue(PorcupineLanguageOption.serializer(), MigrationSettingsEnum.WakeWordPorcupineLanguage.name,
            runCatching { PorcupineLanguageOption.valueOf(wakeWordPorcupineLanguage) }.getOrNull() ?: PorcupineLanguageOption.EN
        )

        settings.encodeValue(DialogManagementOption.serializer(), MigrationSettingsEnum.DialogManagementOption.name,
            runCatching { DialogManagementOption.valueOf(dialogManagementOption) }.getOrNull() ?: DialogManagementOption.Local
        )

        settings.encodeValue(IntentRecognitionOption.serializer(), MigrationSettingsEnum.IntentRecognitionOption.name,
            runCatching { IntentRecognitionOption.valueOf(intentRecognitionOption) }.getOrNull() ?: IntentRecognitionOption.Disabled
        )

        settings.encodeValue(TextToSpeechOption.serializer(), MigrationSettingsEnum.TextToSpeechOption.name,
            runCatching { TextToSpeechOption.valueOf(textToSpeechOption) }.getOrNull() ?: TextToSpeechOption.Disabled
        )

        settings.encodeValue(AudioPlayingOption.serializer(), MigrationSettingsEnum.AudioPlayingOption.name,
            runCatching { AudioPlayingOption.valueOf(audioPlayingOption) }.getOrNull() ?: AudioPlayingOption.Local
        )
        settings.encodeValue(AudioOutputOption.serializer(), MigrationSettingsEnum.AudioOutputOption.name,
            runCatching { AudioOutputOption.valueOf(audioOutputOption) }.getOrNull() ?: AudioOutputOption.Sound
        )

        settings.encodeValue(SpeechToTextOption.serializer(), MigrationSettingsEnum.SpeechToTextOption.name,
            runCatching { SpeechToTextOption.valueOf(speechToTextOption) }.getOrNull() ?: SpeechToTextOption.Disabled
        )

        settings.encodeValue(AudioFormatChannelType.serializer(), MigrationSettingsEnum.SpeechToTextAudioRecorderChannel.name,
            runCatching { AudioFormatChannelType.valueOf(speechToTextAudioRecorderChannel) }.getOrNull() ?: AudioFormatChannelType.default
        )
        settings.encodeValue(AudioFormatEncodingType.serializer(), MigrationSettingsEnum.SpeechToTextAudioRecorderEncoding.name,
            runCatching { AudioFormatEncodingType.valueOf(speechToTextAudioRecorderEncoding) }.getOrNull() ?: AudioFormatEncodingType.default
        )
        settings.encodeValue(AudioFormatSampleRateType.serializer(), MigrationSettingsEnum.SpeechToTextAudioRecorderSampleRate.name,
            runCatching { AudioFormatSampleRateType.valueOf(speechToTextAudioRecorderSampleRate) }.getOrNull() ?: AudioFormatSampleRateType.default
        )

        settings.encodeValue(AudioFormatChannelType.serializer(), MigrationSettingsEnum.SpeechToTextAudioOutputChannel.name,
            runCatching { AudioFormatChannelType.valueOf(speechToTextAudioOutputChannel) }.getOrNull() ?: AudioFormatChannelType.default
        )
        settings.encodeValue(AudioFormatEncodingType.serializer(), MigrationSettingsEnum.SpeechToTextAudioOutputEncoding.name,
            runCatching { AudioFormatEncodingType.valueOf(speechToTextAudioOutputEncoding) }.getOrNull() ?: AudioFormatEncodingType.default
        )
        settings.encodeValue(AudioFormatSampleRateType.serializer(), MigrationSettingsEnum.SpeechToTextAudioOutputSampleRate.name,
            runCatching { AudioFormatSampleRateType.valueOf(speechToTextAudioOutputSampleRate) }.getOrNull() ?: AudioFormatSampleRateType.default
        )


        settings.encodeValue(IntentHandlingOption.serializer(), MigrationSettingsEnum.IntentHandlingOption.name,
            runCatching { IntentHandlingOption.valueOf(intentHandlingOption) }.getOrNull() ?: IntentHandlingOption.Disabled
        )
        settings.encodeValue(HomeAssistantIntentHandlingOption.serializer(), MigrationSettingsEnum.IsIntentHandlingHassEvent.name,
            runCatching { HomeAssistantIntentHandlingOption.valueOf(intentHandlingHomeAssistantOption) }.getOrNull() ?: HomeAssistantIntentHandlingOption.Intent
        )
        settings.encodeValue(VoiceActivityDetectionOption.serializer(), MigrationSettingsEnum.VoiceActivityDetectionOption.name,
            runCatching { VoiceActivityDetectionOption.valueOf(voiceActivityDetectionOption) }.getOrNull() ?: VoiceActivityDetectionOption.Disabled
        )



        settings.encodeValue(LanguageType.serializer(), MigrationSettingsEnum.LanguageOption.name,
            runCatching { LanguageType.valueOf(languageType) }.getOrNull() ?: get<ILanguageUtils>().getDeviceLanguage()
        )
        settings.encodeValue(ThemeType.serializer(), MigrationSettingsEnum.ThemeOption.name,
            runCatching { ThemeType.valueOf(themeType) }.getOrNull() ?: ThemeType.System
        )

        settings.encodeValue(MicrophoneOverlaySizeOption.serializer(), MigrationSettingsEnum.MicrophoneOverlaySize.name,
            runCatching { MicrophoneOverlaySizeOption.valueOf(microphoneOverlaySizeOption) }.getOrNull() ?: MicrophoneOverlaySizeOption.Disabled
        )
        settings.encodeValue(AudioOutputOption.serializer(), MigrationSettingsEnum.SoundIndicationOutput.name,
            runCatching { AudioOutputOption.valueOf(soundIndicationOutputOption) }.getOrNull() ?: AudioOutputOption.Notification
        )

        settings.encodeValue(LogLevel.serializer(), MigrationSettingsEnum.LogLevel.name,
            runCatching { LogLevel.valueOf(logLevel) }.getOrNull() ?: LogLevel.Debug
        )
        settings.encodeValue(AudioFocusOption.serializer(), MigrationSettingsEnum.AudioFocusOption.name,
            runCatching { AudioFocusOption.valueOf(audioFocusOption) }.getOrNull() ?: AudioFocusOption.Disabled
        )
*/
    }

}