package org.rhasspy.mobile.android.uiservices

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                    contentAlignment = Alignment.Center
                ) {

                    when (IndicationService.readonlyState.collectAsState().value) {
                        IndicationState.Idle -> {}
                        IndicationState.Wakeup -> WakeupIndication()
                        IndicationState.Recording -> RecordingIndication()
                        IndicationState.Thinking -> ThinkingIndication()
                        IndicationState.Speaking -> SpeakingIndication()
                    }
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
        CoroutineScope(Dispatchers.Default).launch {
            IndicationService.showVisualIndicationUi.collect {
                if (it != showVisualIndicationOldValue) {
                    if (it) {
                        if (OverlayPermission.isGranted()) {
                            mainScope.launch {
                                overlayWindowManager.addView(view, mParams)
                                //has to be called from main thread
                                lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                            }
                        }
                    } else {
                        mainScope.launch {
                            overlayWindowManager.removeView(view)
                            //has to be called from main thread
                            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                        }
                    }
                    showVisualIndicationOldValue = it
                }
            }
        }
    }
}

