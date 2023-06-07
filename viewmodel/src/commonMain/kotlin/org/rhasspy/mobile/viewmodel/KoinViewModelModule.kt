package org.rhasspy.mobile.viewmodel


import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.rhasspy.mobile.logic.logicModule
import org.rhasspy.mobile.platformspecific.platformSpecificModule
import org.rhasspy.mobile.settings.settingsModule
import org.rhasspy.mobile.viewmodel.configuration.edit.audioplaying.AudioPlayingConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.dialogmanagement.DialogManagementConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling.IntentHandlingConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.intentrecognition.IntentRecognitionConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.mqtt.MqttConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.remotehermeshttp.RemoteHermesHttpConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.speechtotext.SpeechToTextConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.texttospeech.TextToSpeechConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationEditViewModel
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

    singleOf(::MainScreenViewStateCreator)
    singleOf(::MainScreenViewModel)

    singleOf(::MicrophoneFabViewStateCreator)

    singleOf(::HomeScreenViewStateCreator)
    singleOf(::HomeScreenViewModel)

    singleOf(::MicrophoneFabViewStateCreator)
    singleOf(::MicrophoneFabViewModel)

    singleOf(::ConfigurationScreenViewStateCreator)
    singleOf(::ConfigurationScreenViewModel)

    singleOf(::AudioPlayingConfigurationEditViewModel)
    singleOf(::DialogManagementConfigurationEditViewModel)
    singleOf(::IntentHandlingConfigurationEditViewModel)
    singleOf(::IntentRecognitionConfigurationEditViewModel)
    singleOf(::MqttConfigurationEditViewModel)
    singleOf(::RemoteHermesHttpConfigurationEditViewModel)
    singleOf(::SpeechToTextConfigurationEditViewModel)
    singleOf(::TextToSpeechConfigurationEditViewModel)
    singleOf(::WakeWordConfigurationEditViewModel)
    singleOf(::WebServerConfigurationEditViewModel)

    singleOf(::LogScreenViewStateCreator)
    singleOf(::LogScreenViewModel)

    singleOf(::SettingsScreenViewStateCreator)
    singleOf(::SettingsScreenViewModel)

    singleOf(::AboutScreenViewStateCreator)
    singleOf(::AboutScreenViewModel)

    singleOf(::SilenceDetectionSettingsViewStateCreator)
    singleOf(::SilenceDetectionSettingsViewModel)

    singleOf(::AudioFocusSettingsViewStateCreator)
    singleOf(::AudioFocusSettingsViewModel)

    singleOf(::AudioRecorderSettingsViewStateCreator)
    singleOf(::AudioRecorderSettingsViewModel)

    singleOf(::BackgroundServiceSettingsViewStateCreator)
    singleOf(::BackgroundServiceSettingsViewModel)

    singleOf(::DeviceSettingsViewStateCreator)
    singleOf(::DeviceSettingsViewModel)

    singleOf(::IndicationSettingsViewStateCreator)
    singleOf(::IndicationSettingsViewModel)

    factoryOf(::IIndicationSoundSettingsViewStateCreator)
    singleOf(::WakeIndicationSoundSettingsViewModel)
    singleOf(::RecordedIndicationSoundSettingsViewModel)
    singleOf(::ErrorIndicationSoundSettingsViewModel)

    singleOf(::LanguageSettingsViewModel)

    singleOf(::LogSettingsViewStateCreator)
    singleOf(::LogSettingsViewModel)

    singleOf(::MicrophoneOverlaySettingsViewStateCreator)
    singleOf(::MicrophoneOverlaySettingsViewModel)

    singleOf(::SaveAndRestoreSettingsViewModel)

    singleOf(::MicrophoneOverlayViewStateCreator)
    singleOf(::MicrophoneOverlayViewModel)

    singleOf(::IndicationOverlayViewStateCreator)
    singleOf(::IndicationOverlayViewModel)
}