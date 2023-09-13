package org.rhasspy.mobile.platformspecific.intent

import org.rhasspy.mobile.platformspecific.application.NativeApplication

internal actual class IntentAction actual constructor(private val nativeApplication: NativeApplication) :
    IIntentAction {

    actual override fun startRecording() {
        //TODO #514
    }

}