package org.rhasspy.mobile.nativeutils

import dev.icerock.moko.mvvm.livedata.LiveData

expect object MicrophonePermission {

    val granted: LiveData<Boolean>

}