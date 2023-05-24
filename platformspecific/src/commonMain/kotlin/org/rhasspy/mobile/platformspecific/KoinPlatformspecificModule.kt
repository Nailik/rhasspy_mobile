package org.rhasspy.mobile.platformspecific

import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorder
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
            nativeApplication = get()
        )
    }
    single {
        OverlayPermission(
            nativeApplication = get()
        )
    }
    factory {
        AudioRecorder()
    }
    single {
        OpenLinkUtils()
    }
}