package org.rhasspy.mobile

import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.ui.MainUi
import platform.UIKit.UIViewController

class IosApplication : Application() {

    init {
        onInit()
        CoroutineScope(Dispatchers.IO).launch {
            onCreated()
        }
    }

    @Suppress("FunctionName")
    fun MainViewController(): UIViewController = ComposeUIViewController {
        MainUi(
            viewModelFactory = get(),
            isHasStarted = isHasStarted
        )
    }

    override fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        //TODO call ios
    }

    override fun startOverlay() {
        //TODO??
    }

    override fun stopOverlay() {
        //TODO??
    }

    override fun startRecordingAction() {
        //TODO("Not yet implemented")
    }

}