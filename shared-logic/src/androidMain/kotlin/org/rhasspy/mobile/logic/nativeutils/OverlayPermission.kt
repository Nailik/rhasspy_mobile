package org.rhasspy.mobile.logic.nativeutils

import android.content.Intent
import android.provider.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.logic.readOnly

actual object OverlayPermission : KoinComponent {

    private val context by inject<NativeApplication>()
    private val _granted = MutableStateFlow(isGranted())
    actual val granted = _granted.readOnly

    init {
        println("test")
    }

    private var onGranted: (() -> Unit)? = null

    fun update() {
        _granted.value = isGranted()
        if (_granted.value) {
            onGranted?.invoke()
        }
        onGranted = null
    }

    actual fun requestPermission(onGranted: () -> Unit) {
        OverlayPermission.onGranted = onGranted
        // send user to the device settings
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    actual fun isGranted(): Boolean {
        return Settings.canDrawOverlays(context)
    }

}