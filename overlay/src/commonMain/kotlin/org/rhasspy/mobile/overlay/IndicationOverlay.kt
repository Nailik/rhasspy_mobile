package org.rhasspy.mobile.overlay

import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.viewmodel.overlay.indication.IndicationOverlayViewModel

interface IIndicationOverlay {

    fun start()
    fun stop()

}

expect class IndicationOverlay(
    viewModel: IndicationOverlayViewModel,
    nativeApplication: NativeApplication,
    overlayPermission: IOverlayPermission,
    dispatcher: IDispatcherProvider
) : IIndicationOverlay