package org.rhasspy.mobile

import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.ui.MainUi

@Suppress("unused")
fun viewController(isHasStarted: StateFlow<Boolean>) = ComposeUIViewController {
    MainUi(isHasStarted)
}