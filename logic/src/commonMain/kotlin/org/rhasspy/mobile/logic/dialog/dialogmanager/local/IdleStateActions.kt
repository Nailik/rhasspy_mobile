package org.rhasspy.mobile.logic.dialog.dialogmanager.local

import com.benasher44.uuid.uuid4
import org.rhasspy.mobile.logic.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.dialog.SessionData
import org.rhasspy.mobile.logic.dialog.states.IStateTransition
import org.rhasspy.mobile.logic.domains.audioplaying.ISndDomain
import org.rhasspy.mobile.logic.domains.wake.IWakeDomain
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource

interface IIdleStateActions {

    fun onAction(action: DialogServiceMiddlewareAction)

}

internal class IdleStateActions(
    private val dialogManagerService: IDialogManagerService,
    private val stateTransition: IStateTransition,
    private val wakeWordService: IWakeDomain,
    private val indicationService: IIndication,
    private val audioPlayingService: ISndDomain,
) : IIdleStateActions {

    override fun onAction(action: DialogServiceMiddlewareAction) {
        when (action) {
            is StartListening   -> onStartListening(action)
            is StartSession     -> onStartSession(action)
            is WakeWordDetected -> onWakeWordDetected(action)
            is PlayAudio        -> onPlayAudio(action)
            else                -> Unit
        }
    }

    private fun onStartListening(action: StartListening) {
        wakeWordService.stopDetection()

        val sessionData = SessionData(
            sessionId = getNewSessionId(action.source),
            sendAudioCaptured = false,
            wakeWord = null,
            recognizedText = null
        )

        dialogManagerService.informMqtt(sessionData, action)

        indicationService.onSessionStarted()

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToRecordingState(
                sessionData = sessionData,
                isSourceMqtt = action.source is Source.Mqtt
            )
        )
    }

    private fun onStartSession(action: StartSession) {
        wakeWordService.stopDetection()

        val sessionData = SessionData(
            sessionId = getNewSessionId(action.source),
            sendAudioCaptured = false,
            wakeWord = null,
            recognizedText = null
        )

        dialogManagerService.informMqtt(sessionData, action)

        indicationService.onSessionStarted()

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToRecordingState(
                sessionData = sessionData,
                isSourceMqtt = action.source is Source.Mqtt
            )
        )
    }

    private fun onWakeWordDetected(action: WakeWordDetected) {
        wakeWordService.stopDetection()

        val sessionData = SessionData(
            sessionId = getNewSessionId(action.source),
            sendAudioCaptured = false,
            wakeWord = action.wakeWord,
            recognizedText = null
        )

        dialogManagerService.informMqtt(sessionData, action)
        dialogManagerService.informMqtt(sessionData, SessionStarted(Source.Local))

        indicationService.onSessionStarted()

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToRecordingState(
                sessionData = sessionData,
                isSourceMqtt = action.source is Source.Mqtt
            )
        )
    }

    private fun onPlayAudio(action: PlayAudio) {
        dialogManagerService.informMqtt(null, action)

        indicationService.onPlayAudio()
        audioPlayingService.stopPlayAudio()

        @Suppress("DEPRECATION")
        audioPlayingService.playAudio(AudioSource.Data(action.byteArray))

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToAudioPlayingState()
        )
    }

    private fun getNewSessionId(source: Source): String {
        return when (source) {
            Source.HttpApi -> uuid4().toString()
            Source.Local   -> uuid4().toString()
            is Source.Mqtt -> source.sessionId ?: uuid4().toString()
        }
    }

}