package org.rhasspy.mobile.logic.services.dialog.states

import com.benasher44.uuid.uuid4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.dialog.SessionData
import org.rhasspy.mobile.logic.services.indication.IIndicationService
import org.rhasspy.mobile.logic.services.wakeword.IWakeWordService
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource.Data

interface IIdleStateAction {

    suspend fun onAction(action: DialogServiceMiddlewareAction)

}

internal class IdleStateAction(
    private val dialogManagerService: IDialogManagerService,
    private val dispatcherProvider: IDispatcherProvider,
    private val stateTransition: IStateTransition,
    private val wakeWordService: IWakeWordService,
    private val indicationService: IIndicationService,
) : IIdleStateAction {

    override suspend fun onAction(action: DialogServiceMiddlewareAction) {

        wakeWordService.stopDetection()

        val newSessionId = when (action.source) {
            Source.HttpApi -> uuid4().toString()
            Source.Local   -> uuid4().toString()
            is Source.Mqtt -> action.source.sessionId ?: uuid4().toString()
        }

        when (action) {
            is WakeWordDetected -> {
                val sessionData = SessionData(
                    sessionId = newSessionId,
                    sendAudioCaptured = false,
                    wakeWord = action.wakeWord,
                    recognizedText = null
                )

                onWakeWordDetectedAction(sessionData, action)
            }

            is StartSession -> {
                val sessionData = SessionData(
                    sessionId = newSessionId,
                    sendAudioCaptured = false,
                    wakeWord = null,
                    recognizedText = null
                )

                onStartAction(sessionData, action)
            }

            is PlayAudio    -> {
                val sessionData = SessionData(
                    sessionId = newSessionId,
                    sendAudioCaptured = false,
                    wakeWord = null,
                    recognizedText = null
                )

                onPlayAudio(sessionData, action)
            }

            else            -> Unit
        }

    }

    private suspend fun onWakeWordDetectedAction(
        sessionData: SessionData,
        action: WakeWordDetected
    ) {
        dialogManagerService.informMqtt(sessionData, action)

        indicationService.onWakeWordDetected {
            CoroutineScope(dispatcherProvider.IO).launch {
                dialogManagerService.transitionTo(
                    action = action,
                    state = stateTransition.transitionToRecordingState(
                        sessionData = sessionData,
                        isSourceMqtt = action.source is Source.Mqtt
                    )
                )
            }
        }
    }

    private suspend fun onStartAction(
        sessionData: SessionData,
        action: StartSession
    ) {
        dialogManagerService.informMqtt(sessionData, action)

        dialogManagerService.transitionTo(
            action = action,
            state = stateTransition.transitionToRecordingState(
                sessionData = sessionData,
                isSourceMqtt = action.source is Source.Mqtt
            )
        )
    }

    private suspend fun onPlayAudio(
        sessionData: SessionData,
        action: PlayAudio
    ) {
        dialogManagerService.informMqtt(sessionData, action)

        @Suppress("DEPRECATION")
        dialogManagerService.transitionTo(action, stateTransition.transitionToAudioPlayingState(Data(action.byteArray)))
    }

}