package org.rhasspy.mobile.services.logic

enum class State {
    Starting,      //services restarting after settings change
    AwaitingHotWord,    //waiting, maybe recording
    StartingSession,        //session is starting
    RecordingIntent,        //recording the intent
    TranscribingIntent,    //speech to text
    TranscribingError,    //speech to text failed
    RecognizingIntent,      //text to intent
    RecognizingIntentError,      //text to intent
    IntentHandling,         //intent is handled
    EndedSession,     //session ended completely
    PlayingAudio    //playing some sort of audio (stream, sound or recording)
}