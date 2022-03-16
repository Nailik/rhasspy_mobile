package org.rhasspy.mobile.services

enum class DialogueAction {
    HotWordDetected,
    StartSession,
    ToggleHotWordOff,

    SessionStarted,
    ToggleHotWordOn,
    StartListening,
    StopListening,
    SessionEnded,
    None
}