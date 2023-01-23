package org.rhasspy.mobile.logic.nativeutils

import kotlinx.coroutines.flow.StateFlow

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object OverlayPermission {

    val granted: StateFlow<Boolean>

    fun requestPermission(onGranted: () -> Unit)

    fun isGranted(): Boolean

}