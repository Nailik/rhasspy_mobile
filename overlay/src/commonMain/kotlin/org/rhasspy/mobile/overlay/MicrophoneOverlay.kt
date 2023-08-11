package org.rhasspy.mobile.overlay

import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.viewmodel.ViewModelFactory
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayViewModel

interface IMicrophoneOverlay {

    fun start()
    fun stop()

}

expect class MicrophoneOverlay(
    viewModelFactory: ViewModelFactory,
    viewModel: MicrophoneOverlayViewModel,
    nativeApplication: NativeApplication,
    overlayPermission: IOverlayPermission,
    dispatcher: IDispatcherProvider
) : IMicrophoneOverlay