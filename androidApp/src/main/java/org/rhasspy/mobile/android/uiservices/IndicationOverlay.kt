package org.rhasspy.mobile.android.uiservices

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.android.AndroidApplication
import org.rhasspy.mobile.android.theme.AppTheme
import org.rhasspy.mobile.android.utils.observe
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.services.IndicationService
import org.rhasspy.mobile.services.IndicationState

/**
 * Overlay Service
 */
object IndicationOverlay {

    private lateinit var mParams: WindowManager.LayoutParams
    private val lifecycleOwner = CustomLifecycleOwner()

    //stores old value to only react to changes
    private var showVisualIndicationOldValue = false

    private var mainScope = CoroutineScope(Dispatchers.Main)

    private val overlayWindowManager by lazy {
        AndroidApplication.Instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    /**
     * view that's displayed when a wake word is detected
     */
    private val view = ComposeView(AndroidApplication.Instance).apply {
        setContent {
            AppTheme(false) {
                when (IndicationService.readonlyState.observe()) {
                    IndicationState.Idle -> {}
                    IndicationState.Wakeup -> WakeupIndication()
                    IndicationState.Recording -> RecordingIndication()
                    IndicationState.Thinking -> ThinkingIndication()
                    IndicationState.Speaking -> SpeakingIndication()
                }
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
        view.setViewTreeSavedStateRegistryOwner(lifecycleOwner)

        val viewModelStore = ViewModelStore()
        ViewTreeViewModelStoreOwner.set(view) { viewModelStore }
    }

    /**
     * start service, listen to showVisualIndication and show the overlay or remove it when necessary
     */
    fun start() {
        IndicationService.showVisualIndicationUi.observe {
            if (it != showVisualIndicationOldValue) {
                if (it) {
                    if (OverlayPermission.isGranted()) {
                        overlayWindowManager.addView(view, mParams)
                        mainScope.launch {
                            //has to be called from main thread
                            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                        }
                    }
                } else if (showVisualIndicationOldValue) {
                    overlayWindowManager.removeView(view)
                    mainScope.launch {
                        //has to be called from main thread
                        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                    }
                }
                showVisualIndicationOldValue = it
            }
        }
    }
}

