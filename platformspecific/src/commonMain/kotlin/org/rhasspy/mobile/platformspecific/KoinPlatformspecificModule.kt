package org.rhasspy.mobile.platformspecific

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.application.NativeApplication.Companion.koinApplicationModule
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorder
import org.rhasspy.mobile.platformspecific.background.BackgroundService
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequest
import org.rhasspy.mobile.platformspecific.language.LanguageUtils
import org.rhasspy.mobile.platformspecific.permission.BatteryOptimization
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.OverlayPermission
import org.rhasspy.mobile.platformspecific.settings.SettingsUtils
import org.rhasspy.mobile.platformspecific.utils.OpenLinkUtils

val platformSpecificModule = module {
    includes(
        koinApplicationModule
    )

    single {
        BatteryOptimization(
            nativeApplication = get()
        )
    }
    single {
        MicrophonePermission(
            nativeApplication = get(),
            externalResultRequest = get()
        )
    }
    single {
        OverlayPermission(
            nativeApplication = get(),
            externalResultRequest = get()
        )
    }
    factory {
        AudioRecorder()
    }
    single {
        OpenLinkUtils(
            externalResultRequest = get()
        )
    }
    singleOf(::BackgroundService)
    single {
        ExternalResultRequest(
            nativeApplication = get()
        )
    }
    single {
        LanguageUtils()
    }
    single {
        SettingsUtils(
            externalResultRequest = get(),
            nativeApplication = get()
        )
    }
}