package org.rhasspy.mobile.services.logic

import co.touchlab.kermit.Logger
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object StateMachine {

    private val logger = Logger.withTag("StateMachine")


    //saves data about current session
    private var currentSession: Session? = null

    //information about current state
    private var state = State.Starting

    /**
     * indicates that services have started
     * resets session data and state to AwaitingHotWord
     */
    fun started() {
        if (state == State.Starting) {
            currentSession = null
            state = State.AwaitingHotWord
        } else {
            logger.e { "started call with invalid state $state" }
        }
    }

    /**
     * when a hot word was detected, either by clicking on the icon,
     * when remotely a hotWord was detected or when the internal wake word services triggered
     * keyword indicates which keyword triggered it
     * starts the session
     */
    fun hotWordDetected(keyword: String) {
        if (state == State.AwaitingHotWord) {
            state = State.StartingSession
            startedSession("id", keyword)
        } else {
            logger.e { "hotWordDetected call with invalid state $state" }
        }
    }

    /**
     * indicates that a session has started
     * now recording will take place
     */
    fun startedSession(sessionId: String, keyword: String) {
        if (state == State.StartingSession) {
            currentSession = Session(sessionId, keyword)
            state = State.RecordingIntent
        } else {
            logger.e { "startedSession call with invalid state $state" }
        }
    }

    /**
     * when silence is detected when recording
     * can be triggered by button (to stop recording)
     * or when remotely silence was detected
     * or when silence internally was detected
     * will then start transcribing the intent to text
     */
    fun silenceDetected() {
        if (state == State.RecordingIntent) {
            state = State.TranscribingIntent
        } else {
            logger.e { "silenceDetected call with invalid state $state" }
        }
    }

    /**
     * when an intent was transcribed to a text
     * will start to recognize intent in order to handle it later
     */
    fun intentTranscribed(intent: String) {
        if (state == State.TranscribingIntent) {
            state = State.RecognizingIntent
        } else {
            logger.e { "intentTranscribed call with invalid state $state" }
        }
    }

    /**
     * there was an error in transcription
     * text could not be received from speech
     * will end session
     */
    fun intentTranscriptionError() {
        if (state == State.TranscribingIntent) {
            state = State.TranscribingError
            sessionEnded()
        } else {
            logger.e { "intentTranscriptionError call with invalid state $state" }
        }
    }

    /**
     * intent was recognized will now be handled and session will be ended
     */
    fun intentRecognized(intent: String) {
        if (state == State.RecognizingIntent) {
            state = State.IntentHandling
            sessionEnded()
        } else {
            logger.e { "intentRecognized call with invalid state $state" }
        }
    }

    /**
     * intent was not found from text
     * ending session
     */
    fun intentNotRecognized() {
        if (state == State.RecognizingIntent) {
            state = State.RecognizingIntentError
            sessionEnded()
        } else {
            logger.e { "intentNotRecognized call with invalid state $state" }
        }
    }

    fun sessionEnded(){
        if (state == State.TranscribingError || state == State.IntentHandling || state == State.RecognizingIntentError) {
            state = State.EndedSession
        } else {
            logger.e { "intentTranscriptionError call with invalid state $state" }
        }
    }

    /**
     * plays indication audio and calls finished to do whatever is needed
     */
    fun playAudio(finished: () -> Unit) {
        state = State.PlayingAudio
        finished.invoke()
    }
}