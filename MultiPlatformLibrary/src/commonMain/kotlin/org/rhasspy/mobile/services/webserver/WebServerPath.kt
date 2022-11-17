package org.rhasspy.mobile.services.webserver

import org.rhasspy.mobile.services.webserver.WebServerCallType.*

enum class WebServerPath(val path: String, val type: WebServerCallType) {
    ListenForCommand("/api/listen-for-command", POST),
    ListenForWake("/api/listen-for-wake", POST),
    PlayRecordingPost("/api/play-recording", POST),
    PlayRecordingGet("/api/play-recording", GET),
    PlayWav("/api/play-wav", POST),
    SetVolume("/api/set-volume", POST),
    StartRecording("/api/start-recording", POST),
    StopRecording("/api/stop-recording", POST),
    Say("/api/say", POST)
}