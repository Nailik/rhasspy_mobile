package org.rhasspy.mobile.services.native

import dev.icerock.moko.resources.FileResource

expect object NativeIndication {

    fun playAudio(fileResource: FileResource)

    fun wakeUpScreen()

    fun releaseWakeUp()

    fun displayOverAppsPermission()

    fun showDisplayOverOtherApps()

    fun closeDisplayOverOtherApps()

}