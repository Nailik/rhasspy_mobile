package org.rhasspy.mobile.platformspecific.volume

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * used to observe device settings for sound or notification volume
 */
actual object DeviceVolume {

    actual val volumeFlowSound: StateFlow<Int?>
        get() = MutableStateFlow(null) //TODO("Not yet implemented")

    actual val volumeFlowNotification: StateFlow<Int?>
        get() = MutableStateFlow(null) //TODO("Not yet implemented")

}