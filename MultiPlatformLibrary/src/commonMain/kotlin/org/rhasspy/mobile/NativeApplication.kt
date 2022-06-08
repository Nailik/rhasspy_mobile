package org.rhasspy.mobile

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect open class NativeApplication() {

    fun startNativeServices()

    fun restart()

}