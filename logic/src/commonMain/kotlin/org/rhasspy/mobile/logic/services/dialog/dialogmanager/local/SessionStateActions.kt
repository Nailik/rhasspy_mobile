package org.rhasspy.mobile.logic.services.dialog.dialogmanager.local

import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrTextCaptured
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrTimeoutError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.EndSession
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.IntentRecognitionError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.IntentRecognitionResult
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.IntentRecognitionTimeoutError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.PlayAudio
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.SilenceDetected
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.StopAudioPlaying
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.StopListening
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.WakeWordDetected
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.SessionState
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.dialog.states.IStateTransition
import org.rhasspy.mobile.logic.services.indication.IIndicationService
import org.rhasspy.mobile.logic.services.intenthandling.IIntentHandlingService
import org.rhasspy.mobile.logic.services.speechtotext.ISpeechToTextService
import org.rhasspy.mobile.settings.ConfigurationSetting

interface ISessionStateActions {

    fun onAction(action: DialogServiceMiddlewareAction, state: SessionState)

}

internal class SessionStateActions(
    private val dialogManagerService: IDialogManagerService,
    private val indicationService: IIndicationService,
    private val stateTransition: IStateTransition,
    private val speechToTextService: ISpeechToTextService,
    private val intentHandlingService: IIntentHandlingService,
) : ISessionStateActions {

    override fun onAction(action: DialogServiceMiddlewareAction, state: SessionState) {
        if (checkIfActionIsAllowed(action, state)) {
            when (action) {
                is AsrError -> onAsrError(action, state)
                is AsrTextCaptured -> onAsrTextCaptured(action, state)
                is EndSession -> onEndSession(action, state)
                is IntentRecognitionError -> onIntentRecognitionError(action, state)
                is IntentRecognitionResult -> onIntentRecognitionResult(action, state)
                is SilenceDetected -> onSilenceDetected(action, state)
                is StopListening -> onStopListening(action, state)
                is AsrTimeoutError -> onAsrTimeoutError(action, state)
                is IntentRecognitionTimeoutError -> onIntentRecognitionTimeoutError(action, state)
                else -> Unit
            }
        }
    }

    private fun onAsrError(action: AsrError, state: SessionState) {
        state.timeoutJob.cancel()

        dialogManagerService.informMqtt(state.sessionData, action)

        indicationService.onError()

        stopSpeechToTextService(action, state)

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToIdleState(
                sessionData = state.sessionData,
                isSourceMqtt = action.source is Source.Mqtt
            )
        )
    }

    private fun onAsrTextCaptured(action: AsrTextCaptured, state: SessionState) {
        state.timeoutJob.cancel()

        dialogManagerService.informMqtt(state.sessionData, action)

        stopSpeechToTextService(action, state)

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToRecognizingIntentState(
                state.sessionData.copy(recognizedText = action.text)
            )
        )
    }

    private fun onEndSession(action: EndSession, state: SessionState) {
        state.timeoutJob.cancel()

        dialogManagerService.informMqtt(state.sessionData, action)

        stopSpeechToTextService(action, state)

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToIdleState(
                sessionData = state.sessionData,
                isSourceMqtt = action.source is Source.Mqtt
            )
        )
    }

    private fun onIntentRecognitionError(action: IntentRecognitionError, state: SessionState) {
        state.timeoutJob.cancel()

        stopSpeechToTextService(action, state)

        indicationService.onError()
        dialogManagerService.informMqtt(state.sessionData, action)

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToIdleState(
                sessionData = state.sessionData,
                isSourceMqtt = action.source is Source.Mqtt
            )
        )
    }

    private fun onIntentRecognitionResult(action: IntentRecognitionResult, state: SessionState) {
        state.timeoutJob.cancel()

        stopSpeechToTextService(action, state)

        dialogManagerService.informMqtt(state.sessionData, action)
        intentHandlingService.intentHandling(action.intentName, action.intent)

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToIdleState(
                sessionData = state.sessionData,
                isSourceMqtt = action.source is Source.Mqtt
            )
        )
    }

    private fun onSilenceDetected(action: SilenceDetected, state: SessionState) {
        state.timeoutJob.cancel()

        stopSpeechToTextService(action, state)

        indicationService.onSilenceDetected()
        dialogManagerService.informMqtt(state.sessionData, action)

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToTranscribingIntentState(state.sessionData)
        )
    }

    private fun onStopListening(action: StopListening, state: SessionState) {
        state.timeoutJob.cancel()

        indicationService.onSilenceDetected()
        dialogManagerService.informMqtt(state.sessionData, action)

        stopSpeechToTextService(action, state)

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToTranscribingIntentState(state.sessionData)
        )
    }

    private fun onAsrTimeoutError(action: AsrTimeoutError, state: SessionState) {
        stopSpeechToTextService(action, state)

        indicationService.onError()
        dialogManagerService.informMqtt(state.sessionData, AsrError(Source.Local))

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToIdleState(
                sessionData = state.sessionData,
                isSourceMqtt = action.source is Source.Mqtt
            )
        )
    }

    private fun onIntentRecognitionTimeoutError(
        action: IntentRecognitionTimeoutError,
        state: SessionState,
    ) {
        stopSpeechToTextService(action, state)

        indicationService.onError()
        dialogManagerService.informMqtt(state.sessionData, IntentRecognitionError(Source.Local))

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToIdleState(
                sessionData = state.sessionData,
                isSourceMqtt = action.source is Source.Mqtt
            )
        )
    }

    private fun stopSpeechToTextService(
        action: DialogServiceMiddlewareAction,
        state: SessionState,
    ) {
        if (speechToTextService.isActive) {
            speechToTextService.endSpeechToText(
                sessionId = state.sessionData.sessionId,
                fromMqtt = action.source is Source.Mqtt
            )
        }
    }

    private fun checkIfActionIsAllowed(
        action: DialogServiceMiddlewareAction,
        state: SessionState,
    ): Boolean {
        //not from mqtt
        if (action.source !is Source.Mqtt) return true
        //session id doesn't match
        if (action.source.sessionId != state.sessionData.sessionId) return false

        return when (action) {
            is WakeWordDetected -> {
                val wakeWordOption = ConfigurationSetting.wakeWordOption.value
                return wakeWordOption == WakeWordOption.MQTT || wakeWordOption == WakeWordOption.Udp
            }

            is AsrError,
            is AsrTextCaptured,
                -> ConfigurationSetting.speechToTextOption.value == SpeechToTextOption.RemoteMQTT

            is IntentRecognitionError,
            is IntentRecognitionResult,
                -> ConfigurationSetting.intentRecognitionOption.value == IntentRecognitionOption.RemoteMQTT

            is PlayAudio,
            is StopAudioPlaying,
                -> true

            else -> false
        }

    }

}