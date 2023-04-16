package org.rhasspy.mobile

import org.koin.dsl.module
import org.rhasspy.mobile.logic.closeableSingle
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingServiceParams
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceParams
import org.rhasspy.mobile.logic.services.homeassistant.HomeAssistantService
import org.rhasspy.mobile.logic.services.homeassistant.HomeAssistantServiceParams
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.logic.services.indication.IndicationService
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingService
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingServiceParams
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionService
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionServiceParams
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioServiceParams
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.mqtt.MqttServiceParams
import org.rhasspy.mobile.logic.services.recording.RecordingService
import org.rhasspy.mobile.logic.services.settings.AppSettingsService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextServiceParams
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechService
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechServiceParams
import org.rhasspy.mobile.logic.services.udp.UdpService
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.logic.services.wakeword.WakeWordServiceParams
import org.rhasspy.mobile.logic.services.webserver.WebServerService
import org.rhasspy.mobile.logic.services.webserver.WebServerServiceParams
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorder
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationViewModel
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabViewModel
import org.rhasspy.mobile.viewmodel.overlay.IndicationOverlayViewModel
import org.rhasspy.mobile.viewmodel.overlay.MicrophoneOverlayViewModel
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewModel
import org.rhasspy.mobile.viewmodel.settings.automaticsilencedetection.AutomaticSilenceDetectionSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.ErrorIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.RecordedIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.WakeIndicationSoundSettingsViewModel

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
    closeableSingle { HomeAssistantService() }
    closeableSingle { WakeWordService() }
    closeableSingle { RecordingService() }
    closeableSingle { DialogManagerService() }
    closeableSingle { AppSettingsService() }
    closeableSingle { IndicationService() }
    closeableSingle { ServiceMiddleware() }

    single { params -> params.getOrNull<LocalAudioServiceParams>() ?: LocalAudioServiceParams() }
    single { params -> params.getOrNull<AudioPlayingServiceParams>() ?: AudioPlayingServiceParams() }
    single { params -> params.getOrNull<IntentHandlingServiceParams>() ?: IntentHandlingServiceParams() }
    single { params -> params.getOrNull<IntentRecognitionServiceParams>() ?: IntentRecognitionServiceParams() }
    single { params -> params.getOrNull<SpeechToTextServiceParams>() ?: SpeechToTextServiceParams() }
    single { params -> params.getOrNull<TextToSpeechServiceParams>() ?: TextToSpeechServiceParams() }
    single { params -> params.getOrNull<MqttServiceParams>() ?: MqttServiceParams() }
    single { params -> params.getOrNull<HttpClientServiceParams>() ?: HttpClientServiceParams() }
    single { params -> params.getOrNull<WebServerServiceParams>() ?: WebServerServiceParams() }
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
    single {
        HomeScreenViewModel(
            serviceMiddleware = get()
        )
    }
    single {
        MicrophoneFabViewModel(
            dialogManagerService = get(),
            serviceMiddleware = get(),
            wakeWordService = get()
        )
    }
    single { ConfigurationScreenViewModel() }
    single {
        AudioPlayingConfigurationViewModel(
            service = get(),
            testRunner = get()
        )
    }
    single {
        DialogManagementConfigurationViewModel(
            service = get(),
            testRunner = get()
        )
    }
    single {
        IntentHandlingConfigurationViewModel(
            service = get(),
            testRunner = get()
        )
    }
    single {
        IntentRecognitionConfigurationViewModel(
            service = get(),
            testRunner = get()
        )
    }
    single {
        MqttConfigurationViewModel(
            service = get(),
            testRunner = get()
        )
    }
    single {
        RemoteHermesHttpConfigurationViewModel(
            service = get(),
            testRunner = get()
        )
    }
    single {
        SpeechToTextConfigurationViewModel(
            service = get(),
            testRunner = get()
        )
    }
    single {
        TextToSpeechConfigurationViewModel(
            service = get(),
            testRunner = get()
        )
    }
    single {
        WakeWordConfigurationViewModel(
            service = get(),
            testRunner = get()
        )
    }
    single {
        WebServerConfigurationViewModel(
            service = get(),
            testRunner = get()
        )
    }
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

val factoryModule = module {
    factory { params -> UdpService(params[0], params[1]) }
}

val nativeModule = module {
    closeableSingle { AudioRecorder() }
}