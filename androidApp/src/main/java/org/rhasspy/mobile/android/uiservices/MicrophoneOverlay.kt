package org.rhasspy.mobile.android.uiservices

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.android.AndroidApplication
import org.rhasspy.mobile.android.main.MicrophoneFab
import org.rhasspy.mobile.android.main.LocalSnackbarHostState
import org.rhasspy.mobile.android.theme.AppTheme
import org.rhasspy.mobile.viewModels.MicrophoneOverlayViewModel

/**
 * show overlay with microphone button
 */
object MicrophoneOverlay : KoinComponent {

    private val logger = Logger.withTag("MicrophoneOverlay")

    private lateinit var mParams: WindowManager.LayoutParams
    private val lifecycleOwner = CustomLifecycleOwner()

    private val overlayWindowManager by lazy {
        AndroidApplication.Instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private val viewModel by inject<MicrophoneOverlayViewModel>()

    /**
     * view that's displayed as overlay to start wake word detection
     */
    //TODO test snackbar
    private val view: ComposeView = ComposeView(AndroidApplication.Instance).apply {
        setContent {
            AppTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    modifier = Modifier.size(96.dp),
                    topBar = { },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = { },
                    containerColor = Color.Transparent
                ) { paddingValues ->
                    CompositionLocalProvider(
                        LocalActivityResultRegistryOwner provides AndroidApplication.Instance.currentActivity!!,
                        LocalSnackbarHostState provides snackbarHostState
                    ) {
                        val size = 96.dp

                        MicrophoneFab(
                            modifier = Modifier
                                .padding(paddingValues)
                                .size(size)
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        onDrag(dragAmount)
                                    }
                                },
                            iconSize = (size.value * 0.4).dp
                        )
                    }
                }
            }
        }
    }


    private fun onDrag(delta: Offset) {
        viewModel.updateMicrophoneOverlayPosition(delta.x, delta.y)
        mParams.applySettings()
        overlayWindowManager.updateViewLayout(view, mParams)
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
            ).applySettings()
        }

        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        view.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
        ViewTreeLifecycleOwner.set(view, lifecycleOwner)

        val viewModelStore = ViewModelStore()
        ViewTreeViewModelStoreOwner.set(view) { viewModelStore }
    }


    private fun WindowManager.LayoutParams.applySettings(): WindowManager.LayoutParams {
        //apply
        x = viewModel.microphoneOverlayPositionX
        y = viewModel.microphoneOverlayPositionY
        gravity = Gravity.NO_GRAVITY
        //save
        return this
    }

    //stores old value to only react to changes
    private var shouldBeShownOldValue = false

    /**
     * start service, listen to showVisualIndication and show the overlay or remove it when necessary
     */
    fun start() {
        logger.d { "start" }

        CoroutineScope(Dispatchers.Default).launch {
            viewModel.shouldOverlayBeShown.collect {
                if (it != shouldBeShownOldValue) {
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
                            overlayWindowManager.removeView(view)
                            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                        }
                    }
                    shouldBeShownOldValue = it
                }
            }
        }

    }
}
