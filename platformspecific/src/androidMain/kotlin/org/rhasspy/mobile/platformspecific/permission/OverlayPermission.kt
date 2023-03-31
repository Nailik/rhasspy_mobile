package org.rhasspy.mobile.platformspecific.permission

import android.content.Intent
import android.provider.Settings
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual object OverlayPermission : KoinComponent {

    private val logger = Logger.withTag("OverlayPermission")
    private val context by inject<NativeApplication>()
    private val _granted = MutableStateFlow(isGranted())
    actual val granted: StateFlow<Boolean> = _granted

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

        if (!tryOverlayPermission()) {
            logger.a { "tryOverlayPermission didn't work" }
            trySettings()
        }
    }

    private fun tryOverlayPermission(): Boolean {
        return try {
            // send user to the device settings
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }


    private fun trySettings(): Boolean {
        return try {
            // send user to the device settings
            val intent = Intent(Settings.ACTION_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (exception: Exception) {
            logger.a(exception) { "trySettings didn't work" }
            false
        }
    }

    actual fun isGranted(): Boolean {
        return Settings.canDrawOverlays(context)
    }

}