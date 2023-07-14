package org.rhasspy.mobile.logic.services.dialog.dialogmanager

import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction

class DialogManagerDisabled : IDialogManager {

    override suspend fun onAction(action: ServiceMiddlewareAction.DialogServiceMiddlewareAction) {
        TODO("Not yet implemented")
    }

}