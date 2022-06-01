package org.rhasspy.mobile.android.uiservices

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import org.rhasspy.mobile.android.AndroidApplication
import org.rhasspy.mobile.android.theme.*
import org.rhasspy.mobile.nativeutils.NativeIndication
import org.rhasspy.mobile.nativeutils.OverlayPermission

/**
 * Overlay Service
 */
object IndicationOverlay {

    private lateinit var mParams: WindowManager.LayoutParams
    private val lifecycleOwner = CustomLifecycleOwner()

    private val overlayWindowManager by lazy {
        AndroidApplication.Instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    /**
     * view that's displayed when a wakeword is detected
     */
    private val view = ComposeView(AndroidApplication.Instance).apply {
        setContent {
            AppTheme(false) {
                val infiniteTransition = rememberInfiniteTransition()

                val time = 250
                // Creates a Color animation as a part of the [InfiniteTransition].
                val size by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.25f, // Dark Red
                    animationSpec = infiniteRepeatable(
                        // Linearly interpolate between initialValue and targetValue every 1000ms.
                        animation = tween(time, easing = LinearEasing),
                        // Once [TargetValue] is reached, starts the next iteration in reverse (i.e. from
                        // TargetValue to InitialValue). Then again from InitialValue to TargetValue. This
                        // [RepeatMode] ensures that the animation value is *always continuous*.
                        repeatMode = RepeatMode.Reverse
                    )
                )

                val item by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 4f, // Dark Red
                    animationSpec = infiniteRepeatable(
                        // Linearly interpolate between initialValue and targetValue every 1000ms.
                        animation = tween(time * 8, easing = LinearEasing),
                        // Once [TargetValue] is reached, starts the next iteration in reverse (i.e. from
                        // TargetValue to InitialValue). Then again from InitialValue to TargetValue. This
                        // [RepeatMode] ensures that the animation value is *always continuous*.
                        repeatMode = RepeatMode.Restart
                    )
                )

                Row(
                    modifier = Modifier
                        .height(5.dp)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(if (item > 0 && item <= 1) size else 1f)
                            .background(color = assistant_color_one)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(if (item > 1 && item <= 2) size else 1f)
                            .background(color = assistant_color_two)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(if (item > 2 && item <= 3) size else 1f)
                            .background(color = assistant_color_three)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(if (item > 3 && item <= 4) size else 1f)
                            .background(color = assistant_color_four)
                    )
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

    //stores old value to only react to changes
    private var showVisualIndicationOldValue = false

    /**
     * start service, listen to showVisualIndication and show the overlay or remove it when necessary
     */
    fun start() {

        NativeIndication.showVisualIndication.observeForever {
            if (it != showVisualIndicationOldValue) {
                if (it) {
                    if (OverlayPermission.isGranted()) {
                        overlayWindowManager.addView(view, mParams)
                        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                    }
                } else if (showVisualIndicationOldValue) {
                    overlayWindowManager.removeView(view)
                    lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                }
                showVisualIndicationOldValue = it
            }
        }
    }
}
