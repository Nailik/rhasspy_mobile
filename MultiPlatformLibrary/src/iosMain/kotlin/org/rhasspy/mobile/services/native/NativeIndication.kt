package org.rhasspy.mobile.services.native

import dev.icerock.moko.resources.FileResource

actual object NativeIndication {
    actual fun playAudio(fileResource: FileResource) {
    }

    actual fun wakeUpScreen() {
    }

    actual fun displayOverAppsPermission() {
    }

    actual fun showIndication() {
    }

    actual fun closeIndicationOverOtherApps() {
    }

    actual fun releaseWakeUp() {
    }
}