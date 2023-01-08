package org.rhasspy.mobile.nativeutils

import kotlinx.coroutines.flow.StateFlow

/**
 * used to observe device settings for sound or notification volume
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object DeviceVolume {

    //sound output volume
    val volumeFlowSound: StateFlow<Int?>

    //notification output volume
    val volumeFlowNotification: StateFlow<Int?>

}