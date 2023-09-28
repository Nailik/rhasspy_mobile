package org.rhasspy.mobile.viewmodel

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.rhasspy.mobile.logic.logicModule
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.platformSpecificModule
import org.rhasspy.mobile.settings.settingsModule
import org.rhasspy.mobile.viewmodel.assist.AssistantViewModel
import org.rhasspy.mobile.viewmodel.bottomnavigation.BottomNavigationViewModel
import org.rhasspy.mobile.viewmodel.bottomnavigation.BottomNavigationViewStateCreator
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewStateCreator
import org.rhasspy.mobile.viewmodel.configuration.audioinput.AudioInputConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audioinputformat.AudioInputFormatConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audioinputformat.AudioInputFormatConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat.AudioOutputFormatConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat.AudioOutputFormatConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsScreenViewStateCreator
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.AudioRecorderViewStateCreator
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabViewModel
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabViewStateCreator
import org.rhasspy.mobile.viewmodel.navigation.INavigator
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.overlay.indication.IndicationOverlayViewModel
import org.rhasspy.mobile.viewmodel.overlay.indication.IndicationOverlayViewStateCreator
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayViewModel
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayViewStateCreator
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenViewStateCreator
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewStateCreator
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenViewStateCreator
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenViewStateCreator
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenViewStateCreator
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.appearance.AppearanceSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.audiofocus.AudioFocusSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.audiofocus.AudioFocusSettingsViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.indication.sound.ErrorIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.indication.sound.RecordedIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.WakeIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsViewModel

fun viewModelModule() = module {
    includes(
        logicModule(),
        platformSpecificModule,
        settingsModule()
    )

    single<INavigator> {
        Navigator(
            nativeApplication = get()
        )
    }

    singleOf(::BottomNavigationViewStateCreator)
    singleOf(::BottomNavigationViewModel)

    singleOf(::AssistantViewModel)

    factoryOf(::MainScreenViewStateCreator)
    singleOf(::MainScreenViewModel)

    singleOf(::DialogScreenViewModel)

    factoryOf(::MicrophoneFabViewStateCreator)

    factoryOf(::HomeScreenViewStateCreator)
    singleOf(::HomeScreenViewModel)

    factoryOf(::MicrophoneFabViewStateCreator)
    singleOf(::MicrophoneFabViewModel)

    factoryOf(::ConfigurationScreenViewStateCreator)
    singleOf(::ConfigurationScreenViewModel)

    factory { params ->
        IConfigurationViewStateCreator(params[0])
    }

    singleOf(::ConnectionsScreenViewStateCreator)
    singleOf(::ConnectionsConfigurationViewModel)

    singleOf(::AudioInputConfigurationViewModel)


    singleOf(::AudioInputFormatConfigurationDataMapper)
    singleOf(::AudioInputFormatConfigurationViewModel)

    singleOf(::AudioOutputFormatConfigurationDataMapper)
    singleOf(::AudioOutputFormatConfigurationViewModel)

    singleOf(::AudioPlayingConfigurationDataMapper)
    singleOf(::AudioPlayingConfigurationViewModel)

    singleOf(::DialogManagementConfigurationDataMapper)
    singleOf(::DialogManagementConfigurationViewModel)

    singleOf(::IntentHandlingConfigurationDataMapper)
    singleOf(::IntentHandlingConfigurationViewModel)

    singleOf(::IntentRecognitionConfigurationDataMapper)
    singleOf(::IntentRecognitionConfigurationViewModel)

    singleOf(::Rhasspy2HermesConnectionConfigurationDataMapper)
    singleOf(::Rhasspy2HermesConnectionConfigurationViewModel)

    singleOf(::Rhasspy3WyomingConnectionConfigurationDataMapper)
    singleOf(::Rhasspy3WyomingConnectionConfigurationViewModel)

    singleOf(::MqttConnectionConfigurationDataMapper)
    singleOf(::MqttConnectionConfigurationViewModel)

    singleOf(::HomeAssistantConnectionConfigurationDataMapper)
    singleOf(::HomeAssistantConnectionConfigurationViewModel)

    singleOf(::WebServerConnectionConfigurationDataMapper)
    singleOf(::WebServerConnectionConfigurationViewModel)

    singleOf(::SpeechToTextConfigurationDataMapper)
    singleOf(::SpeechToTextConfigurationViewModel)

    singleOf(::TextToSpeechConfigurationDataMapper)
    singleOf(::TextToSpeechConfigurationViewModel)

    singleOf(::WakeWordConfigurationDataMapper)
    singleOf(::WakeWordConfigurationViewModel)

    singleOf(::VoiceActivityDetectionConfigurationDataMapper)
    factory { params ->
        AudioRecorderViewStateCreator(
            audioRecorder = params[0]
        )
    }
    single {
        val audioRecorder: IAudioRecorder = get()
        VoiceActivityDetectionConfigurationViewModel(
            nativeApplication = get(),
            audioRecorderViewStateCreator = get { parametersOf(audioRecorder) },
            mapper = get(),
            audioRecorder = audioRecorder
        )
    }

    factoryOf(::LogScreenViewStateCreator)
    singleOf(::LogScreenViewModel)

    factoryOf(::SettingsScreenViewStateCreator)
    singleOf(::SettingsScreenViewModel)

    factoryOf(::AboutScreenViewStateCreator)
    singleOf(::AboutScreenViewModel)

    factoryOf(::AudioFocusSettingsViewStateCreator)
    singleOf(::AudioFocusSettingsViewModel)

    factoryOf(::BackgroundServiceSettingsViewStateCreator)
    singleOf(::BackgroundServiceSettingsViewModel)

    factoryOf(::DeviceSettingsViewStateCreator)
    singleOf(::DeviceSettingsViewModel)

    factoryOf(::IndicationSettingsViewStateCreator)
    singleOf(::IndicationSettingsViewModel)

    factoryOf(::IIndicationSoundSettingsViewStateCreator)
    singleOf(::WakeIndicationSoundSettingsViewModel)
    singleOf(::RecordedIndicationSoundSettingsViewModel)
    singleOf(::ErrorIndicationSoundSettingsViewModel)

    singleOf(::AppearanceSettingsViewModel)

    factoryOf(::LogSettingsViewStateCreator)
    singleOf(::LogSettingsViewModel)

    factoryOf(::MicrophoneOverlaySettingsViewStateCreator)
    singleOf(::MicrophoneOverlaySettingsViewModel)

    singleOf(::SaveAndRestoreSettingsViewModel)

    factoryOf(::MicrophoneOverlayViewStateCreator)
    singleOf(::MicrophoneOverlayViewModel)

    factoryOf(::IndicationOverlayViewStateCreator)
    singleOf(::IndicationOverlayViewModel)
}