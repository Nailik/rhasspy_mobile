@file:Suppress("unused", "FunctionName")

package org.rhasspy.mobile.app

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.ui.MainUi
import platform.UIKit.UIViewController

class MainView : KoinComponent {
    fun controller(): UIViewController = ComposeUIViewController {
        MainUi(
            viewModel = get(),
            isHasStarted = get<NativeApplication>().isHasStarted
        )
    }
}


fun MainViewController(): UIViewController {
    Application()
    return MainView().controller()
}

