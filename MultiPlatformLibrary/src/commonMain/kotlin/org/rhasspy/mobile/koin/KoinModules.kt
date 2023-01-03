package org.rhasspy.mobile.koin

import org.koin.dsl.module
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.middleware.ServiceMiddleware
import org.rhasspy.mobile.middleware.ServiceTestMiddleware
import org.rhasspy.mobile.nativeutils.AudioRecorder
import org.rhasspy.mobile.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.services.audioplaying.AudioPlayingServiceParams
import org.rhasspy.mobile.services.dialog.DialogManagerService
import org.rhasspy.mobile.services.dialog.DialogManagerServiceParams
import org.rhasspy.mobile.services.homeassistant.HomeAssistantService
import org.rhasspy.mobile.services.homeassistant.HomeAssistantServiceParams
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.services.indication.IndicationService
import org.rhasspy.mobile.services.intenthandling.IntentHandlingService
import org.rhasspy.mobile.services.intenthandling.IntentHandlingServiceParams
import org.rhasspy.mobile.services.intentrecognition.IntentRecognitionService
import org.rhasspy.mobile.services.intentrecognition.IntentRecognitionServiceParams
import org.rhasspy.mobile.services.localaudio.LocalAudioService
import org.rhasspy.mobile.services.localaudio.LocalAudioServiceParams
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.mqtt.MqttServiceParams
import org.rhasspy.mobile.services.recording.RecordingService
import org.rhasspy.mobile.services.settings.AppSettingsService
import org.rhasspy.mobile.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.services.speechtotext.SpeechToTextServiceParams
import org.rhasspy.mobile.services.texttospeech.TextToSpeechService
import org.rhasspy.mobile.services.texttospeech.TextToSpeechServiceParams
import org.rhasspy.mobile.services.udp.UdpService
import org.rhasspy.mobile.services.udp.UdpServiceParams
import org.rhasspy.mobile.services.wakeword.WakeWordService
import org.rhasspy.mobile.services.wakeword.WakeWordServiceParams
import org.rhasspy.mobile.services.webserver.WebServerService
import org.rhasspy.mobile.services.webserver.WebServerServiceParams
import org.rhasspy.mobile.viewmodel.AppViewModel
import org.rhasspy.mobile.viewmodel.configuration.*
import org.rhasspy.mobile.viewmodel.configuration.test.*
import org.rhasspy.mobile.viewmodel.overlay.IndicationOverlayViewModel
import org.rhasspy.mobile.viewmodel.overlay.MicrophoneOverlayViewModel
import org.rhasspy.mobile.viewmodel.screens.*
import org.rhasspy.mobile.viewmodel.settings.*
import org.rhasspy.mobile.viewmodel.settings.sound.ErrorIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.sound.RecordedIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.sound.WakeIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.widget.MicrophoneWidgetViewModel


val serviceModule = module {
    closeableSingle { LocalAudioService() }
    closeableSingle { AudioPlayingService() }
    closeableSingle { IntentHandlingService() }
    closeableSingle { IntentRecognitionService() }
    closeableSingle { SpeechToTextService() }
    closeableSingle { TextToSpeechService() }
    closeableSingle { MqttService() }
    closeableSingle { HttpClientService() }
    closeableSingle { WebServerService() }
    closeableSingle { UdpService() }
    closeableSingle { HomeAssistantService() }
    closeableSingle { RecordingService() }
    closeableSingle { WakeWordService() }
    closeableSingle { DialogManagerService() }
    closeableSingle { AppSettingsService() }
    closeableSingle { IndicationService() }

    closeableSingle { params -> createServiceMiddleware(params.getOrNull() ?: false) }

    single { params -> params.getOrNull<LocalAudioServiceParams>() ?: LocalAudioServiceParams() }
    single { params -> params.getOrNull<AudioPlayingServiceParams>() ?: AudioPlayingServiceParams() }
    single { params -> params.getOrNull<IntentHandlingServiceParams>() ?: IntentHandlingServiceParams() }
    single { params -> params.getOrNull<IntentRecognitionServiceParams>() ?: IntentRecognitionServiceParams() }
    single { params -> params.getOrNull<SpeechToTextServiceParams>() ?: SpeechToTextServiceParams() }
    single { params -> params.getOrNull<TextToSpeechServiceParams>() ?: TextToSpeechServiceParams() }
    single { params -> params.getOrNull<MqttServiceParams>() ?: MqttServiceParams() }
    single { params -> params.getOrNull<HttpClientServiceParams>() ?: HttpClientServiceParams() }
    single { params -> params.getOrNull<WebServerServiceParams>() ?: WebServerServiceParams() }
    single { params -> params.getOrNull<UdpServiceParams>() ?: UdpServiceParams() }
    single { params -> params.getOrNull<HomeAssistantServiceParams>() ?: HomeAssistantServiceParams() }
    single { params -> params.getOrNull<WakeWordServiceParams>() ?: WakeWordServiceParams() }
    single { params -> params.getOrNull<DialogManagerServiceParams>() ?: DialogManagerServiceParams() }

    closeableSingle { AudioPlayingConfigurationTest() }
    closeableSingle { DialogManagementConfigurationTest() }
    closeableSingle { IntentHandlingConfigurationTest() }
    closeableSingle { IntentRecognitionConfigurationTest() }
    closeableSingle { MqttConfigurationTest() }
    closeableSingle { RemoteHermesHttpConfigurationTest() }
    closeableSingle { SpeechToTextConfigurationTest() }
    closeableSingle { TextToSpeechConfigurationTest() }
    closeableSingle { WakeWordConfigurationTest() }
    closeableSingle { WebServerConfigurationTest() }
}

val viewModelModule = module {
    single { AppViewModel() }
    single { HomeScreenViewModel() }
    single { MicrophoneWidgetViewModel() }
    single { ConfigurationScreenViewModel() }
    single { AudioPlayingConfigurationViewModel() }
    single { DialogManagementConfigurationViewModel() }
    single { IntentHandlingConfigurationViewModel() }
    single { IntentRecognitionConfigurationViewModel() }
    single { MqttConfigurationViewModel() }
    single { RemoteHermesHttpConfigurationViewModel() }
    single { SpeechToTextConfigurationViewModel() }
    single { TextToSpeechConfigurationViewModel() }
    single { WakeWordConfigurationViewModel() }
    single { WebServerConfigurationViewModel() }
    single { LogScreenViewModel() }
    single { SettingsScreenViewModel() }
    single { AboutScreenViewModel() }
    single { AutomaticSilenceDetectionSettingsViewModel() }
    single { BackgroundServiceSettingsViewModel() }
    single { DeviceSettingsSettingsViewModel() }
    single { IndicationSettingsViewModel() }
    single { WakeIndicationSoundSettingsViewModel() }
    single { RecordedIndicationSoundSettingsViewModel() }
    single { ErrorIndicationSoundSettingsViewModel() }
    single { LanguageSettingsViewModel() }
    single { LogSettingsViewModel() }
    single { MicrophoneOverlaySettingsViewModel() }
    single { SaveAndRestoreSettingsViewModel() }
    single { MicrophoneOverlayViewModel() }
    single { IndicationOverlayViewModel() }
}

val nativeModule = module {
    closeableSingle { AudioRecorder() }
}

fun createServiceMiddleware(isTest: Boolean): IServiceMiddleware {
    return when (isTest) {
        true -> ServiceTestMiddleware()
        false -> ServiceMiddleware()
    }
}