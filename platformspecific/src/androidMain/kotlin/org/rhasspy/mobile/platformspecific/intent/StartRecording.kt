package org.rhasspy.mobile.platformspecific.intent

import android.content.Intent
import org.rhasspy.mobile.platformspecific.application.NativeApplication

enum class IntentAction(val param: String) {
    StartRecording("START_RECORDING")
}

actual fun startRecording(nativeApplication: NativeApplication) {
    nativeApplication.currentActivity?.also {
        it.startActivity(
            Intent().apply {
                putExtra(IntentAction.StartRecording.param, true)
                setClassName(nativeApplication, "org.rhasspy.mobile.android.MainActivity")
            }
        )
    } ?: run {
        nativeApplication.startActivity(
            Intent().apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(IntentAction.StartRecording.param, true)
                setClassName(nativeApplication, "org.rhasspy.mobile.android.MainActivity")
            }
        )
    }
}