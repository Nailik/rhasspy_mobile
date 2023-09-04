package org.rhasspy.mobile.overlay

import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.platformSpecificModule
import org.rhasspy.mobile.viewmodel.viewModelModule

fun koinOverlayModule() = module {
    includes(
        viewModelModule(),
        platformSpecificModule
    )

    single<IIndicationOverlay> {
        IndicationOverlay(
            viewModel = get(),
            nativeApplication = get(),
            overlayPermission = get(),
            dispatcher = get()
        )
    }
    single<IMicrophoneOverlay> {
        MicrophoneOverlay(
            viewModelFactory = get(),
            viewModel = get(),
            nativeApplication = get(),
            overlayPermission = get(),
            dispatcher = get()
        )
    }

}