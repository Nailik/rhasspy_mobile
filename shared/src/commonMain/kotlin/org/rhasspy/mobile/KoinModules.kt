package org.rhasspy.mobile

import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.services.audiofocus.AudioFocusService
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingServiceParamsCreator
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceParamsCreator
import org.rhasspy.mobile.logic.services.homeassistant.HomeAssistantService
import org.rhasspy.mobile.logic.services.homeassistant.HomeAssistantServiceParamsCreator
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParamsCreator
import org.rhasspy.mobile.logic.services.indication.IndicationService
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingService
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingServiceParamsCreator
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionService
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionServiceParamsCreator
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioServiceParamsCreator
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.mqtt.MqttServiceParamsCreator
import org.rhasspy.mobile.logic.services.recording.RecordingService
import org.rhasspy.mobile.logic.services.settings.AppSettingsService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextServiceParamsCreator
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechService
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechServiceParamsCreator
import org.rhasspy.mobile.logic.services.wakeword.UdpConnection
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.logic.services.wakeword.WakeWordServiceParamsCreator
import org.rhasspy.mobile.logic.services.webserver.WebServerService
import org.rhasspy.mobile.logic.services.webserver.WebServerServiceParamsCreator
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorder
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.ViewModelFactory
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationViewModel
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabViewModel
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabViewStateCreator
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.overlay.indication.IndicationOverlayViewModel
import org.rhasspy.mobile.viewmodel.overlay.indication.IndicationOverlayViewStateCreator
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayViewModel
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayViewStateCreator
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenViewStateCreator
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewStateCreator
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenViewStateCreator
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenViewStateCreator
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenViewStateCreator
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.audiofocus.AudioFocusSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.audiofocus.AudioFocusSettingsViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.audiorecorder.AudioRecorderSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.audiorecorder.AudioRecorderSettingsViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.ErrorIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.indication.sound.RecordedIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.WakeIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsViewStateCreator

val navigatorModule = module {
    single {
        Navigator()
    }
}

val serviceModule = module {
    single {
        ServiceMiddleware(
            dialogManagerService = get(),
            speechToTextService = get(),
            textToSpeechService = get(),
            appSettingsService = get(),
            localAudioService = get(),
            mqttService = get(),
            wakeWordService = get()
        )
    }

    single { AudioFocusService() }

    single { AudioPlayingServiceParamsCreator() }
    single { AudioPlayingService(paramsCreator = get()) }

    single { DialogManagerServiceParamsCreator() }
    single { DialogManagerService(paramsCreator = get()) }

    single { HomeAssistantServiceParamsCreator() }
    single { HomeAssistantService(paramsCreator = get()) }

    single { HttpClientServiceParamsCreator() }
    single { HttpClientService(paramsCreator = get()) }

    single { IndicationService() }

    single { IntentHandlingServiceParamsCreator() }
    single { IntentHandlingService(paramsCreator = get()) }

    single { IntentRecognitionServiceParamsCreator() }
    single { IntentRecognitionService(paramsCreator = get()) }

    single { LocalAudioServiceParamsCreator() }
    single { LocalAudioService(paramsCreator = get()) }

    single { RecordingService(audioRecorder = get()) }

    single { AppSettingsService() }

    single { MqttServiceParamsCreator() }
    single { MqttService(paramsCreator = get()) }

    single { SpeechToTextServiceParamsCreator() }
    single { SpeechToTextService(paramsCreator = get()) }

    single { TextToSpeechServiceParamsCreator() }
    single { TextToSpeechService(paramsCreator = get()) }

    single { WakeWordServiceParamsCreator() }
    single { WakeWordService(paramsCreator = get()) }

    single { WebServerServiceParamsCreator() }
    single { WebServerService(paramsCreator = get()) }
}

val viewModelFactory = module {
    single {
        ViewModelFactory()
    }

}

