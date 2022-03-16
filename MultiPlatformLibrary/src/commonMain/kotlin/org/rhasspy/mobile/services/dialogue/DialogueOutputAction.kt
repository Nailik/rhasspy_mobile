package org.rhasspy.mobile.services.dialogue

enum class DialogueOutputAction {
    /** Dialogue Manager */
    SessionStarted,
    SessionEnded,
    /** Audio Input */
    AudioFrame,
    /** Wake Word Detection */
    HotWordDetected,
    HotWordError,
    /** Speech to Text */
    AudioCaptured,
    /** Intent Handling */
    Say,
    /** Audio Output */
    PlayFinished
}