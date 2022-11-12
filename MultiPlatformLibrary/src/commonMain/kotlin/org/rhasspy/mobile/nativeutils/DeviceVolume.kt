package org.rhasspy.mobile.nativeutils

import kotlinx.coroutines.flow.StateFlow

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object DeviceVolume {

    val volumeFlowSound: StateFlow<Int?>
    val volumeFlowNotification: StateFlow<Int?>

}