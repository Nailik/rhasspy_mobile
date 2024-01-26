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

    override fun preMigrate() {
        val wakeWordOption = settings[SettingsEnum.WakeWordOption.name, ""]
        settings.remove(SettingsEnum.WakeWordOption.name)

        val wakeWordAudioRecorderChannel = settings[SettingsEnum.WakeWordAudioRecorderChannel.name, ""]
        settings.remove(SettingsEnum.WakeWordAudioRecorderChannel.name)
        val wakeWordAudioRecorderEncoding = settings[SettingsEnum.WakeWordAudioRecorderEncoding.name, ""]
        settings.remove(SettingsEnum.WakeWordAudioRecorderEncoding.name)
        val wakeWordAudioRecorderSampleRate = settings[SettingsEnum.WakeWordAudioRecorderSampleRate.name, ""]
        settings.remove(SettingsEnum.WakeWordAudioRecorderSampleRate.name)

        val wakeWordAudioOutputChannel = settings[SettingsEnum.WakeWordAudioOutputChannel.name, ""]
        settings.remove(SettingsEnum.WakeWordAudioOutputChannel.name)
        val wakeWordAudioOutputEncoding = settings[SettingsEnum.WakeWordAudioROutputEncoding.name, ""]
        settings.remove(SettingsEnum.WakeWordAudioROutputEncoding.name)
        val wakeWordAudioOutputSampleRate = settings[SettingsEnum.WakeWordAudioOutputSampleRate.name, ""]
        settings.remove(SettingsEnum.WakeWordAudioOutputSampleRate.name)

        val wakeWordPorcupineLanguage = settings[SettingsEnum.WakeWordPorcupineLanguage.name, ""]
        settings.remove(SettingsEnum.WakeWordPorcupineLanguage.name)
        val dialogManagementOption = settings[SettingsEnum.DialogManagementOption.name, ""]
        settings.remove(SettingsEnum.DialogManagementOption.name)
        val intentRecognitionOption = settings[SettingsEnum.IntentRecognitionOption.name, ""]
        settings.remove(SettingsEnum.IntentRecognitionOption.name)

        val textToSpeechOption = settings[SettingsEnum.TextToSpeechOption.name, ""]
        settings.remove(SettingsEnum.TextToSpeechOption.name)
        val audioPlayingOption = settings[SettingsEnum.AudioPlayingOption.name, ""]
        settings.remove(SettingsEnum.AudioPlayingOption.name)
        val audioOutputOption = settings[SettingsEnum.AudioOutputOption.name, ""]
        settings.remove(SettingsEnum.AudioOutputOption.name)
        val speechToTextOption = settings[SettingsEnum.SpeechToTextOption.name, ""]
        settings.remove(SettingsEnum.SpeechToTextOption.name)

        val speechToTextAudioRecorderChannel = settings[SettingsEnum.SpeechToTextAudioRecorderChannel.name, ""]
        settings.remove(SettingsEnum.SpeechToTextAudioRecorderChannel.name)
        val speechToTextAudioRecorderEncoding = settings[SettingsEnum.SpeechToTextAudioRecorderEncoding.name, ""]
        settings.remove(SettingsEnum.SpeechToTextAudioRecorderEncoding.name)
        val speechToTextAudioRecorderSampleRate = settings[SettingsEnum.SpeechToTextAudioRecorderSampleRate.name, ""]
        settings.remove(SettingsEnum.SpeechToTextAudioRecorderSampleRate.name)

        val speechToTextAudioOutputChannel = settings[SettingsEnum.SpeechToTextAudioOutputChannel.name, ""]
        settings.remove(SettingsEnum.SpeechToTextAudioOutputChannel.name)
        val speechToTextAudioOutputEncoding = settings[SettingsEnum.SpeechToTextAudioOutputEncoding.name, ""]
        settings.remove(SettingsEnum.SpeechToTextAudioOutputEncoding.name)
        val speechToTextAudioOutputSampleRate = settings[SettingsEnum.SpeechToTextAudioOutputSampleRate.name, ""]
        settings.remove(SettingsEnum.SpeechToTextAudioOutputSampleRate.name)

        val intentHandlingOption = settings[SettingsEnum.IntentHandlingOption.name, ""]
        settings.remove(SettingsEnum.IntentHandlingOption.name)
        val intentHandlingHomeAssistantOption = settings[SettingsEnum.IsIntentHandlingHassEvent.name, ""]
        settings.remove(SettingsEnum.IsIntentHandlingHassEvent.name)
        val voiceActivityDetectionOption = settings[SettingsEnum.VoiceActivityDetectionOption.name, ""]
        settings.remove(SettingsEnum.VoiceActivityDetectionOption.name)

        val languageType = settings[SettingsEnum.LanguageOption.name, ""]
        settings.remove(SettingsEnum.LanguageOption.name)

        val themeType = settings[SettingsEnum.ThemeOption.name, ""]
        settings.remove(SettingsEnum.ThemeOption.name)
        val microphoneOverlaySizeOption = settings[SettingsEnum.MicrophoneOverlaySize.name, ""]
        settings.remove(SettingsEnum.MicrophoneOverlaySize.name)
        val soundIndicationOutputOption = settings[SettingsEnum.SoundIndicationOutput.name, ""]
        settings.remove(SettingsEnum.SoundIndicationOutput.name)

        val logLevel = settings[SettingsEnum.LogLevel.name, ""]
        settings.remove(SettingsEnum.LogLevel.name)
        val audioFocusOption = settings[SettingsEnum.AudioFocusOption.name, ""]
        settings.remove(SettingsEnum.AudioFocusOption.name)

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


    override fun migrate() {}

}