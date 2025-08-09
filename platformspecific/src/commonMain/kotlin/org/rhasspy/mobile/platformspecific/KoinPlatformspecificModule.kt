package org.rhasspy.mobile.platformspecific

import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.application.NativeApplication.Companion.koinApplicationModule
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorder
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.background.BackgroundService
import org.rhasspy.mobile.platformspecific.background.IBackgroundService
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequest
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest
import org.rhasspy.mobile.platformspecific.firebase.Crashlytics
import org.rhasspy.mobile.platformspecific.firebase.ICrashlytics
import org.rhasspy.mobile.platformspecific.intent.IIntentAction
import org.rhasspy.mobile.platformspecific.intent.IntentAction
import org.rhasspy.mobile.platformspecific.language.ILanguageUtils
import org.rhasspy.mobile.platformspecific.language.LanguageUtils
import org.rhasspy.mobile.platformspecific.permission.BatteryOptimization
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.OverlayPermission
import org.rhasspy.mobile.platformspecific.settings.ISettingsUtils
import org.rhasspy.mobile.platformspecific.settings.SettingsUtils
import org.rhasspy.mobile.platformspecific.utils.IOpenLinkUtils
import org.rhasspy.mobile.platformspecific.utils.OpenLinkUtils

val platformSpecificModule = module {
    includes(
        koinApplicationModule
    )

    single<ICrashlytics> {
        Crashlytics()
    }

    single<IIntentAction> {
        IntentAction(
            nativeApplication = get()
        )
    }

    single<IDispatcherProvider> {
        DispatcherProvider()
    }
    single {
        BatteryOptimization(
            nativeApplication = get()
        )
    }
    single<IMicrophonePermission> {
        MicrophonePermission(
            nativeApplication = get(),
            externalResultRequest = get()
        )
    }
    single<IOverlayPermission> {
        OverlayPermission(
            nativeApplication = get(),
            externalResultRequest = get()
        )
    }
    factory<IAudioRecorder> {
        AudioRecorder()
    }
    single<IOpenLinkUtils> {
        OpenLinkUtils(
            externalResultRequest = get()
        )
    }
    single<IBackgroundService> {
        BackgroundService()
    }
    single<IExternalResultRequest> {
        ExternalResultRequest(
            nativeApplication = get()
        )
    }
    single<ILanguageUtils> {
        LanguageUtils()
    }
    single<ISettingsUtils> {
        SettingsUtils(
            externalResultRequest = get(),
            nativeApplication = get()
        )
    }
}