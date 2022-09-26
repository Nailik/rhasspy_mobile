package org.rhasspy.mobile.android.uiservices

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.android.AndroidApplication
import org.rhasspy.mobile.android.bottomBarScreens.Fab
import org.rhasspy.mobile.android.theme.AppTheme
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.settings.AppSettings

object MicrophoneOverlay {
    private val logger = Logger.withTag("MicrophoneOverlay")

    private lateinit var mParams: WindowManager.LayoutParams
    private val lifecycleOwner = CustomLifecycleOwner()

    private val overlayWindowManager by lazy {
        AndroidApplication.Instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }


    /**
     * view that's displayed as overlay to start wake word detection
     */
    private val view: ComposeView = ComposeView(AndroidApplication.Instance).apply {
        setContent {
            AppTheme(false) {
                val size = 96.dp

                Fab(
                    modifier = Modifier
                        .size(96.dp)
                        .padding(10.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                onDragVertical(dragAmount)
                            }
                        },
                    iconSize = (size.value * 0.4).dp,
                    snackbarHostState = null,
                    viewModel = viewModel()
                )
            }
        }
    }

    private fun onDragVertical(delta: Offset) {
        mParams.apply {
            //apply
            x = (x + delta.x).toInt()
            y = (y + delta.y).toInt()
            gravity = Gravity.NO_GRAVITY
            //save
            AppSettings.isMicrophoneOverlayPositionX.value = x
            AppSettings.isMicrophoneOverlayPositionY.value = y
        }
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
            ).apply {
                x = AppSettings.isMicrophoneOverlayPositionX.value
                y = AppSettings.isMicrophoneOverlayPositionY.value
                gravity = Gravity.NO_GRAVITY
            }
        }

        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        ViewTreeLifecycleOwner.set(view, lifecycleOwner)
        view.setViewTreeSavedStateRegistryOwner(lifecycleOwner)

        val viewModelStore = ViewModelStore()
        ViewTreeViewModelStoreOwner.set(view) { viewModelStore }
    }

    //stores old value to only react to changes
    private var shouldBeShownOldValue = false


    private val shouldBeShown = combineState(
        OverlayPermission.granted, AppSettings.isMicrophoneOverlayEnabled.data, AndroidApplication.isAppInBackground, AppSettings
            .isMicrophoneOverlayWhileApp.data
    ) { _, _, _, _ ->
        getShouldBeShown()
    }

    /**
     * start service, listen to showVisualIndication and show the overlay or remove it when necessary
     */
    fun start() {
        logger.d { "start" }

        CoroutineScope(Dispatchers.Default).launch {
            shouldBeShown.collect {
                if (it != shouldBeShownOldValue) {
                    if (it) {
                        overlayWindowManager.addView(view, mParams)
                        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                    } else {
                        overlayWindowManager.removeView(view)
                        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                    }
                    shouldBeShownOldValue = it
                }
            }
        }

    }

    private fun getShouldBeShown(): Boolean {
        return OverlayPermission.granted.value && AppSettings.isMicrophoneOverlayEnabled.value &&
                (AndroidApplication.isAppInBackground.value || AppSettings.isMicrophoneOverlayWhileApp.value)
    }
}