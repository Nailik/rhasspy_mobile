package org.rhasspy.mobile.settings.settingsmigration

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.settings.Database
import java.io.File

object MigrateToDatabase : KoinComponent {

    lateinit var database: Database
        private set

    fun migrateIfNecessary() {
        val sharedPreferencesFile = File(
            get<NativeApplication>().filesDir.parent,
            "shared_prefs/org.rhasspy.mobile.android_preferences.xml"
        )

        if (!sharedPreferencesFile.exists()) return

        val driver = AndroidSqliteDriver(
            schema = InitialSchema,
            context = get<NativeApplication>(),
            name = "settings.db",
            callback = object : AndroidSqliteDriver.Callback(InitialSchema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }
        )
        database = Database(driver)
        //migrate to new settings and delete file
        migrateAppSetting()
        migrateConfigurationSetting()

        sharedPreferencesFile.delete()
    }


    private fun migrateAppSetting() {
        NewAppSetting.didShowCrashlyticsDialog.value = DeprecatedAppSetting.didShowCrashlyticsDialog.value
        NewAppSetting.didShowChangelogDialog.value = DeprecatedAppSetting.didShowChangelogDialog.value
        NewAppSetting.languageType.value = DeprecatedAppSetting.languageType.value
        NewAppSetting.themeType.value = DeprecatedAppSetting.themeType.value
        NewAppSetting.isAutomaticSilenceDetectionEnabled.value = DeprecatedAppSetting.isAutomaticSilenceDetectionEnabled.value
        NewAppSetting.automaticSilenceDetectionAudioLevel.value = DeprecatedAppSetting.automaticSilenceDetectionAudioLevel.value
        NewAppSetting.automaticSilenceDetectionTime.value = DeprecatedAppSetting.automaticSilenceDetectionTime.value
        NewAppSetting.automaticSilenceDetectionMinimumTime.value = DeprecatedAppSetting.automaticSilenceDetectionMinimumTime.value
        NewAppSetting.isBackgroundServiceEnabled.value = DeprecatedAppSetting.isBackgroundServiceEnabled.value
        NewAppSetting.microphoneOverlaySizeOption.value = DeprecatedAppSetting.microphoneOverlaySizeOption.value
        NewAppSetting.isMicrophoneOverlayWhileAppEnabled.value = DeprecatedAppSetting.isMicrophoneOverlayWhileAppEnabled.value
        NewAppSetting.microphoneOverlayPositionX.value = DeprecatedAppSetting.microphoneOverlayPositionX.value
        NewAppSetting.microphoneOverlayPositionY.value = DeprecatedAppSetting.microphoneOverlayPositionY.value
        NewAppSetting.isWakeWordDetectionTurnOnDisplayEnabled.value = DeprecatedAppSetting.isWakeWordDetectionTurnOnDisplayEnabled.value
        NewAppSetting.isSoundIndicationEnabled.value = DeprecatedAppSetting.isSoundIndicationEnabled.value
        NewAppSetting.soundIndicationOutputOption.value = DeprecatedAppSetting.soundIndicationOutputOption.value
        NewAppSetting.isWakeWordLightIndicationEnabled.value = DeprecatedAppSetting.isWakeWordLightIndicationEnabled.value
        NewAppSetting.isMqttApiDeviceChangeEnabled.value = DeprecatedAppSetting.isMqttApiDeviceChangeEnabled.value
        NewAppSetting.isHttpApiDeviceChangeEnabled.value = DeprecatedAppSetting.isHttpApiDeviceChangeEnabled.value
        NewAppSetting.volume.value = DeprecatedAppSetting.volume.value
        NewAppSetting.isHotWordEnabled.value = DeprecatedAppSetting.isHotWordEnabled.value
        NewAppSetting.isAudioOutputEnabled.value = DeprecatedAppSetting.isAudioOutputEnabled.value
        NewAppSetting.isIntentHandlingEnabled.value = DeprecatedAppSetting.isIntentHandlingEnabled.value
        NewAppSetting.wakeSoundVolume.value = DeprecatedAppSetting.wakeSoundVolume.value
        NewAppSetting.recordedSoundVolume.value = DeprecatedAppSetting.recordedSoundVolume.value
        NewAppSetting.errorSoundVolume.value = DeprecatedAppSetting.errorSoundVolume.value
        NewAppSetting.wakeSound.value = DeprecatedAppSetting.wakeSound.value
        NewAppSetting.recordedSound.value = DeprecatedAppSetting.recordedSound.value
        NewAppSetting.errorSound.value = DeprecatedAppSetting.errorSound.value
        NewAppSetting.customWakeSounds.value = DeprecatedAppSetting.customWakeSounds.value
        NewAppSetting.customRecordedSounds.value = DeprecatedAppSetting.customRecordedSounds.value
        NewAppSetting.customErrorSounds.value = DeprecatedAppSetting.customErrorSounds.value
        NewAppSetting.isCrashlyticsEnabled.value = DeprecatedAppSetting.isCrashlyticsEnabled.value
        NewAppSetting.isShowLogEnabled.value = DeprecatedAppSetting.isShowLogEnabled.value
        NewAppSetting.isLogAudioFramesEnabled.value = DeprecatedAppSetting.isLogAudioFramesEnabled.value
        NewAppSetting.logLevel.value = DeprecatedAppSetting.logLevel.value
        NewAppSetting.isLogAutoscroll.value = DeprecatedAppSetting.isLogAutoscroll.value
        NewAppSetting.audioFocusOption.value = DeprecatedAppSetting.audioFocusOption.value
        NewAppSetting.isAudioFocusOnNotification.value = DeprecatedAppSetting.isAudioFocusOnNotification.value
        NewAppSetting.isAudioFocusOnSound.value = DeprecatedAppSetting.isAudioFocusOnSound.value
        NewAppSetting.isAudioFocusOnRecord.value = DeprecatedAppSetting.isAudioFocusOnRecord.value
        NewAppSetting.isAudioFocusOnDialog.value = DeprecatedAppSetting.isAudioFocusOnDialog.value
        NewAppSetting.isPauseRecordingOnMedia.value = DeprecatedAppSetting.isPauseRecordingOnMedia.value
        NewAppSetting.isDialogAutoscroll.value = DeprecatedAppSetting.isDialogAutoscroll.value
        NewAppSetting.didShowCrashlyticsDialog.value = DeprecatedAppSetting.didShowCrashlyticsDialog.value
    }

