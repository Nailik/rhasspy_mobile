package org.rhasspy.mobile.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.*
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.ui.native.nativeComposeView
import org.rhasspy.mobile.ui.overlay.IndicationOverlay
import org.rhasspy.mobile.viewmodel.overlay.indication.IndicationOverlayViewModel

/**
 * Overlay Service
 */
actual class IndicationOverlay actual constructor(
    private val viewModel: IndicationOverlayViewModel,
    private val nativeApplication: NativeApplication,
    private val overlayPermission: IOverlayPermission,
    private val dispatcher: IDispatcherProvider
) : IIndicationOverlay {

    private val logger = Logger.withTag("IndicationOverlay")
    private var mParams = WindowManager.LayoutParams()
    private var lifecycleOwner = CustomLifecycleOwner()

    //stores old value to only react to changes
    private var showVisualIndicationOldValue = false

    private var job: Job? = null

    private val context: Context
        get() = nativeApplication.currentActivity ?: nativeApplication

    private val overlayWindowManager by lazy {
        context.getSystemService<WindowManager>()
    }

    /**
     * view that's displayed when a wake word is detected
     */
    private fun getView(): ComposeView {
        return nativeComposeView(context) {
            IndicationOverlay(viewModel)
        }
    }

    init {
        try {
            val typeFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION") TYPE_PHONE
            }
            // set the layout parameters of the window
            mParams = WindowManager.LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT,
                typeFlag,
                @Suppress("DEPRECATION") FLAG_SHOW_WHEN_LOCKED or FLAG_NOT_FOCUSABLE or FLAG_NOT_TOUCHABLE,
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
    override fun start() {
        try {
            val view = getView()

            view.setViewTreeLifecycleOwner(lifecycleOwner)
            view.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            view.setViewTreeViewModelStoreOwner(lifecycleOwner)

            if (job?.isActive == true) {
                return
            }
            job = CoroutineScope(dispatcher.IO).launch {
                viewModel.viewState.collect {
                    try {
                        if (it.isShowVisualIndication != showVisualIndicationOldValue) {
                            showVisualIndicationOldValue = it.isShowVisualIndication
                            if (it.isShowVisualIndication) {
                                if (overlayPermission.isGranted()) {
                                    if (Looper.myLooper() == null) {
                                        Looper.prepare()
                                    }
                                    launch(dispatcher.Main) {
                                        overlayWindowManager?.addView(view, mParams) ?: {
                                            logger.e { "addView overlayWindowManager is null" }
                                        }
                                        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                                    }
                                }
                            } else {
                                launch(dispatcher.Main) {
                                    if (view.parent != null) {
                                        overlayWindowManager?.removeView(view) ?: {
                                            logger.e { "removeView overlayWindowManager is null" }
                                        }
                                    }
                                    lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                                }
                            }
                        }
                    } catch (exception: Exception) {
                        logger.a(exception) { "exception in collect" }
                    }
                }
            }.also {
                it.invokeOnCompletion {
                    if (view.parent != null) {
                        //check if view is attached before removing it
                        //removing a not attached view results in IllegalArgumentException
                        overlayWindowManager?.removeView(view) ?: {
                            logger.e { "removeView2 overlayWindowManager is null" }
                        }
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
    override fun stop() {
        job?.cancel()
        job = null
    }

}

