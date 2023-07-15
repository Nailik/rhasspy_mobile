package org.rhasspy.mobile.logic.services.dialog.dialogmanager

import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction

class DialogManagerRemoteMqtt : IDialogManager {

    override suspend fun onAction(action: ServiceMiddlewareAction.DialogServiceMiddlewareAction) {
        TODO("Not yet implemented")
    }


    /*
     DialogManagementOption.RemoteMQTT -> {
                when (action.source) {
                    //from webserver or local always ignore for now
                    Source.HttpApi,
                    Source.Local -> {
                        val doNotIgnore = when (action) {
                            is WakeWordDetected -> true
                            is PlayFinished -> true
                            is StopListening -> true //maybe called by user clicking button
                            else -> false
                        }

                        if (doNotIgnore) {
                            //don't ignore
                            val result = states.contains(_currentDialogState.value)
                            if (!result) {
                                logger.v { "$action called in wrong state ${_currentDialogState.value} expected one of ${states.joinToString()}" }
                            }
                            return result
                        } else {
                            //from mqtt but session id doesn't match
                            logger.v { "$action but mqtt dialog management" }
                            return false
                        }
                    }
                    //from mqtt check session id
                    is Source.Mqtt -> {
                        if (sessionId == null) {
                            //update session id if none is set
                            sessionId = action.source.sessionId
                            return true
                        } else {
                            //compare session id if one is set
                            val matches = sessionId == action.source.sessionId
                            if (!matches) {
                                logger.v { "$action but session id doesn't match $sessionId != ${action.source.sessionId}" }
                                return false
                            }
                            val result = states.contains(_currentDialogState.value)
                            if (!result) {
                                logger.v { "$action called in wrong state ${_currentDialogState.value} expected one of ${states.joinToString()}" }
                            }
                            return result
                        }
                    }
                }
     */
}