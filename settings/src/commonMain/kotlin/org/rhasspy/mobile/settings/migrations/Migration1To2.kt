package org.rhasspy.mobile.settings.migrations

import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.domain.AudioInputDomainData
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.ISetting

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

    override fun migrate() {
        ConfigurationSetting.audioInputDomainData.value = AudioInputDomainData(
            audioInputChannel = speechToTextAudioRecorderChannel.value,
            audioInputEncoding = speechToTextAudioRecorderEncoding.value,
            audioInputSampleRate = speechToTextAudioRecorderSampleRate.value,
            audioOutputChannel = speechToTextAudioOutputChannel.value,
            audioOutputEncoding = speechToTextAudioOutputEncoding.value,
            audioOutputSampleRate = speechToTextAudioOutputSampleRate.value,
            isUseAutomaticGainControl = false
        )

        speechToTextAudioRecorderChannel.delete()
        speechToTextAudioRecorderEncoding.delete()
        speechToTextAudioRecorderSampleRate.delete()

        speechToTextAudioOutputChannel.delete()
        speechToTextAudioOutputEncoding.delete()
        speechToTextAudioOutputSampleRate.delete()

        wakeWordAudioRecorderChannel.delete()
        wakeWordAudioRecorderEncoding.delete()
        wakeWordAudioRecorderSampleRate.delete()

        wakeWordAudioOutputChannel.delete()
        wakeWordAudioOutputEncoding.delete()
        wakeWordAudioOutputSampleRate.delete()
    }


}