package org.rhasspy.mobile.nativeutils

import dev.icerock.moko.mvvm.livedata.LiveData

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object OverlayPermission {

    val granted: LiveData<Boolean>

    fun isGranted(): Boolean

}