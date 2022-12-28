package org.rhasspy.mobile.middleware

class ServiceTestMiddleware : IServiceMiddleware() {
/*
    override fun mqttAction(event: MqttAction) {
        coroutineScope.launch {
            when (event) {
                is MqttAction.AsrError -> {
                    if (event.sessionId == sessionId) {
                        rhasspyActionsService.endSpeechToText(event.sessionId, true)
                    }
                }
                is MqttAction.AsrTextCaptured -> {
                    if (event.sessionId == sessionId) {
                        rhasspyActionsService.endSpeechToText(event.sessionId, true)
                    }
                }
                is MqttAction.PlayAudio -> {
                    rhasspyActionsService.playAudio(event.byteArray.toList())
                }
                else -> {}
            }
        }
    }

    override fun webServerAction(event: WebServerAction) {
        coroutineScope.launch {
            when (event) {
                is WebServerAction.PlayWav -> {
                    rhasspyActionsService.playAudio(event.byteArray.toList())
                }
                else -> {}
            }
        }
    }*/

}