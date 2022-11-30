package org.rhasspy.mobile.services.localaudio

import org.rhasspy.mobile.nativeutils.AudioPlayer
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.ServiceResponse

class LocalAudioService : IService() {
    override fun onClose() {
       // TODO("Not yet implemented")
    }

    suspend fun playAudio(data: List<Byte>): ServiceResponse<*> { //TODO play byte array
         AudioPlayer().playData(data) {}
        return ServiceResponse.Success("yay")
    }

}