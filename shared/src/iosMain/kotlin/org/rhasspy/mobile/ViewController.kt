package org.rhasspy.mobile

import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.ui.MainUi
import org.rhasspy.mobile.viewmodel.ViewModelFactory

@Suppress("unused")
fun viewController(
    viewModelFactory: ViewModelFactory,
    isHasStarted: StateFlow<Boolean>
) = ComposeUIViewController {
    MainUi(viewModelFactory, isHasStarted)
}