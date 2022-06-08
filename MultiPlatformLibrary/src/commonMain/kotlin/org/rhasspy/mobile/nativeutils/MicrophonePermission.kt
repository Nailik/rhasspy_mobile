package org.rhasspy.mobile.nativeutils

import dev.icerock.moko.mvvm.livedata.LiveData

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MicrophonePermission {

    val granted: LiveData<Boolean>

}