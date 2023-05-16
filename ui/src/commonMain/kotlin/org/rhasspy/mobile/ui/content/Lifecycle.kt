package org.rhasspy.mobile.ui.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState

@Composable
fun OnPauseEffect(onPause: () -> Unit) {
  /*  val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
    DisposableEffect(onPause) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                onPause.invoke()
            }
        }
        val lifecycle = lifecycleOwner.value.lifecycle
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    } */
}