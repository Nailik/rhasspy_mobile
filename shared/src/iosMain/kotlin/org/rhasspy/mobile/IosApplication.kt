package org.rhasspy.mobile

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.ui.viewController
import platform.UIKit.UIViewController

@Suppress("unused")
class IosApplication : Application() {

    init {
        CoroutineScope(Dispatchers.Default).launch {
            onCreated()
        }
    }

    @Suppress("FunctionName")
    fun MainViewController(): UIViewController = viewController(isHasStarted)

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