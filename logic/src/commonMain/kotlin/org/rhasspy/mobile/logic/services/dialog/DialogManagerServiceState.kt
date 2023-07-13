package org.rhasspy.mobile.logic.services.dialog

import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import kotlin.reflect.KClass

//transition map defines from with state to which state the transition is happening for a specific action


//source and dialog management option also plays a role
//best is probably to build 3 different dialog management options that have a common api
//(just onaction)

sealed class DialogManagerServiceState(val transition: Map<KClass<*>, DialogManagerServiceState >) {
    data object Idle : DialogManagerServiceState(
        mapOf(
            Pair(StartSession::class, RecordingIntent),
            Pair(SessionStarted::class, RecordingIntent),
            Pair(StartListening::class, RecordingIntent)
        )
    )                  //doing nothing, hot word from externally awaited

    data object AwaitingWakeWord : DialogManagerServiceState(
        mapOf(
            Pair(WakeWordDetected::class, RecordingIntent)
        )
    )    //recording HotWord
    data object RecordingIntent : DialogManagerServiceState(
        mapOf(
            Pair(AsrError::class, Idle),
            Pair(AsrTextCaptured::class, RecognizingIntent),
            Pair(EndSession::class, Idle),
            Pair(SilenceDetected::class, TranscribingIntent),
            Pair(StopListening::class, TranscribingIntent)
        )
    )        //recording the intent
    data object TranscribingIntent : DialogManagerServiceState(
        mapOf(
            Pair(AsrError::class, Idle),
            Pair(AsrTextCaptured::class, RecognizingIntent),
            Pair(EndSession::class, Idle),
            Pair(SessionEnded::class, Idle),
        )
    )    //transcribe the recorded sound to text
    data object RecognizingIntent : DialogManagerServiceState(
        mapOf(
            Pair(EndSession::class, Idle),
            Pair(IntentRecognitionResult::class, HandlingIntent),
            Pair(IntentRecognitionError::class, Idle),
            Pair(SessionEnded::class, Idle),
        )
    )    //recognize the intent from the recorded text
    data object HandlingIntent : DialogManagerServiceState(
        mapOf(
            Pair(EndSession::class, Idle),
            Pair(SessionEnded::class, Idle),
        )
    )    //doing intent action
}