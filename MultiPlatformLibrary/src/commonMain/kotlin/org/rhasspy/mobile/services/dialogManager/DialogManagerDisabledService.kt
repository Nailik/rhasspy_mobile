package org.rhasspy.mobile.services.dialogManager

class DialogManagerDisabledService : IDialogManagerService() {
    override fun startSessionMqtt() {
        // TODO("Not yet implemented")
    }

    override fun endSessionMqtt(sessionId: String?) {
        //   TODO("Not yet implemented")
    }

    override fun startedSessionMqtt(sessionId: String?) {
        //    TODO("Not yet implemented")
    }

    override fun sessionEndedMqtt(sessionId: String?) {
        //   TODO("Not yet implemented")
    }

    override fun startListeningMqtt(sessionId: String?, isSendAudioCaptured: Boolean) {
        //   TODO("Not yet implemented")
    }

    override fun stopListeningMqtt(sessionId: String?) {
        // TODO("Not yet implemented")
    }

    override fun intentTranscribedMqtt(sessionId: String?, text: String?) {
        //   TODO("Not yet implemented")
    }

    override fun intentTranscribedErrorMqtt(sessionId: String?) {
        //    TODO("Not yet implemented")
    }

    override fun intentNotRecognizedMqtt(sessionId: String?) {
        //   TODO("Not yet implemented")
    }

    override fun intentRecognizedMqtt(sessionId: String?, intentName: String?, intent: String) {
        //  TODO("Not yet implemented")
    }

    override fun listenForCommandWebServer() {
        //    TODO("Not yet implemented")
    }

    override fun startRecordingWebServer() {
        //   TODO("Not yet implemented")
    }

    override fun stopRecordingWebServer() {
        // TODO("Not yet implemented")
    }

    override fun hotWordDetectedLocal(hotWord: String) {
        //   TODO("Not yet implemented")
    }

    override fun silenceDetectedLocal() {
        //   TODO("Not yet implemented")
    }

    override fun hotWordDetectedMqtt(hotWord: String) {
        //   TODO("Not yet implemented")
    }

    override fun onClose() {
        //   TODO("Not yet implemented")
    }
}