    private fun migrateConfigurationSetting() {
        NewConfigurationSetting.siteId.value = DeprecatedConfigurationSetting.siteId.value
        NewConfigurationSetting.isHttpServerEnabled.value = DeprecatedConfigurationSetting.isHttpServerEnabled.value
        NewConfigurationSetting.httpServerPort.value = DeprecatedConfigurationSetting.httpServerPort.value
        NewConfigurationSetting.isHttpServerSSLEnabledEnabled.value = DeprecatedConfigurationSetting.isHttpServerSSLEnabledEnabled.value
        NewConfigurationSetting.httpServerSSLKeyStoreFile.value = DeprecatedConfigurationSetting.httpServerSSLKeyStoreFile.value
        NewConfigurationSetting.httpServerSSLKeyStorePassword.value = DeprecatedConfigurationSetting.httpServerSSLKeyStorePassword.value
        NewConfigurationSetting.httpServerSSLKeyAlias.value = DeprecatedConfigurationSetting.httpServerSSLKeyAlias.value
        NewConfigurationSetting.httpServerSSLKeyPassword.value = DeprecatedConfigurationSetting.httpServerSSLKeyPassword.value
        NewConfigurationSetting.isHttpClientSSLVerificationDisabled.value = DeprecatedConfigurationSetting.isHttpClientSSLVerificationDisabled.value
        NewConfigurationSetting.httpClientServerEndpointHost.value = DeprecatedConfigurationSetting.httpClientServerEndpointHost.value
        NewConfigurationSetting.httpClientServerEndpointPort.value = DeprecatedConfigurationSetting.httpClientServerEndpointPort.value
        NewConfigurationSetting.httpClientTimeout.value = DeprecatedConfigurationSetting.httpClientTimeout.value
        NewConfigurationSetting.isMqttEnabled.value = DeprecatedConfigurationSetting.isMqttEnabled.value
        NewConfigurationSetting.mqttHost.value = DeprecatedConfigurationSetting.mqttHost.value
        NewConfigurationSetting.mqttPort.value = DeprecatedConfigurationSetting.mqttPort.value
        NewConfigurationSetting.mqttUserName.value = DeprecatedConfigurationSetting.mqttUserName.value
        NewConfigurationSetting.mqttPassword.value = DeprecatedConfigurationSetting.mqttPassword.value
        NewConfigurationSetting.isMqttSSLEnabled.value = DeprecatedConfigurationSetting.isMqttSSLEnabled.value
        NewConfigurationSetting.mqttConnectionTimeout.value = DeprecatedConfigurationSetting.mqttConnectionTimeout.value
        NewConfigurationSetting.mqttKeepAliveInterval.value = DeprecatedConfigurationSetting.mqttKeepAliveInterval.value
        NewConfigurationSetting.mqttRetryInterval.value = DeprecatedConfigurationSetting.mqttRetryInterval.value
        NewConfigurationSetting.mqttKeyStoreFile.value = DeprecatedConfigurationSetting.mqttKeyStoreFile.value
        NewConfigurationSetting.wakeWordOption.value = DeprecatedConfigurationSetting.wakeWordOption.value
        NewConfigurationSetting.wakeWordAudioRecorderChannel.value = DeprecatedConfigurationSetting.wakeWordAudioRecorderChannel.value
        NewConfigurationSetting.wakeWordAudioRecorderEncoding.value = DeprecatedConfigurationSetting.wakeWordAudioRecorderEncoding.value
        NewConfigurationSetting.wakeWordAudioRecorderSampleRate.value = DeprecatedConfigurationSetting.wakeWordAudioRecorderSampleRate.value
        NewConfigurationSetting.wakeWordAudioOutputChannel.value = DeprecatedConfigurationSetting.wakeWordAudioOutputChannel.value
        NewConfigurationSetting.wakeWordAudioOutputEncoding.value = DeprecatedConfigurationSetting.wakeWordAudioOutputEncoding.value
        NewConfigurationSetting.wakeWordAudioOutputSampleRate.value = DeprecatedConfigurationSetting.wakeWordAudioOutputSampleRate.value
        NewConfigurationSetting.wakeWordPorcupineAccessToken.value = DeprecatedConfigurationSetting.wakeWordPorcupineAccessToken.value
        NewConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value = DeprecatedConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value
        NewConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value = DeprecatedConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value
        NewConfigurationSetting.wakeWordPorcupineLanguage.value = DeprecatedConfigurationSetting.wakeWordPorcupineLanguage.value
        NewConfigurationSetting.wakeWordUdpOutputHost.value = DeprecatedConfigurationSetting.wakeWordUdpOutputHost.value
        NewConfigurationSetting.wakeWordUdpOutputPort.value = DeprecatedConfigurationSetting.wakeWordUdpOutputPort.value
        NewConfigurationSetting.dialogManagementOption.value = DeprecatedConfigurationSetting.dialogManagementOption.value
        NewConfigurationSetting.textAsrTimeout.value = DeprecatedConfigurationSetting.textAsrTimeout.value
        NewConfigurationSetting.intentRecognitionTimeout.value = DeprecatedConfigurationSetting.intentRecognitionTimeout.value
        NewConfigurationSetting.recordingTimeout.value = DeprecatedConfigurationSetting.recordingTimeout.value
        NewConfigurationSetting.intentRecognitionOption.value = DeprecatedConfigurationSetting.intentRecognitionOption.value
        NewConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value = DeprecatedConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value
        NewConfigurationSetting.intentRecognitionHttpEndpoint.value = DeprecatedConfigurationSetting.intentRecognitionHttpEndpoint.value
        NewConfigurationSetting.textToSpeechOption.value = DeprecatedConfigurationSetting.textToSpeechOption.value
        NewConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.value = DeprecatedConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.value
        NewConfigurationSetting.textToSpeechHttpEndpoint.value = DeprecatedConfigurationSetting.textToSpeechHttpEndpoint.value
        NewConfigurationSetting.audioPlayingOption.value = DeprecatedConfigurationSetting.audioPlayingOption.value
        NewConfigurationSetting.audioOutputOption.value = DeprecatedConfigurationSetting.audioOutputOption.value
        NewConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.value = DeprecatedConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.value
        NewConfigurationSetting.audioPlayingHttpEndpoint.value = DeprecatedConfigurationSetting.audioPlayingHttpEndpoint.value
        NewConfigurationSetting.audioPlayingMqttSiteId.value = DeprecatedConfigurationSetting.audioPlayingMqttSiteId.value
        NewConfigurationSetting.speechToTextOption.value = DeprecatedConfigurationSetting.speechToTextOption.value
        NewConfigurationSetting.speechToTextAudioRecorderChannel.value = DeprecatedConfigurationSetting.speechToTextAudioRecorderChannel.value
        NewConfigurationSetting.speechToTextAudioRecorderEncoding.value = DeprecatedConfigurationSetting.speechToTextAudioRecorderEncoding.value
        NewConfigurationSetting.speechToTextAudioRecorderSampleRate.value = DeprecatedConfigurationSetting.speechToTextAudioRecorderSampleRate.value
        NewConfigurationSetting.speechToTextAudioOutputChannel.value = DeprecatedConfigurationSetting.speechToTextAudioOutputChannel.value
        NewConfigurationSetting.speechToTextAudioOutputEncoding.value = DeprecatedConfigurationSetting.speechToTextAudioOutputEncoding.value
        NewConfigurationSetting.speechToTextAudioOutputSampleRate.value = DeprecatedConfigurationSetting.speechToTextAudioOutputSampleRate.value
        NewConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value = DeprecatedConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value
        NewConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value = DeprecatedConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value
        NewConfigurationSetting.speechToTextHttpEndpoint.value = DeprecatedConfigurationSetting.speechToTextHttpEndpoint.value
        NewConfigurationSetting.intentHandlingOption.value = DeprecatedConfigurationSetting.intentHandlingOption.value
        NewConfigurationSetting.intentHandlingHttpEndpoint.value = DeprecatedConfigurationSetting.intentHandlingHttpEndpoint.value
        NewConfigurationSetting.intentHandlingHomeAssistantEndpoint.value = DeprecatedConfigurationSetting.intentHandlingHomeAssistantEndpoint.value
        NewConfigurationSetting.intentHandlingHomeAssistantAccessToken.value = DeprecatedConfigurationSetting.intentHandlingHomeAssistantAccessToken.value
        NewConfigurationSetting.intentHandlingHomeAssistantOption.value = DeprecatedConfigurationSetting.intentHandlingHomeAssistantOption.value

    }


}