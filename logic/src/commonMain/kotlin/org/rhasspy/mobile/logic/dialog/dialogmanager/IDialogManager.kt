package org.rhasspy.mobile.logic.dialog.dialogmanager

import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction

interface IDialogManager {

    fun onAction(action: DialogServiceMiddlewareAction)

}

