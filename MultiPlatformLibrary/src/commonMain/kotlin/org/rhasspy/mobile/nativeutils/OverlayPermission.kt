package org.rhasspy.mobile.nativeutils

import kotlinx.coroutines.flow.StateFlow

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object OverlayPermission {

    val granted: StateFlow<Boolean>

    fun requestPermission(onResult: (granted: Boolean) -> Unit)

    fun isGranted(): Boolean

}