package org.rhasspy.mobile.android.uiservices

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import org.rhasspy.mobile.android.AndroidApplication
import org.rhasspy.mobile.android.WrapMaterialTheme
import org.rhasspy.mobile.android.screens.MainActionFab
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.settings.AppSettings

object MicrophoneOverlay {

    private lateinit var mParams: WindowManager.LayoutParams
    private val lifecycleOwner = CustomLifecycleOwner()

    private val overlayWindowManager by lazy {
        AndroidApplication.Instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    /**
     * view that's displayed as overlay to start wake word detection
     */
    @OptIn(ExperimentalMaterial3Api::class)
    private val view = ComposeView(AndroidApplication.Instance).apply {
        setContent {
            WrapMaterialTheme {
                MainActionFab(Modifier.size(96.dp), null, false, viewModel())
            }
        }
    }

    init {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // set the layout parameters of the window
            mParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.BOTTOM
            }
        }

        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        ViewTreeLifecycleOwner.set(view, lifecycleOwner)
        ViewTreeSavedStateRegistryOwner.set(view, lifecycleOwner)

        val viewModelStore = ViewModelStore()
        ViewTreeViewModelStoreOwner.set(view) { viewModelStore }
    }

    //stores old value to only react to changes
    private var showOverlayOldValue = false

    /**
     * start service, listen to showVisualIndication and show the overlay or remove it when necessary
     */
    fun start() {
        AppSettings.isMicrophoneOverlayEnabled.value.addObserver {
            if (it != showOverlayOldValue) {
                if (it) {
                    if (OverlayPermission.isGranted()) {
                        overlayWindowManager.addView(view, mParams)
                        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                    }
                } else if (showOverlayOldValue) {
                    overlayWindowManager.removeView(view)
                    lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                }
                showOverlayOldValue = it
            }
        }
    }
}