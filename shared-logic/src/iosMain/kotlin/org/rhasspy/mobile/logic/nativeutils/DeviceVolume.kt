package org.rhasspy.mobile.logic.nativeutils

import kotlinx.coroutines.flow.StateFlow

/**
 * used to observe device settings for sound or notification volume
 */
actual object DeviceVolume {

    actual val volumeFlowSound: StateFlow<Int?>
        get() = TODO("Not yet implemented")

    actual val volumeFlowNotification: StateFlow<Int?>
        get() = TODO("Not yet implemented")

}