package org.rhasspy.mobile.platformspecific

import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorder
import org.rhasspy.mobile.platformspecific.background.BackgroundService
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequest
import org.rhasspy.mobile.platformspecific.language.LanguageUtils
import org.rhasspy.mobile.platformspecific.permission.BatteryOptimization
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.OverlayPermission
import org.rhasspy.mobile.platformspecific.utils.OpenLinkUtils

val platformSpecificModule = module {
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
    single {
        BackgroundService(
            nativeApplication = get()
        )
    }
    single {
        ExternalResultRequest(
            nativeApplication = get()
        )
    }
    single {
        LanguageUtils()
    }
}