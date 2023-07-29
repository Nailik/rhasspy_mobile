package org.rhasspy.mobile.logic.services.dialog.states

import com.benasher44.uuid.uuid4
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.middleware.Source.HttpApi
import org.rhasspy.mobile.logic.middleware.Source.Local
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.dialog.SessionData
import org.rhasspy.mobile.logic.services.indication.IIndicationService
import org.rhasspy.mobile.logic.services.wakeword.IWakeWordService
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource.Data

interface IIdleStateAction {

    suspend fun onAction(action: DialogServiceMiddlewareAction)

}

internal class IdleStateAction(
    private val dialogManagerService: IDialogManagerService,
    private val stateTransition: IStateTransition,
    private val wakeWordService: IWakeWordService,
    private val indicationService: IIndicationService,
) : IIdleStateAction {

    override suspend fun onAction(action: DialogServiceMiddlewareAction) {


        val newSessionId = when (action.source) {
            HttpApi        -> uuid4().toString()
            Local          -> uuid4().toString()
            is Source.Mqtt -> action.source.sessionId ?: uuid4().toString()
        }

        when (action) {
            is WakeWordDetected -> {
                wakeWordService.stopDetection()

                val sessionData = SessionData(
                    sessionId = newSessionId,
                    sendAudioCaptured = false,
                    wakeWord = action.wakeWord,
                    recognizedText = null
                )

                onWakeWordDetectedAction(sessionData, action)
            }

            is StartSession   -> {
                wakeWordService.stopDetection()

                val sessionData = SessionData(
                    sessionId = newSessionId,
                    sendAudioCaptured = false,
                    wakeWord = null,
                    recognizedText = null
                )

                onStartSessionAction(sessionData, action)
            }

            is StartListening -> {
                wakeWordService.stopDetection()

                val sessionData = SessionData(
                    sessionId = newSessionId,
                    sendAudioCaptured = false,
                    wakeWord = null,
                    recognizedText = null
                )

                onStartListeningAction(sessionData, action)
            }

            is PlayAudio        -> {
                wakeWordService.stopDetection()

                onPlayAudio(action)
            }

            else                -> Unit
        }

    }

    private suspend fun onWakeWordDetectedAction(
        sessionData: SessionData,
        action: WakeWordDetected
    ) {
        dialogManagerService.informMqtt(sessionData, action)
        dialogManagerService.informMqtt(sessionData, SessionStarted(Local))

        indicationService.onSessionStarted()

        dialogManagerService.transitionTo(
            action = action,
            state = stateTransition.transitionToRecordingState(
                sessionData = sessionData,
                isSourceMqtt = action.source is Source.Mqtt
            )
        )
    }

    private suspend fun onStartSessionAction(
        sessionData: SessionData,
        action: StartSession
    ) {
        dialogManagerService.informMqtt(sessionData, action)

        indicationService.onSessionStarted()

        dialogManagerService.transitionTo(
            action = action,
            state = stateTransition.transitionToRecordingState(
                sessionData = sessionData,
                isSourceMqtt = action.source is Source.Mqtt
            )
        )

    }

    private suspend fun onStartListeningAction(
        sessionData: SessionData,
        action: StartListening
    ) {
        dialogManagerService.informMqtt(sessionData, action)
        dialogManagerService.informMqtt(sessionData, SessionStarted(Local))

        dialogManagerService.transitionTo(
            action = action,
            state = stateTransition.transitionToRecordingState(
                sessionData = sessionData,
                isSourceMqtt = action.source is Source.Mqtt
            )
        )
    }

    private suspend fun onPlayAudio(
        action: PlayAudio
    ) {
        dialogManagerService.informMqtt(null, action)

        @Suppress("DEPRECATION")
        dialogManagerService.transitionTo(
            action = action,
            state = stateTransition.transitionToAudioPlayingState(Data(action.byteArray))
        )
    }

}