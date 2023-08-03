package org.rhasspy.mobile.platformspecific.intent

import android.content.Intent
import org.rhasspy.mobile.platformspecific.application.NativeApplication

enum class IntentActionType(val param: String) {
    StartRecording("START_RECORDING")
}

internal actual class IntentAction actual constructor(private val nativeApplication: NativeApplication) :
    IIntentAction {

    actual override fun startRecording() {
        nativeApplication.currentActivity?.also {
            it.startActivity(
                Intent().apply {
                    putExtra(IntentActionType.StartRecording.param, true)
                    setClassName(nativeApplication, "org.rhasspy.mobile.app.MainActivity")
                }
            )
        } ?: run {
            nativeApplication.startActivity(
                Intent().apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(IntentActionType.StartRecording.param, true)
                    setClassName(nativeApplication, "org.rhasspy.mobile.app.MainActivity")
                }
            )
        }
    }

}

