package org.rhasspy.mobile.services.localaudio

import org.rhasspy.mobile.services.IService

class LocalAudioService : IService() {
    override fun onClose() {
        TODO("Not yet implemented")
    }
    suspend fun playAudio(data: List<Byte>): Boolean {
        return true
    }

}