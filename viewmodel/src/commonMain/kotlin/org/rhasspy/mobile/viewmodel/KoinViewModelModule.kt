package org.rhasspy.mobile.viewmodel


import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.rhasspy.mobile.logic.logicModule
import org.rhasspy.mobile.platformspecific.platformSpecificModule
import org.rhasspy.mobile.settings.settingsModule
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewStateCreator
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
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabViewModel
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabViewStateCreator
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
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.indication.sound.ErrorIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.indication.sound.RecordedIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.WakeIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsViewStateCreator
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsViewStateCreator


val viewModelModule = module {
    includes(
        logicModule,
        platformSpecificModule,
        settingsModule
    )

    singleOf(::Navigator)
    singleOf(::ViewModelFactory)

    factoryOf(::MainScreenViewStateCreator)
    singleOf(::MainScreenViewModel)

    factoryOf(::MicrophoneFabViewStateCreator)

    factoryOf(::HomeScreenViewStateCreator)
    singleOf(::HomeScreenViewModel)

    factoryOf(::MicrophoneFabViewStateCreator)
    singleOf(::MicrophoneFabViewModel)

    factoryOf(::ConfigurationScreenViewStateCreator)
    singleOf(::ConfigurationScreenViewModel)

    factoryOf(::IConfigurationViewStateCreator)

    singleOf(::AudioPlayingConfigurationViewModel)
    singleOf(::DialogManagementConfigurationViewModel)
    singleOf(::IntentHandlingConfigurationViewModel)
    singleOf(::IntentRecognitionConfigurationViewModel)
    singleOf(::MqttConfigurationViewModel)
    singleOf(::RemoteHermesHttpConfigurationViewModel)
    singleOf(::SpeechToTextConfigurationViewModel)
    singleOf(::TextToSpeechConfigurationViewModel)
    singleOf(::WakeWordConfigurationViewModel)
    singleOf(::WebServerConfigurationViewModel)

    factoryOf(::LogScreenViewStateCreator)
    singleOf(::LogScreenViewModel)

    factoryOf(::SettingsScreenViewStateCreator)
    singleOf(::SettingsScreenViewModel)

    factoryOf(::AboutScreenViewStateCreator)
    singleOf(::AboutScreenViewModel)

    factoryOf(::SilenceDetectionSettingsViewStateCreator)
    singleOf(::SilenceDetectionSettingsViewModel)

    factoryOf(::AudioFocusSettingsViewStateCreator)
    singleOf(::AudioFocusSettingsViewModel)

    factoryOf(::AudioRecorderSettingsViewStateCreator)
    singleOf(::AudioRecorderSettingsViewModel)

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

    singleOf(::LanguageSettingsViewModel)

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