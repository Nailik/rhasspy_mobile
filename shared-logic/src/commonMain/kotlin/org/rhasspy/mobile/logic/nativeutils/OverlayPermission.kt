package org.rhasspy.mobile.logic.nativeutils

import kotlinx.coroutines.flow.StateFlow

expect object OverlayPermission {

    val granted: StateFlow<Boolean>

    fun requestPermission(onGranted: () -> Unit)

    fun isGranted(): Boolean

}