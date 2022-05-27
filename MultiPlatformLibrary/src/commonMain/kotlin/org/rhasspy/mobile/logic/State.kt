package org.rhasspy.mobile.logic

enum class State {
    Stopped,        //services halted
    Starting,      //services restarting after settings change
    AwaitingHotWord,    //waiting, maybe recording
    StartingSession,        //session is starting
    StartedSession,        //session has started
    RecordingIntent,        //recording the intent
    RecordingStopped,        //recording was stopped
    TranscribingIntent,    //speech to text
    TranscribingError,    //speech to text failed
    RecognizingIntent,      //text to intent
    RecognizingIntentError,      //text to intent
    IntentHandling,         //intent is handled
    SessionStopped,     //session was stopped remote
    EndedSession,     //session ended completely
    PlayingAudio,    //playing some sort of audio (stream, sound or recording)
    PlayingRecording
}