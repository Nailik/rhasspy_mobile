@file:OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)

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
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.data.theme.ThemeType
import org.rhasspy.mobile.platformspecific.language.ILanguageUtils
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class Migrate1To2 : IMigration(1, 2) {

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
        wakeWordOption = settings[SettingsEnum.WakeWordOption.name, ""]
        settings.remove(SettingsEnum.WakeWordOption.name)

        wakeWordAudioRecorderChannel = settings[SettingsEnum.WakeWordAudioRecorderChannel.name, ""]
        settings.remove(SettingsEnum.WakeWordAudioRecorderChannel.name)
        wakeWordAudioRecorderEncoding = settings[SettingsEnum.WakeWordAudioRecorderEncoding.name, ""]
        settings.remove(SettingsEnum.WakeWordAudioRecorderEncoding.name)
        wakeWordAudioRecorderSampleRate = settings[SettingsEnum.WakeWordAudioRecorderSampleRate.name, ""]
        settings.remove(SettingsEnum.WakeWordAudioRecorderSampleRate.name)

        wakeWordAudioOutputChannel = settings[SettingsEnum.WakeWordAudioOutputChannel.name, ""]
        settings.remove(SettingsEnum.WakeWordAudioOutputChannel.name)
        wakeWordAudioOutputEncoding = settings["WakeWordAudioROutputEncoding", ""]
        settings.remove("WakeWordAudioROutputEncoding")
        wakeWordAudioOutputSampleRate = settings[SettingsEnum.WakeWordAudioOutputSampleRate.name, ""]
        settings.remove(SettingsEnum.WakeWordAudioOutputSampleRate.name)

        wakeWordPorcupineLanguage = settings[SettingsEnum.WakeWordPorcupineLanguage.name, ""]
        settings.remove(SettingsEnum.WakeWordPorcupineLanguage.name)
        dialogManagementOption = settings[SettingsEnum.DialogManagementOption.name, ""]
        settings.remove(SettingsEnum.DialogManagementOption.name)
        intentRecognitionOption = settings[SettingsEnum.IntentRecognitionOption.name, ""]
        settings.remove(SettingsEnum.IntentRecognitionOption.name)

        textToSpeechOption = settings[SettingsEnum.TextToSpeechOption.name, ""]
        settings.remove(SettingsEnum.TextToSpeechOption.name)
        audioPlayingOption = settings[SettingsEnum.AudioPlayingOption.name, ""]
        settings.remove(SettingsEnum.AudioPlayingOption.name)
        audioOutputOption = settings[SettingsEnum.AudioOutputOption.name, ""]
        settings.remove(SettingsEnum.AudioOutputOption.name)
        speechToTextOption = settings[SettingsEnum.SpeechToTextOption.name, ""]
        settings.remove(SettingsEnum.SpeechToTextOption.name)

        speechToTextAudioRecorderChannel = settings[SettingsEnum.SpeechToTextAudioRecorderChannel.name, ""]
        settings.remove(SettingsEnum.SpeechToTextAudioRecorderChannel.name)
        speechToTextAudioRecorderEncoding = settings[SettingsEnum.SpeechToTextAudioRecorderEncoding.name, ""]
        settings.remove(SettingsEnum.SpeechToTextAudioRecorderEncoding.name)
        speechToTextAudioRecorderSampleRate = settings[SettingsEnum.SpeechToTextAudioRecorderSampleRate.name, ""]
        settings.remove(SettingsEnum.SpeechToTextAudioRecorderSampleRate.name)

        speechToTextAudioOutputChannel = settings[SettingsEnum.SpeechToTextAudioOutputChannel.name, ""]
        settings.remove(SettingsEnum.SpeechToTextAudioOutputChannel.name)
        speechToTextAudioOutputEncoding = settings[SettingsEnum.SpeechToTextAudioOutputEncoding.name, ""]
        settings.remove(SettingsEnum.SpeechToTextAudioOutputEncoding.name)
        speechToTextAudioOutputSampleRate = settings[SettingsEnum.SpeechToTextAudioOutputSampleRate.name, ""]
        settings.remove(SettingsEnum.SpeechToTextAudioOutputSampleRate.name)

        intentHandlingOption = settings[SettingsEnum.IntentHandlingOption.name, ""]
        settings.remove(SettingsEnum.IntentHandlingOption.name)
        intentHandlingHomeAssistantOption = settings[SettingsEnum.IsIntentHandlingHassEvent.name, ""]
        settings.remove(SettingsEnum.IsIntentHandlingHassEvent.name)
        voiceActivityDetectionOption = settings[SettingsEnum.VoiceActivityDetectionOption.name, ""]
        settings.remove(SettingsEnum.VoiceActivityDetectionOption.name)

        languageType = settings[SettingsEnum.LanguageOption.name, ""]
        settings.remove(SettingsEnum.LanguageOption.name)

        themeType = settings[SettingsEnum.ThemeOption.name, ""]
        settings.remove(SettingsEnum.ThemeOption.name)
        microphoneOverlaySizeOption = settings[SettingsEnum.MicrophoneOverlaySize.name, ""]
        settings.remove(SettingsEnum.MicrophoneOverlaySize.name)
        soundIndicationOutputOption = settings[SettingsEnum.SoundIndicationOutput.name, ""]
        settings.remove(SettingsEnum.SoundIndicationOutput.name)

        logLevel = settings[SettingsEnum.LogLevel.name, ""]
        settings.remove(SettingsEnum.LogLevel.name)
        audioFocusOption = settings[SettingsEnum.AudioFocusOption.name, ""]
        settings.remove(SettingsEnum.AudioFocusOption.name)
    }


    override fun migrate() {
        settings.encodeValue(WakeWordOption.serializer(), SettingsEnum.WakeWordOption.name,
            runCatching { WakeWordOption.valueOf(wakeWordOption) }.getOrNull() ?: WakeWordOption.Disabled
        )

        settings.encodeValue(AudioFormatChannelType.serializer(), SettingsEnum.WakeWordAudioRecorderChannel.name,
            runCatching { AudioFormatChannelType.valueOf(wakeWordAudioRecorderChannel) }.getOrNull() ?: AudioFormatChannelType.default
        )
        settings.encodeValue(AudioFormatEncodingType.serializer(), SettingsEnum.WakeWordAudioRecorderEncoding.name,
            runCatching { AudioFormatEncodingType.valueOf(wakeWordAudioRecorderEncoding) }.getOrNull() ?: AudioFormatEncodingType.default
        )
        settings.encodeValue(AudioFormatSampleRateType.serializer(), SettingsEnum.WakeWordAudioRecorderSampleRate.name,
            runCatching { AudioFormatSampleRateType.valueOf(wakeWordAudioRecorderSampleRate) }.getOrNull() ?: AudioFormatSampleRateType.default
        )


        settings.encodeValue(AudioFormatChannelType.serializer(), SettingsEnum.WakeWordAudioOutputChannel.name,
            runCatching { AudioFormatChannelType.valueOf(wakeWordAudioOutputChannel) }.getOrNull() ?: AudioFormatChannelType.default
        )
        settings.encodeValue(AudioFormatEncodingType.serializer(), SettingsEnum.WakeWordAudioOutputEncoding.name,
            runCatching { AudioFormatEncodingType.valueOf(wakeWordAudioOutputEncoding) }.getOrNull() ?: AudioFormatEncodingType.default
        )
        settings.encodeValue(AudioFormatSampleRateType.serializer(), SettingsEnum.WakeWordAudioOutputSampleRate.name,
            runCatching { AudioFormatSampleRateType.valueOf(wakeWordAudioOutputSampleRate) }.getOrNull() ?: AudioFormatSampleRateType.default
        )


        settings.encodeValue(PorcupineLanguageOption.serializer(), SettingsEnum.WakeWordPorcupineLanguage.name,
            runCatching { PorcupineLanguageOption.valueOf(wakeWordPorcupineLanguage) }.getOrNull() ?: PorcupineLanguageOption.EN
        )

        settings.encodeValue(DialogManagementOption.serializer(), SettingsEnum.DialogManagementOption.name,
            runCatching { DialogManagementOption.valueOf(dialogManagementOption) }.getOrNull() ?: DialogManagementOption.Local
        )

        settings.encodeValue(IntentRecognitionOption.serializer(), SettingsEnum.IntentRecognitionOption.name,
            runCatching { IntentRecognitionOption.valueOf(intentRecognitionOption) }.getOrNull() ?: IntentRecognitionOption.Disabled
        )

        settings.encodeValue(TextToSpeechOption.serializer(), SettingsEnum.TextToSpeechOption.name,
            runCatching { TextToSpeechOption.valueOf(textToSpeechOption) }.getOrNull() ?: TextToSpeechOption.Disabled
        )

        settings.encodeValue(AudioPlayingOption.serializer(), SettingsEnum.AudioPlayingOption.name,
            runCatching { AudioPlayingOption.valueOf(audioPlayingOption) }.getOrNull() ?: AudioPlayingOption.Local
        )
        settings.encodeValue(AudioOutputOption.serializer(), SettingsEnum.AudioOutputOption.name,
            runCatching { AudioOutputOption.valueOf(audioOutputOption) }.getOrNull() ?: AudioOutputOption.Sound
        )

        settings.encodeValue(SpeechToTextOption.serializer(), SettingsEnum.SpeechToTextOption.name,
            runCatching { SpeechToTextOption.valueOf(speechToTextOption) }.getOrNull() ?: SpeechToTextOption.Disabled
        )

        settings.encodeValue(AudioFormatChannelType.serializer(), SettingsEnum.SpeechToTextAudioRecorderChannel.name,
            runCatching { AudioFormatChannelType.valueOf(speechToTextAudioRecorderChannel) }.getOrNull() ?: AudioFormatChannelType.default
        )
        settings.encodeValue(AudioFormatEncodingType.serializer(), SettingsEnum.SpeechToTextAudioRecorderEncoding.name,
            runCatching { AudioFormatEncodingType.valueOf(speechToTextAudioRecorderEncoding) }.getOrNull() ?: AudioFormatEncodingType.default
        )
        settings.encodeValue(AudioFormatSampleRateType.serializer(), SettingsEnum.SpeechToTextAudioRecorderSampleRate.name,
            runCatching { AudioFormatSampleRateType.valueOf(speechToTextAudioRecorderSampleRate) }.getOrNull() ?: AudioFormatSampleRateType.default
        )

        settings.encodeValue(AudioFormatChannelType.serializer(), SettingsEnum.SpeechToTextAudioOutputChannel.name,
            runCatching { AudioFormatChannelType.valueOf(speechToTextAudioOutputChannel) }.getOrNull() ?: AudioFormatChannelType.default
        )
        settings.encodeValue(AudioFormatEncodingType.serializer(), SettingsEnum.SpeechToTextAudioOutputEncoding.name,
            runCatching { AudioFormatEncodingType.valueOf(speechToTextAudioOutputEncoding) }.getOrNull() ?: AudioFormatEncodingType.default
        )
        settings.encodeValue(AudioFormatSampleRateType.serializer(), SettingsEnum.SpeechToTextAudioOutputSampleRate.name,
            runCatching { AudioFormatSampleRateType.valueOf(speechToTextAudioOutputSampleRate) }.getOrNull() ?: AudioFormatSampleRateType.default
        )


        settings.encodeValue(IntentHandlingOption.serializer(), SettingsEnum.IntentHandlingOption.name,
            runCatching { IntentHandlingOption.valueOf(intentHandlingOption) }.getOrNull() ?: IntentHandlingOption.Disabled
        )
        settings.encodeValue(HomeAssistantIntentHandlingOption.serializer(), SettingsEnum.IsIntentHandlingHassEvent.name,
            runCatching { HomeAssistantIntentHandlingOption.valueOf(intentHandlingHomeAssistantOption) }.getOrNull() ?: HomeAssistantIntentHandlingOption.Intent
        )
        settings.encodeValue(VoiceActivityDetectionOption.serializer(), SettingsEnum.VoiceActivityDetectionOption.name,
            runCatching { VoiceActivityDetectionOption.valueOf(voiceActivityDetectionOption) }.getOrNull() ?: VoiceActivityDetectionOption.Disabled
        )



        settings.encodeValue(LanguageType.serializer(), SettingsEnum.LanguageOption.name,
            runCatching { LanguageType.valueOf(languageType) }.getOrNull() ?: get<ILanguageUtils>().getDeviceLanguage()
        )
        settings.encodeValue(ThemeType.serializer(), SettingsEnum.ThemeOption.name,
            runCatching { ThemeType.valueOf(themeType) }.getOrNull() ?: ThemeType.System
        )

        settings.encodeValue(MicrophoneOverlaySizeOption.serializer(), SettingsEnum.MicrophoneOverlaySize.name,
            runCatching { MicrophoneOverlaySizeOption.valueOf(microphoneOverlaySizeOption) }.getOrNull() ?: MicrophoneOverlaySizeOption.Disabled
        )
        settings.encodeValue(AudioOutputOption.serializer(), SettingsEnum.SoundIndicationOutput.name,
            runCatching { AudioOutputOption.valueOf(soundIndicationOutputOption) }.getOrNull() ?: AudioOutputOption.Notification
        )

        settings.encodeValue(LogLevel.serializer(), SettingsEnum.LogLevel.name,
            runCatching { LogLevel.valueOf(logLevel) }.getOrNull() ?: LogLevel.Debug
        )
        settings.encodeValue(AudioFocusOption.serializer(), SettingsEnum.AudioFocusOption.name,
            runCatching { AudioFocusOption.valueOf(audioFocusOption) }.getOrNull() ?: AudioFocusOption.Disabled
        )

    }

}