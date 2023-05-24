package org.rhasspy.mobile.viewmodel


import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorder
import org.rhasspy.mobile.settings.AppSetting
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
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsViewModel
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


val viewModelModule = module {
    single {
        ViewModelFactory()
    }
    single {
        Navigator(nativeApplication = get())
    }
    single {
        MainScreenViewStateCreator(
            navigator = get()
        )
    }
    single {
        MainScreenViewModel(
            viewStateCreator = get()
        )
    }

    single {
        MicrophoneFabViewStateCreator(
            dialogManagerService = get(),
            serviceMiddleware = get(),
            wakeWordService = get(),
            microphonePermission = get()
        )
    }

    single {
        HomeScreenViewStateCreator(
            serviceMiddleware = get(),
            microphoneFabViewStateCreator = get()
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
            wakeWordService = get(),
            microphonePermission = get()
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
            viewStateCreator = get()
        )
    }
    single {
        AudioPlayingConfigurationViewModel(
            service = get()
        )
    }
    single {
        DialogManagementConfigurationViewModel(
            service = get()
        )
    }
    single {
        IntentHandlingConfigurationViewModel(
            service = get()
        )
    }
    single {
        IntentRecognitionConfigurationViewModel(
            service = get()
        )
    }
    single {
        MqttConfigurationViewModel(
            service = get()
        )
    }
    single {
        RemoteHermesHttpConfigurationViewModel(
            service = get()
        )
    }
    single {
        SpeechToTextConfigurationViewModel(
            service = get()
        )
    }
    single {
        TextToSpeechConfigurationViewModel(
            service = get()
        )
    }
    single {
        WakeWordConfigurationViewModel(
            service = get(),
            microphonePermission = get()
        )
    }
    single {
        WebServerConfigurationViewModel(
            service = get()
        )
    }

    single {
        LogScreenViewStateCreator(
            fileLogger = get()
        )
    }
    single {
        LogScreenViewModel(
            viewStateCreator = get(),
            fileLogger = get()
        )
    }

    single {
        SettingsScreenViewStateCreator()
    }
    single {
        SettingsScreenViewModel(
            viewStateCreator = get()
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
            viewStateCreator = get { parametersOf(audioRecorder) }
        )
    }

    single {
        AudioFocusSettingsViewStateCreator()
    }
    single {
        AudioFocusSettingsViewModel(
            viewStateCreator = get()
        )
    }

    single {
        AudioRecorderSettingsViewStateCreator()
    }
    single {
        AudioRecorderSettingsViewModel(
            viewStateCreator = get()
        )
    }

    single {
        BackgroundServiceSettingsViewStateCreator(
            nativeApplication = get(),
            batteryOptimization = get()
        )
    }
    single {
        BackgroundServiceSettingsViewModel(
            viewStateCreator = get()
        )
    }

    single {
        DeviceSettingsViewStateCreator()
    }
    single {
        DeviceSettingsViewModel(
            viewStateCreator = get()
        )
    }

    single {
        IndicationSettingsViewModel()
    }



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

    single {
        LanguageSettingsViewModel()
    }

    single {
        LogSettingsViewModel(
            nativeApplication = get()
        )
    }
    single { MicrophoneOverlaySettingsViewModel() }
    single { SaveAndRestoreSettingsViewModel() }

    single {
        MicrophoneOverlayViewStateCreator(
            nativeApplication = get(),
            microphoneFabViewStateCreator = get(),
            overlayPermission = get()
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