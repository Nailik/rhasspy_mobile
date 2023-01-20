package org.rhasspy.mobile.android.uiservices

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.WindowManager
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
import org.rhasspy.mobile.android.*
import org.rhasspy.mobile.android.main.MicrophoneFab
import org.rhasspy.mobile.android.theme.AppTheme
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabViewModel
import org.rhasspy.mobile.viewmodel.overlay.MicrophoneOverlayViewModel

/**
 * show overlay with microphone button
 */
object MicrophoneOverlay : KoinComponent {
    private val logger = Logger.withTag("MicrophoneOverlay")

    private var mParams: WindowManager.LayoutParams
    private val lifecycleOwner = CustomLifecycleOwner()

    private var viewModel = get<MicrophoneOverlayViewModel>()

    private var job: Job? = null

    private val overlayWindowManager by lazy {
        AndroidApplication.nativeInstance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private fun onClick() {
        if (MicrophonePermission.granted.value) {
            get<MicrophoneFabViewModel>().onClick()
        } else {
            MainActivity.startRecordingAction()
        }
    }

    /**
     * view that's displayed as overlay to start wake word detection
     */
    private fun getView(): ComposeView {
        return ComposeView(AndroidApplication.nativeInstance).apply {
            setContent {
                AppTheme {
                    val size by viewModel.microphoneOverlaySize.collectAsState()

                    val microphoneViewModel = get<MicrophoneFabViewModel>()

                    MicrophoneFab(
                        modifier = Modifier
                            .size(size.dp)
                            .combinedTestTag(TestTag.MicrophoneFab, TestTag.Overlay)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    onDrag(dragAmount, this@apply)
                                }
                            },
                        iconSize = (size * 0.4).dp,
                        viewModel = microphoneViewModel,
                        onClick = ::onClick
                    )
                }
            }
        }
    }


    private fun onDrag(delta: Offset, view: View) {
        viewModel.updateMicrophoneOverlayPosition(delta.x, delta.y)
        mParams.applySettings()
        overlayWindowManager.updateViewLayout(view, mParams)
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
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).applySettings()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }


    private fun WindowManager.LayoutParams.applySettings(): WindowManager.LayoutParams {
        //apply
        x = viewModel.microphoneOverlayPositionX
        y = viewModel.microphoneOverlayPositionY
        gravity = Gravity.NO_GRAVITY
        //save
        return this
    }

    /**
     * start service, listen to showVisualIndication and show the overlay or remove it when necessary
     */
    fun start() {
        viewModel = get()

        val view = getView()

        view.setViewTreeLifecycleOwner(lifecycleOwner)
        view.setViewTreeSavedStateRegistryOwner(lifecycleOwner)

        val viewModelStore = ViewModelStore()
        ViewTreeViewModelStoreOwner.set(view) { viewModelStore }

        if (job?.isActive == true) {
            return
        }
        logger.d { "start" }

        job = CoroutineScope(Dispatchers.Default).launch {
            viewModel.shouldOverlayBeShown.collect {
                if (it) {
                    if (Looper.myLooper() == null) {
                        Looper.prepare()
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        overlayWindowManager.addView(view, mParams)
                        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            overlayWindowManager.removeView(view)
                            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                        } catch (exception: Exception) {
                            //remove view may throw not attached to window manager
                        }
                    }
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
