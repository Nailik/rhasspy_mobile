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
import org.rhasspy.mobile.viewmodel.configuration.domains.asr.AsrDomainConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.domains.asr.AsrDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.handle.HandleDomainConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.domains.handle.HandleDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.intent.IntentDomainConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.domains.intent.IntentDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.MicDomainConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.MicDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.audioinputformat.AudioRecorderFormatConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.audioinputformat.AudioRecorderFormatConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.audiooutputformat.AudioOutputFormatConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.audiooutputformat.AudioOutputFormatConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.snd.AudioPlayingConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.domains.snd.AudioPlayingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.tts.TtsDomainConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.domains.tts.TtsDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.vad.AudioRecorderViewStateCreator
import org.rhasspy.mobile.viewmodel.configuration.domains.vad.VadDomainConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.domains.vad.VadDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.wake.WakeDomainConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.domains.wake.WakeDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationViewModel
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
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewStateCreator
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

    singleOf(::DialogScreenViewStateCreator)
    singleOf(::DialogScreenViewModel)

    factoryOf(::MicrophoneFabViewStateCreator)

    factoryOf(::HomeScreenViewStateCreator)
    singleOf(::HomeScreenViewModel)

    factoryOf(::MicrophoneFabViewStateCreator)
    singleOf(::MicrophoneFabViewModel)

    factoryOf(::ConfigurationScreenViewStateCreator)
    singleOf(::ConfigurationScreenViewModel)

    singleOf(::ConnectionsScreenViewStateCreator)
    singleOf(::ConnectionsConfigurationViewModel)

    singleOf(::MicDomainConfigurationDataMapper)
    singleOf(::MicDomainConfigurationViewModel)

    singleOf(::AudioRecorderFormatConfigurationDataMapper)
    singleOf(::AudioRecorderFormatConfigurationViewModel)

    singleOf(::AudioOutputFormatConfigurationDataMapper)
    singleOf(::AudioOutputFormatConfigurationViewModel)

    singleOf(::AudioPlayingConfigurationDataMapper)
    singleOf(::AudioPlayingConfigurationViewModel)

    singleOf(::PipelineConfigurationDataMapper)
    singleOf(::PipelineConfigurationViewModel)

    singleOf(::HandleDomainConfigurationDataMapper)
    singleOf(::HandleDomainConfigurationViewModel)

    singleOf(::IntentDomainConfigurationDataMapper)
    singleOf(::IntentDomainConfigurationViewModel)

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

    singleOf(::AsrDomainConfigurationDataMapper)
    singleOf(::AsrDomainConfigurationViewModel)

    singleOf(::TtsDomainConfigurationDataMapper)
    singleOf(::TtsDomainConfigurationViewModel)

    singleOf(::WakeDomainConfigurationDataMapper)
    singleOf(::WakeDomainConfigurationViewModel)

    singleOf(::VadDomainConfigurationDataMapper)
    factory { params ->
        AudioRecorderViewStateCreator(
            audioRecorder = params[0]
        )
    }
    single {
        val audioRecorder: IAudioRecorder = get()
        VadDomainConfigurationViewModel(
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