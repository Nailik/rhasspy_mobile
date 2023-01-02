package org.rhasspy.mobile.services.udp

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import org.koin.core.component.inject
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.settings.AppSetting

class UdpService : IService() {
    private val logger = LogType.UdpService.logger()

    private var socketAddress: SocketAddress? = null
    private var sendChannel: SendChannel<Datagram>? = null

    private val params by inject<UdpServiceParams>()

    /**
     * makes sure the address is up to date
     *
     * suspend is necessary else there is an network on main thread error at least on android
     */
    init {
        logger.d { "initialization" }
        try {
            sendChannel = aSocket(SelectorManager(Dispatchers.Default)).udp().bind().outgoing

            socketAddress = InetSocketAddress(
                params.udpOutputHost,
                params.udpOutputPort
            )
        } catch (exception: Exception) {
            logger.e(exception) { "initialization error" }
        }
    }

    override fun onClose() {
        logger.d { "onClose" }
        sendChannel?.close()
        socketAddress = null
    }

    suspend fun streamAudio(data: List<Byte>) {
        if (AppSetting.isLogAudioFramesEnabled.value) {
            logger.d { "stream audio dataSize: ${data.size}" }
        }
        socketAddress?.also {
            try {
                sendChannel?.send(Datagram(ByteReadPacket(data.toByteArray()), it))
            } catch (exception: Exception) {
                logger.e(exception) { "streamAudio error" }
            }
        } ?: run {
            logger.e { "stream audio socketAddress not initialized" }
        }
    }

}