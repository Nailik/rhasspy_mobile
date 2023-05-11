package org.rhasspy.mobile.android.uiservices

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.*
import org.rhasspy.mobile.ui.theme.AppTheme
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.combinedTestTag
import org.rhasspy.mobile.ui.main.MicrophoneFab
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabViewModel
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayUiEvent.Change.UpdateMicrophoneOverlayPosition
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayViewModel

/**
 * show overlay with microphone button
 */
object MicrophoneOverlay : KoinComponent {
    private val logger = Logger.withTag("MicrophoneOverlay")
    private var mParams = WindowManager.LayoutParams()
    private val lifecycleOwner = CustomLifecycleOwner()

    //stores old value to only react to changes
    private var showVisualIndicationOldValue = false

    private val microphoneViewModel = get<MicrophoneFabViewModel>()
    private val viewModel = get<MicrophoneOverlayViewModel>()

    private var job: Job? = null

    private val context: Context
        get() {
            val application = get<NativeApplication>()
            return application.currentActivity ?: application
        }

    private val overlayWindowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private fun onClick() {

    }

    /**
     * view that's displayed as overlay to start wake word detection
     */
    private fun getView(): ComposeView {
        return ComposeView(context).apply {
            setContent {
                AppTheme {
                    val viewState by viewModel.viewState.collectAsState()
                    val microphoneFabViewState by microphoneViewModel.viewState.collectAsState()

                    MicrophoneFab(
                        modifier = Modifier
                            .size(viewState.microphoneOverlaySize.dp)
                            .combinedTestTag(TestTag.MicrophoneFab, TestTag.Overlay)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    onDrag(dragAmount, this@apply)
                                }
                            },
                        iconSize = (viewState.microphoneOverlaySize * 0.4).dp,
                        viewState = microphoneFabViewState,
                        onEvent = { onClick() }
                    )
                }
            }
        }
    }


    private fun onDrag(delta: Offset, view: View) {
        viewModel.onEvent(UpdateMicrophoneOverlayPosition(offsetX = delta.x, offsetY = delta.y))
        mParams.applySettings()
        overlayWindowManager.updateViewLayout(view, mParams)
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
                @Suppress("DEPRECATION") FLAG_SHOW_WHEN_LOCKED or FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            ).applySettings()
            lifecycleOwner.performRestore(null)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        } catch (exception: Exception) {
            logger.a(exception) { "exception in initialization" }
        }
    }


    private fun WindowManager.LayoutParams.applySettings(): WindowManager.LayoutParams {
        //apply
        x = viewModel.viewState.value.microphoneOverlayPositionX
        y = viewModel.viewState.value.microphoneOverlayPositionY
        gravity = Gravity.NO_GRAVITY
        //save
        return this
    }

    /**
     * start service, listen to showVisualIndication and show the overlay or remove it when necessary
     */
    fun start() {
        try {
            val view = getView()

            view.setViewTreeLifecycleOwner(lifecycleOwner)
            view.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            view.setViewTreeViewModelStoreOwner(lifecycleOwner)

            if (job?.isActive == true) {
                return
            }
            logger.d { "start" }

            job = CoroutineScope(Dispatchers.Default).launch {
                viewModel.viewState.collect {
                    try {
                        if (it.shouldOverlayBeShown != showVisualIndicationOldValue) {
                            showVisualIndicationOldValue = it.shouldOverlayBeShown
                            if (it.shouldOverlayBeShown) {
                                if (Looper.myLooper() == null) {
                                    Looper.prepare()
                                }
                                launch(Dispatchers.Main) {
                                    overlayWindowManager.addView(view, mParams)
                                    lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                                }
                            } else {
                                launch(Dispatchers.Main) {
                                    if (view.parent != null) {
                                        overlayWindowManager.removeView(view)
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
