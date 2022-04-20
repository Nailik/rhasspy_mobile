package org.rhasspy.mobile.nativeutils

import dev.icerock.moko.mvvm.livedata.LiveData

expect object OverlayPermission {

    val granted: LiveData<Boolean>

    fun isGranted(): Boolean

}