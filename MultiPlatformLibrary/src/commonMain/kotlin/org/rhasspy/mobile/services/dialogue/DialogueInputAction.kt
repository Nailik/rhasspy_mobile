package org.rhasspy.mobile.services.dialogue

enum class DialogueInputAction {
    /** Dialogue Manager */
    StartSession,
    EndSession,
    /** Wake Word Detection */
    ToggleHotWordOn,
    ToggleHotWordOff,
    /** Speech to Text */
    StartListening,
    StopListening,
    /** Audio Output */
    ToggleAudioOutputOff,
    ToggleAudioOutputOn
}