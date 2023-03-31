package org.rhasspy.mobile.platformspecific.volume

import kotlinx.coroutines.flow.StateFlow

/**
 * used to observe device settings for sound or notification volume
 */
expect object DeviceVolume {

    //sound output volume
    val volumeFlowSound: StateFlow<Int?>

    //notification output volume
    val volumeFlowNotification: StateFlow<Int?>

}