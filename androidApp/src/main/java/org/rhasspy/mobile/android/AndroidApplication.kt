package org.rhasspy.mobile.android

import android.content.Intent
import co.touchlab.kermit.Logger
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.NativeApplication
import org.rhasspy.mobile.android.uiservices.IndicationOverlay
import org.rhasspy.mobile.android.uiservices.MicrophoneOverlay
import org.rhasspy.mobile.viewModels.*
import org.rhasspy.mobile.viewModels.configuration.*
import org.rhasspy.mobile.viewModels.settings.*
import org.rhasspy.mobile.viewModels.settings.sound.ErrorIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewModels.settings.sound.RecordedIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewModels.settings.sound.WakeIndicationSoundSettingsViewModel
import kotlin.system.exitProcess

/**
 * holds android application and native functions and provides koin module
 */
class AndroidApplication : Application() {

    private val logger = Logger.withTag("AndroidApplication")

    init {
        Instance = this
    }

    companion object {
        lateinit var Instance: NativeApplication
            private set
    }

    init {
        //catches all exceptions
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            logger.e(exception) {
                "uncaught exception in Thread $thread"
            }
            exitProcess(2)
        }
    }

    override fun onCreate() {
        super.onCreate()
        onCreated()
    }

    override fun startNativeServices() {
        IndicationOverlay.start()
        MicrophoneOverlay.start()
    }

    override fun restart() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    override val viewModelModule: Module
        get() = module {
            viewModelOf(::ConfigurationScreenViewModel)
            viewModelOf(::AudioPlayingConfigurationViewModel)
            viewModelOf(::DialogManagementConfigurationViewModel)
            viewModelOf(::IntentHandlingConfigurationViewModel)
            viewModelOf(::IntentRecognitionConfigurationViewModel)
            viewModelOf(::MqttConfigurationViewModel)
            viewModelOf(::RemoteHermesHttpConfigurationViewModel)
            viewModelOf(::SpeechToTextConfigurationViewModel)
            viewModelOf(::TextToSpeechConfigurationViewModel)
            viewModelOf(::WakeWordConfigurationViewModel)
            viewModelOf(::WebServerConfigurationViewModel)
            viewModelOf(::HomeScreenViewModel)
            viewModelOf(::LogScreenViewModel)
            viewModelOf(::SettingsScreenViewModel)
            viewModelOf(::AboutScreenViewModel)
            viewModelOf(::AutomaticSilenceDetectionSettingsViewModel)
            viewModelOf(::BackgroundServiceSettingsViewModel)
            viewModelOf(::DeviceSettingsSettingsViewModel)
            viewModelOf(::IndicationSettingsViewModel)
            viewModelOf(::WakeIndicationSoundSettingsViewModel)
            viewModelOf(::RecordedIndicationSoundSettingsViewModel)
            viewModelOf(::ErrorIndicationSoundSettingsViewModel)
            viewModelOf(::LanguageSettingsViewModel)
            viewModelOf(::LogSettingsViewModel)
            viewModelOf(::MicrophoneOverlaySettingsViewModel)
            viewModelOf(::SaveAndRestoreSettingsViewModel)
            viewModelOf(::ThemeSettingsViewModel)
            viewModelOf(::MicrophoneOverlayViewModel)
        }

    override fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(if (BuildConfig.DEBUG) false else enabled)
    }

}