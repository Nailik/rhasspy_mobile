package org.rhasspy.mobile.android.uiservices

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.theme.AppTheme
import org.rhasspy.mobile.logic.nativeutils.NativeApplication
import org.rhasspy.mobile.logic.nativeutils.OverlayPermission
import org.rhasspy.mobile.viewmodel.overlay.IndicationOverlayViewModel

/**
 * Overlay Service
 */
object IndicationOverlay : KoinComponent {
    private val logger = Logger.withTag("IndicationOverlay")
    private var mParams = WindowManager.LayoutParams()
    private var lifecycleOwner = CustomLifecycleOwner()

    //stores old value to only react to changes
    private var showVisualIndicationOldValue = false

    private var viewModel = get<IndicationOverlayViewModel>()

    private var job: Job? = null

    private val overlayWindowManager by lazy {
        get<NativeApplication>().getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    /**
     * view that's displayed when a wake word is detected
     */
    private fun getView(): ComposeView {
        return ComposeView(get<NativeApplication>()).apply {
            setContent {
                AppTheme {
                    Indication(viewModel.indicationState.collectAsState().value)
                }
            }
        }
    }

    init {
        try {
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
        } catch (exception: Exception) {
            logger.a(exception) { "exception in initialization" }
        }
    }


    /**
     * start service, listen to showVisualIndication and show the overlay or remove it when necessary
     */
    fun start() {
        try {
            viewModel = get()

            val view = getView()

            view.setViewTreeLifecycleOwner(lifecycleOwner)
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
                                if (Looper.myLooper() == null) {
                                    Looper.prepare()
                                }
                                launch(Dispatchers.Main) {
                                    overlayWindowManager.addView(view, mParams)
                                    lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                                }
                            }
                        } else {
                            launch(Dispatchers.Main) {
                                if (view.parent != null) {
                                    overlayWindowManager.removeView(view)
                                }
                                lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                            }
                        }
                        showVisualIndicationOldValue = it
                    }
                }
            }.also {
                it.invokeOnCompletion {
                    if (view.parent != null) {
                        //check if view is attached before removing it
                        //removing a not attached view results in IllegalArgumentException
                        overlayWindowManager.removeView(view)
                    }
                }
            }
        } catch (exception: Exception) {
            logger.a(exception) { "exception in start" }
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

