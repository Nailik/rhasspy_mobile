package org.rhasspy.mobile.nativeutils

import dev.icerock.moko.mvvm.livedata.LiveData

actual object MicrophonePermission {

    actual val granted: LiveData<Boolean>
        get() = LiveData(true)

}