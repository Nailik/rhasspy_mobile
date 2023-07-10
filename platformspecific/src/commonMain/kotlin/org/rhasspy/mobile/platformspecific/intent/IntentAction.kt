package org.rhasspy.mobile.platformspecific.intent

import org.rhasspy.mobile.platformspecific.application.NativeApplication

interface IIntentAction {

    fun startRecording()

}

internal expect class IntentAction(nativeApplication: NativeApplication) : IIntentAction {

    override fun startRecording()

}