val viewModelModule = module {
    single {
        MainScreenViewStateCreator(
            navigator = get()
        )
    }
    single {
        MainScreenViewModel(
            viewStateCreator = get(),
            navigator = get()
        )
    }

    single {
        MicrophoneFabViewStateCreator(
            dialogManagerService = get(),
            serviceMiddleware = get(),
            wakeWordService = get()
        )
    }

    single {
        HomeScreenViewStateCreator(
            serviceMiddleware = get()
        )
    }
    single {
        HomeScreenViewModel(
            serviceMiddleware = get(),
            viewStateCreator = get()
        )
    }

    single {
        MicrophoneFabViewStateCreator(
            dialogManagerService = get(),
            serviceMiddleware = get(),
            wakeWordService = get()
        )
    }
    single {
        MicrophoneFabViewModel(
            serviceMiddleware = get(),
            viewStateCreator = get()
        )
    }

    single {
        ConfigurationScreenViewStateCreator(
            httpClientService = get(),
            webServerService = get(),
            mqttService = get(),
            wakeWordService = get(),
            speechToTextService = get(),
            intentRecognitionService = get(),
            textToSpeechService = get(),
            audioPlayingService = get(),
            dialogManagerService = get(),
            intentHandlingService = get()
        )
    }
    single {
        ConfigurationScreenViewModel(
            viewStateCreator = get(),
            navigator = get()
        )
    }
    single {
        AudioPlayingConfigurationViewModel(
            service = get(),
            navigator = get()
        )
    }
    single {
        DialogManagementConfigurationViewModel(
            service = get(),
            navigator = get()
        )
    }
    single {
        IntentHandlingConfigurationViewModel(
            service = get(),
            navigator = get()
        )
    }
    single {
        IntentRecognitionConfigurationViewModel(
            service = get(),
            navigator = get()
        )
    }
    single {
        MqttConfigurationViewModel(
            service = get(),
            navigator = get()
        )
    }
    single {
        RemoteHermesHttpConfigurationViewModel(
            service = get(),
            navigator = get()
        )
    }
    single {
        SpeechToTextConfigurationViewModel(
            service = get(),
            navigator = get()
        )
    }
    single {
        TextToSpeechConfigurationViewModel(
            service = get(),
            navigator = get()
        )
    }
    single {
        WakeWordConfigurationViewModel(
            service = get(),
            navigator = get()
        )
    }
    single {
        WebServerConfigurationViewModel(
            service = get(),
            navigator = get()
        )
    }

    single {
        LogScreenViewStateCreator()
    }
    single {
        LogScreenViewModel(
            viewStateCreator = get()
        )
    }

    single {
        SettingsScreenViewStateCreator()
    }
    single {
        SettingsScreenViewModel(
            viewStateCreator = get(),
            navigator = get()
        )
    }

    single {
        AboutScreenViewStateCreator(
            nativeApplication = get()
        )
    }
    single {
        AboutScreenViewModel(
            viewStateCreator = get()
        )
    }


    single { params ->
        SilenceDetectionSettingsViewStateCreator(
            audioRecorder = params[0]
        )
    }
    single {
        val audioRecorder = get<AudioRecorder>()
        SilenceDetectionSettingsViewModel(
            nativeApplication = get(),
            audioRecorder = audioRecorder,
            viewStateCreator = get { parametersOf(audioRecorder) },
            navigator = get()
        )
    }

    single {
        AudioFocusSettingsViewStateCreator()
    }
    single {
        AudioFocusSettingsViewModel(
            viewStateCreator = get(),
            navigator = get()
        )
    }

    single {
        AudioRecorderSettingsViewStateCreator()
    }
    single {
        AudioRecorderSettingsViewModel(
            viewStateCreator = get(),
            navigator = get()
        )
    }

    single {
        BackgroundServiceViewStateCreator(
            nativeApplication = get()
        )
    }
    single {
        BackgroundServiceSettingsViewModel(
            viewStateCreator = get(),
            navigator = get()
        )
    }

    single {
        DeviceSettingsViewStateCreator()
    }
    single {
        DeviceSettingsSettingsViewModel(
            viewStateCreator = get(),
            navigator = get()
        )
    }

    single { IndicationSettingsViewModel(
        navigator = get()) }



    factory { params ->
        IIndicationSoundSettingsViewStateCreator(
            localAudioService = get(),
            customSoundOptions = params[0],
            soundSetting = params[1],
            soundVolume = params[2]
        )
    }
    single {
        WakeIndicationSoundSettingsViewModel(
            localAudioService = get(),
            nativeApplication = get(),
            viewStateCreator = get {
                parametersOf(
                    AppSetting.customWakeSounds,
                    AppSetting.wakeSound,
                    AppSetting.wakeSoundVolume
                )
            }
        )
    }
    single {
        RecordedIndicationSoundSettingsViewModel(
            localAudioService = get(),
            nativeApplication = get(),
            viewStateCreator = get {
                parametersOf(
                    AppSetting.customRecordedSounds,
                    AppSetting.recordedSound,
                    AppSetting.recordedSoundVolume
                )
            }
        )
    }
    single {
        ErrorIndicationSoundSettingsViewModel(
            localAudioService = get(),
            nativeApplication = get(),
            viewStateCreator = get {
                parametersOf(
                    AppSetting.customErrorSounds,
                    AppSetting.errorSound,
                    AppSetting.errorSoundVolume
                )
            }
        )
    }

    single { LanguageSettingsViewModel(
        navigator = get()) }

    single {
        LogSettingsViewModel(
            nativeApplication = get(),
            navigator = get()
        )
    }
    single { MicrophoneOverlaySettingsViewModel(navigator = get()) }
    single { SaveAndRestoreSettingsViewModel(navigator = get()) }

    single {
        MicrophoneOverlayViewStateCreator(
            nativeApplication = get()
        )
    }
    single {
        MicrophoneOverlayViewModel(
            nativeApplication = get(),
            microphoneFabViewModel = get(),
            viewStateCreator = get()
        )
    }

    single {
        IndicationOverlayViewStateCreator(
            indicationService = get()
        )
    }
    single {
        IndicationOverlayViewModel(
            viewStateCreator = get()
        )
    }
}

val factoryModule = module {
    factory { params -> UdpConnection(params[0], params[1]) }
    factory { AudioRecorder() }
}