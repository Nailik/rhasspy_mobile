package org.rhasspy.mobile.services.dialogue

abstract class IDialogueManagement {

    var sessionId: String? = null
        internal set

    abstract suspend fun doAction(inputAction: DialogueInputAction)

    abstract suspend fun wakeWordDetected()

}