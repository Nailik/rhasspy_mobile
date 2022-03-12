package org.rhasspy.mobile.nativeutils

import dev.icerock.moko.mvvm.livedata.LiveData

actual object OverlayPermission {

    actual val granted: LiveData<Boolean>
        get() = LiveData(true)

}