package org.rhasspy.mobile.logic.services.dialog

enum class DialogManagerServiceState {
    Idle,                   //doing nothing, hotword from externally awaited
    AwaitingWakeWord,       //recording HotWord
    RecordingIntent,        //recording the intent
    TranscribingIntent,     //transcribe the recorded sound to text
    RecognizingIntent,      //recognize the intent from the recorded text
    HandlingIntent          //doing intent action
}