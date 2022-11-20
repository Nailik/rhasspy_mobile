package org.rhasspy.mobile.services.statemachine

import org.rhasspy.mobile.settings.AppSettings

class StateMachineService(
    isTest: Boolean = false
) {

    fun startMqttSession() {

    }

    fun endMqttSession(sessionId: String?) {

    }

    fun startedMqttSession(sessionId: String?){

    }

    fun sessionEndedMqtt(sessionId: String?){

    }

    fun toggleHotWordEnabledMqtt(isEnabled: Boolean ){

    }

    fun startListeningMqtt(sessionId: String?, isSendAudioCaptured: Boolean){

    }

    fun stopListeningMqtt(sessionId: String?){

    }

    fun intentTranscribedMqtt(sessionId: String?, text: String?){

    }

    fun intentTranscribedErrorMqtt(sessionId: String?){

    }

    fun intentNotRecognizedMqtt(sessionId: String?){

    }

    fun intentRecognizedMqtt(sessionId: String?, intentName: String?, intent: String){

    }

    fun toggleIntentHandlingEnabledMqtt(isEnabled: Boolean ){

    }

    fun playAudioMqtt(data: List<Byte>){

    }

    fun toggleAudioOutputEnabledMqtt(isEnabled: Boolean){
        AppSettings.isAudioOutputEnabled.value = isEnabled
    }

    fun setAudioVolumeMqtt(value: Float){

    }

}