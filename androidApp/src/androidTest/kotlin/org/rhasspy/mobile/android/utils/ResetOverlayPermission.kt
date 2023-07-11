package org.rhasspy.mobile.android.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Switch
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.resources.MR

fun UiDevice.resetOverlayPermission(context: Context, overlayPermission: IOverlayPermission) {
    if (!Settings.canDrawOverlays(context)) {
        return
    }

    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)

    val list = ".*list"

    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    UiScrollable(UiSelector().resourceIdMatches(list)).scrollIntoView(UiSelector().text(MR.strings.appName.stable))
    this.findObject(UiSelector().text(MR.strings.appName.stable)).click()
    this.findObject(UiSelector().className(Switch::class.java)).click()

    this.pressBack()
    this.pressBack()
    overlayPermission.update()
}

fun UiDevice.requestOverlayPermissionLegacy(context: Context, overlayPermission: IOverlayPermission) {
    if (Settings.canDrawOverlays(context)) {
        return
    }

    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)

    val list = ".*list"

    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    UiScrollable(UiSelector().resourceIdMatches(list)).scrollIntoView(UiSelector().text(MR.strings.appName.stable))
    this.findObject(UiSelector().text(MR.strings.appName.stable)).click()
    this.findObject(UiSelector().className(Switch::class.java)).click()

    this.pressBack()
    this.pressBack()
    overlayPermission.update()
}