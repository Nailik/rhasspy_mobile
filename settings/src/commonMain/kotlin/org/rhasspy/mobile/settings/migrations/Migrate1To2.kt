package org.rhasspy.mobile.settings.migrations

import com.russhwolf.settings.get
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.data.log.LogLevel
import org.rhasspy.mobile.data.service.option.*
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.data.theme.ThemeType
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
        ConfigurationSetting.wakeWordOption.apply {
            value = runCatching { WakeWordOption.valueOf(wakeWordOption) }.getOrNull() ?: initial
        }

        ConfigurationSetting.wakeWordAudioRecorderChannel.apply {
            value = runCatching { AudioFormatChannelType.valueOf(wakeWordAudioRecorderChannel) }.getOrNull() ?: initial
        }
        ConfigurationSetting.wakeWordAudioRecorderEncoding.apply {
            value = runCatching { AudioFormatEncodingType.valueOf(wakeWordAudioRecorderEncoding) }.getOrNull() ?: initial
        }
        ConfigurationSetting.wakeWordAudioRecorderSampleRate.apply {
            value = runCatching { AudioFormatSampleRateType.valueOf(wakeWordAudioRecorderSampleRate) }.getOrNull() ?: initial
        }


        ConfigurationSetting.wakeWordAudioOutputChannel.apply {
            value = runCatching { AudioFormatChannelType.valueOf(wakeWordAudioOutputChannel) }.getOrNull() ?: initial
        }
        ConfigurationSetting.wakeWordAudioOutputEncoding.apply {
            value = runCatching { AudioFormatEncodingType.valueOf(wakeWordAudioOutputEncoding) }.getOrNull() ?: initial
        }
        ConfigurationSetting.wakeWordAudioOutputSampleRate.apply {
            value = runCatching { AudioFormatSampleRateType.valueOf(wakeWordAudioOutputSampleRate) }.getOrNull() ?: initial
        }


        ConfigurationSetting.wakeWordPorcupineLanguage.apply {
            value = runCatching { PorcupineLanguageOption.valueOf(wakeWordPorcupineLanguage) }.getOrNull() ?: initial
        }
        ConfigurationSetting.dialogManagementOption.apply {
            value = runCatching { DialogManagementOption.valueOf(dialogManagementOption) }.getOrNull() ?: initial
        }
        ConfigurationSetting.intentRecognitionOption.apply {
            value = runCatching { IntentRecognitionOption.valueOf(intentRecognitionOption) }.getOrNull() ?: initial
        }


        ConfigurationSetting.textToSpeechOption.apply {
            value = runCatching { TextToSpeechOption.valueOf(textToSpeechOption) }.getOrNull() ?: initial
        }
        ConfigurationSetting.audioPlayingOption.apply {
            value = runCatching { AudioPlayingOption.valueOf(audioPlayingOption) }.getOrNull() ?: initial
        }
        ConfigurationSetting.audioOutputOption.apply {
            value = runCatching { AudioOutputOption.valueOf(audioOutputOption) }.getOrNull() ?: initial
        }
        ConfigurationSetting.speechToTextOption.apply {
            value = runCatching { SpeechToTextOption.valueOf(speechToTextOption) }.getOrNull() ?: initial
        }

        ConfigurationSetting.speechToTextAudioRecorderChannel.apply {
            value = runCatching { AudioFormatChannelType.valueOf(speechToTextAudioRecorderChannel) }.getOrNull() ?: initial
        }
        ConfigurationSetting.speechToTextAudioRecorderEncoding.apply {
            value = runCatching { AudioFormatEncodingType.valueOf(speechToTextAudioRecorderEncoding) }.getOrNull() ?: initial
        }
        ConfigurationSetting.speechToTextAudioRecorderSampleRate.apply {
            value = runCatching { AudioFormatSampleRateType.valueOf(speechToTextAudioRecorderSampleRate) }.getOrNull() ?: initial
        }


        ConfigurationSetting.speechToTextAudioOutputChannel.apply {
            value = runCatching { AudioFormatChannelType.valueOf(speechToTextAudioOutputChannel) }.getOrNull() ?: initial
        }
        ConfigurationSetting.speechToTextAudioOutputEncoding.apply {
            value = runCatching { AudioFormatEncodingType.valueOf(speechToTextAudioOutputEncoding) }.getOrNull() ?: initial
        }
        ConfigurationSetting.speechToTextAudioOutputSampleRate.apply {
            value = runCatching { AudioFormatSampleRateType.valueOf(speechToTextAudioOutputSampleRate) }.getOrNull() ?: initial
        }

        ConfigurationSetting.intentHandlingOption.apply {
            value = runCatching { IntentHandlingOption.valueOf(intentHandlingOption) }.getOrNull() ?: initial
        }
        ConfigurationSetting.intentHandlingHomeAssistantOption.apply {
            value = runCatching { HomeAssistantIntentHandlingOption.valueOf(intentHandlingHomeAssistantOption) }.getOrNull() ?: initial
        }
        ConfigurationSetting.voiceActivityDetectionOption.apply {
            value = runCatching { VoiceActivityDetectionOption.valueOf(voiceActivityDetectionOption) }.getOrNull() ?: initial
        }

        AppSetting.languageType.apply {
            value = runCatching { LanguageType.valueOf(languageType) }.getOrNull() ?: initial
        }
        AppSetting.themeType.apply {
            value = runCatching { ThemeType.valueOf(themeType) }.getOrNull() ?: initial
        }
        AppSetting.microphoneOverlaySizeOption.apply {
            value = runCatching { MicrophoneOverlaySizeOption.valueOf(microphoneOverlaySizeOption) }.getOrNull() ?: initial
        }
        AppSetting.soundIndicationOutputOption.apply {
            value = runCatching { AudioOutputOption.valueOf(soundIndicationOutputOption) }.getOrNull() ?: initial
        }

        AppSetting.logLevel.apply {
            value = runCatching { LogLevel.valueOf(logLevel) }.getOrNull() ?: initial
        }
        AppSetting.audioFocusOption.apply {
            value = runCatching { AudioFocusOption.valueOf(audioFocusOption) }.getOrNull() ?: initial
        }
    }

}