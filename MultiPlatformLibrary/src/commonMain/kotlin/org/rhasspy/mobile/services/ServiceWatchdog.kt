package org.rhasspy.mobile.services

import org.rhasspy.mobile.mqtt.MqttError
import org.rhasspy.mobile.services.hotword.HotWordServiceError

class ServiceWatchdog : IService() {

    //gets information when a service didn't start sucessfull
    override fun onClose() {
        //nothing to do
    }

    fun hotWordServiceError(error: HotWordServiceError) {

    }

    fun httpClientServiceError(e: Exception) {
        TODO("Not yet implemented")
    }

    fun indicationServiceOverlayPermissionMissing() {
        TODO("Not yet implemented")
    }

    fun mqttServiceStartError(it: MqttError) {
        TODO("Not yet implemented")
    }

    fun udpServiceError(e: Exception) {
        TODO("Not yet implemented")
    }

    fun webServerServiceStartError(e: Exception) {
        TODO("Not yet implemented")
    }

}