package org.rhasspy.mobile.overlay

import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.viewmodel.overlay.indication.IndicationOverlayViewModel

actual class IndicationOverlay actual constructor(
    viewModel: IndicationOverlayViewModel,
    nativeApplication: NativeApplication,
    overlayPermission: IOverlayPermission,
    dispatcher: IDispatcherProvider
) : IIndicationOverlay {

    override fun start() {
        //TODO("Not yet implemented")
    }

    override fun stop() {
        //TODO("Not yet implemented")
    }

}