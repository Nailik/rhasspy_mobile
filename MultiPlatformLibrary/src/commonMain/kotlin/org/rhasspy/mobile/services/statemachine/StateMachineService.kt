package org.rhasspy.mobile.services.statemachine

import co.touchlab.kermit.Logger
import org.koin.core.component.inject
import org.rhasspy.mobile.services.IService

//TODO mqtt say finished
//TODO mqtt play finished
class StateMachineService: IService() {

    val logger = Logger.withTag("StateMachineService")

    private val params by inject<StateMachineServiceParams>()

    override fun onClose() {

    }

    //other from webserver (audio)

    fun playRecordingPostWebServer() {
      //  TODO("Not yet implemented")
    }

    fun playRecordingGetWebServer(): List<Byte> {
  //      TODO()
        return listOf()
    }

    fun playWavWebServer(toList: List<Byte>) {
   //     TODO("Not yet implemented")
    }

    fun sayWebServer(receive: ByteArray) {

    }

    fun playAudioMqtt(toList: List<Byte>) {
    //    TODO("Not yet implemented")
    }

}