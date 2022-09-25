package org.rhasspy.mobile.nativeutils

import kotlinx.coroutines.flow.StateFlow

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MicrophonePermission {

    val granted: StateFlow<Boolean>

}