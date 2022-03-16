package org.rhasspy.mobile.services.dialogue

import com.benasher44.uuid.Uuid
import org.rhasspy.mobile.services.DialogueAction

abstract class IDialogueManagement {

    var sessionId: String? = null
        internal set

    abstract suspend fun doAction(action: DialogueAction)

}