package org.rhasspy.mobile.viewmodel.screens.dialog

import dev.icerock.moko.resources.format
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.middleware.Source.*
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.*
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewState.DialogTransitionItem
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewState.DialogTransitionItem.DialogActionViewState
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewState.DialogTransitionItem.DialogActionViewState.SourceViewState
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewState.DialogTransitionItem.DialogActionViewState.SourceViewState.SourceType
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenViewState.DialogTransitionItem.DialogStateViewState


class DialogScreenViewStateCreator(
    private val dialogManagerService: IDialogManagerService
) {

    operator fun invoke(): StateFlow<DialogScreenViewState> {
        return combineStateFlow(
            AppSetting.isDialogAutoscroll.data,
            dialogManagerService.dialogHistory,
        ).mapReadonlyState {
            getViewState()
        }
    }

    private fun getViewState(): DialogScreenViewState {
        return DialogScreenViewState(
            isDialogAutoscroll = AppSetting.isDialogAutoscroll.value,
            history = dialogManagerService.dialogHistory.value.map { item ->
                DialogTransitionItem(
                    action = item.first.toDialogActionViewState(),
                    state = item.second.toDialogStateViewState()
                )
            }.toImmutableList()
        )
    }

    private fun DialogManagerState.toDialogStateViewState(): DialogStateViewState {
        return DialogStateViewState(
            name = this.toText(),
            sessionData = this.sessionData
        )
    }

    private fun DialogManagerState.toText(): StableStringResource {
        return when (this) {
            is AudioPlayingState       -> MR.strings.audio_playing_state.stable
            is IdleState               -> MR.strings.idle_state.stable
            is RecognizingIntentState  -> MR.strings.recognizing_intent_state.stable
            is RecordingIntentState    -> MR.strings.recording_intent_state.stable
            is TranscribingIntentState -> MR.strings.transcribing_intent_state.stable
        }
    }

    private fun DialogServiceMiddlewareAction.toDialogActionViewState(): DialogActionViewState {
        val name = this.toText()
        val source = this.source.toSourceViewState()

        val information = when (this) {
            is AsrTextCaptured         -> MR.strings.asr_text.format(text ?: "").stable
            is IntentRecognitionResult -> MR.strings.intent.format("$intentName\n$intent").stable
            is PlayAudio               -> MR.strings.audio_data_size.format(byteArray.size).stable
            is StartListening          -> MR.strings.send_audio_captured.format(sendAudioCaptured).stable
            is WakeWordDetected        -> MR.strings.wake_word.format(wakeWord).stable
            else                       -> null
        }

        return DialogActionViewState(
            name = name,
            source = source,
            information = information
        )
    }

    private fun DialogServiceMiddlewareAction.toText(): StableStringResource {
        return when (this) {
            is AsrError                -> MR.strings.asr_error.stable
            is AsrTextCaptured         -> MR.strings.asr_text_captured.stable
            is EndSession              -> MR.strings.end_session.stable
            is IntentRecognitionError  -> MR.strings.intent_recognition_error.stable
            is IntentRecognitionResult -> MR.strings.intent_recognition_result.stable
            is PlayAudio               -> MR.strings.play_audio.stable
            is PlayFinished            -> MR.strings.play_finished.stable
            is SessionEnded            -> MR.strings.session_ended.stable
            is SessionStarted          -> MR.strings.session_started.stable
            is SilenceDetected         -> MR.strings.silence_detected.stable
            is StartListening          -> MR.strings.start_listening.stable
            is StartSession            -> MR.strings.start_session.stable
            is StopAudioPlaying        -> MR.strings.stop_audio_playing.stable
            is StopListening           -> MR.strings.stop_listening.stable
            is WakeWordDetected        -> MR.strings.wake_word_detected.stable
        }
    }

    private fun Source.toSourceViewState(): SourceViewState {
        return when (this) {
            HttpApi -> SourceViewState(
                type = SourceType.Http,
                name = MR.strings.http_api.stable
            )

            Local   -> SourceViewState(
                type = SourceType.Local,
                name = MR.strings.local.stable
            )

            is Mqtt -> SourceViewState(
                type = SourceType.MQTT,
                name = MR.strings.mqtt.stable
            )
        }
    }

}