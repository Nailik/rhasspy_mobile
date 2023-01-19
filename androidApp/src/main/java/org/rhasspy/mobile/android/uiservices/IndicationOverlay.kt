package org.rhasspy.mobile.android.uiservices

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.AndroidApplication
import org.rhasspy.mobile.android.theme.AppTheme
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.viewmodel.overlay.IndicationOverlayViewModel

/**
 * Overlay Service
 */
object IndicationOverlay : KoinComponent {

    private var mParams: WindowManager.LayoutParams
    private var lifecycleOwner = CustomLifecycleOwner()

    //stores old value to only react to changes
    private var showVisualIndicationOldValue = false

    private var mainScope = CoroutineScope(Dispatchers.Main)
    private var viewModel = get<IndicationOverlayViewModel>()

    private var job: Job? = null

    private val overlayWindowManager by lazy {
        AndroidApplication.nativeInstance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    /**
     * view that's displayed when a wake word is detected
     */
    private fun getView(): ComposeView {
        return ComposeView(AndroidApplication.nativeInstance).apply {
            setContent {
                AppTheme {
                    Indication(viewModel.indicationState.collectAsState().value)
                }
            }
        }
    }

    init {
        val typeFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
        }
        // set the layout parameters of the window
        mParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            typeFlag,
            @Suppress("DEPRECATION") WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.BOTTOM
        }
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }


    /**
     * start service, listen to showVisualIndication and show the overlay or remove it when necessary
     */
    fun start() {
        viewModel = get()

        val view = getView()

        ViewTreeLifecycleOwner.set(view, lifecycleOwner)
        view.setViewTreeSavedStateRegistryOwner(lifecycleOwner)

        val viewModelStore = ViewModelStore()
        ViewTreeViewModelStoreOwner.set(view) { viewModelStore }

        if (job?.isActive == true) {
            return
        }
        job = CoroutineScope(Dispatchers.Default).launch {
            viewModel.isShowVisualIndication.collect {
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
        }.also {
            it.invokeOnCompletion {
                overlayWindowManager.removeView(view)
            }
        }
    }

    /**
     * stop overlay service
     */
    fun stop() {
        job?.cancel()
        job = null
    }
}

