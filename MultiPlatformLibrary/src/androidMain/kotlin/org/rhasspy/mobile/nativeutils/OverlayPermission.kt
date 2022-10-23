package org.rhasspy.mobile.nativeutils

import android.content.Intent
import android.provider.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.readOnly

actual object OverlayPermission {

    private val _granted = MutableStateFlow(isGranted())
    actual val granted = _granted.readOnly

    private var onGranted: (() -> Unit)? = null

    fun update() {
        _granted.value = isGranted()
        if (_granted.value) {
            onGranted?.invoke()
        }
        onGranted = null
    }

    actual fun requestPermission(onGranted: () -> Unit) {
        this.onGranted = onGranted
        // send user to the device settings
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        Application.Instance.startActivity(intent)
    }

    actual fun isGranted(): Boolean {
        return Settings.canDrawOverlays(Application.Instance)
    }

}