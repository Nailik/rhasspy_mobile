package org.rhasspy.mobile.settings

import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.connection.HttpConnectionData
import org.rhasspy.mobile.data.connection.LocalWebserverConnectionData
import org.rhasspy.mobile.data.connection.MqttConnectionData
import org.rhasspy.mobile.data.domain.*
import org.rhasspy.mobile.data.pipeline.PipelineData
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.*
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.platformspecific.features.FeatureAvailability
import kotlin.time.Duration.Companion.seconds

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
            isSSLVerificationDisabled = false,
        ),
        serializer = HttpConnectionData.serializer(),
    )

    val rhasspy3Connection = ISetting(
        key = SettingsEnum.Rhasspy3Connection,
        initial = HttpConnectionData(
            host = "",
            timeout = 30000L,
            bearerToken = "",
            isSSLVerificationDisabled = false,
        ),
        serializer = HttpConnectionData.serializer(),
    )

    val homeAssistantConnection = ISetting(
        key = SettingsEnum.HomeAssistantConnection,
        initial = HttpConnectionData(
            host = "",
            timeout = 30000L,
            bearerToken = "",
            isSSLVerificationDisabled = false,
        ),
        serializer = HttpConnectionData.serializer(),
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
            keystoreFile = null,
        ),
        serializer = MqttConnectionData.serializer(),
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
        serializer = LocalWebserverConnectionData.serializer(),
    )

    val micDomainData = ISetting(
        key = SettingsEnum.MicDomain,
        initial = MicDomainData(
            audioInputSource = AudioSourceType.default,
            audioInputChannel = AudioFormatChannelType.default,
            audioInputEncoding = AudioFormatEncodingType.default,
            audioInputSampleRate = AudioFormatSampleRateType.default,
            audioOutputChannel = AudioFormatChannelType.default,
            audioOutputEncoding = AudioFormatEncodingType.default,
            audioOutputSampleRate = AudioFormatSampleRateType.default,
            isUseLoudnessEnhancer = false,
            gainControl = 0,
            isPauseRecordingOnMediaPlayback = FeatureAvailability.isPauseRecordingOnPlaybackFeatureEnabled,
        ),
        serializer = MicDomainData.serializer(),
    )

    val vadDomainData = ISetting(
        key = SettingsEnum.VoiceActivityDetectionDomain,
        initial = VadDomainData(
            option = VoiceActivityDetectionOption.Disabled,
            timeout = 20.seconds,
            automaticSilenceDetectionAudioLevel = 40f,
            automaticSilenceDetectionTime = 2.seconds,
            automaticSilenceDetectionMinimumTime = 2.seconds,
        ),
        serializer = VadDomainData.serializer(),
    )

    val wakeDomainData = ISetting(
        key = SettingsEnum.WakeDomain,
        initial = WakeDomainData(
            wakeWordOption = WakeWordOption.Disabled,
            wakeWordPorcupineAccessToken = "",
            wakeWordPorcupineKeywordDefaultOptions = PorcupineKeywordOption.entries.map { PorcupineDefaultKeyword(it, false, 0.5) },
            wakeWordPorcupineKeywordCustomOptions = emptyList(),
            wakeWordPorcupineLanguage = PorcupineLanguageOption.EN,
            wakeWordUdpOutputHost = "",
            wakeWordUdpOutputPort = 20000
        ),
        serializer = WakeDomainData.serializer()
    )

    val asrDomainData = ISetting(
        key = SettingsEnum.AsrDomain,
        initial = AsrDomainData(
            option = SpeechToTextOption.Disabled,
            isUseSpeechToTextMqttSilenceDetection = true,
            mqttTimeout = 20.seconds,
        ),
        serializer = AsrDomainData.serializer(),
    )

    val handleDomainData = ISetting(
        key = SettingsEnum.HandleDomain,
        initial = HandleDomainData(
            option = IntentHandlingOption.Disabled,
            homeAssistantIntentHandlingOption = HomeAssistantIntentHandlingOption.Intent,
            homeAssistantEventTimeout = 20.seconds,
        ),
        serializer = HandleDomainData.serializer(),
    )

    val intentDomainData = ISetting(
        key = SettingsEnum.IntentDomain,
        initial = IntentDomainData(
            option = IntentRecognitionOption.Disabled,
            isRhasspy2HermesHttpHandleWithRecognition = false,
            rhasspy2HermesHttpHandleTimeout = 20.seconds,
            rhasspy2HermesMqttHandleTimeout = 20.seconds,
        ),
        serializer = IntentDomainData.serializer(),
    )

    val sndDomainData = ISetting(
        key = SettingsEnum.SndDomain,
        initial = SndDomainData(
            option = AudioPlayingOption.Local,
            localOutputOption = AudioOutputOption.Sound,
            mqttSiteId = "",
            audioTimeout = 20.seconds,
            rhasspy2HermesMqttTimeout = 20.seconds,
        ),
        serializer = SndDomainData.serializer(),
    )

    val ttsDomainData = ISetting(
        key = SettingsEnum.TtsDomain,
        initial = TtsDomainData(
            option = TextToSpeechOption.Disabled,
            rhasspy2HermesMqttTimeout = 20.seconds,
        ),
        serializer = TtsDomainData.serializer(),
    )

    val pipelineData = ISetting(
        key = SettingsEnum.Pipeline,
        initial = PipelineData(
            option = DialogManagementOption.Local,
        ),
        serializer = PipelineData.serializer(),
    )

}