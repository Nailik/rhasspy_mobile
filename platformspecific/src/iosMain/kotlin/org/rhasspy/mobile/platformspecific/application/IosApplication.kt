package org.rhasspy.mobile.platformspecific.application

import kotlinx.coroutines.flow.StateFlow

class IosApplication : NativeApplication() {
    override val isHasStarted: StateFlow<Boolean>
        get() = TODO("Not yet implemented")

    override fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun resume() {
        TODO("Not yet implemented")
    }

    override fun startRecordingAction() {
        TODO("Not yet implemented")
    }
}