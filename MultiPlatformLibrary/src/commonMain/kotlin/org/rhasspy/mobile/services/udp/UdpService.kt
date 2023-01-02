package org.rhasspy.mobile.services.udp

import co.touchlab.kermit.Logger
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import org.koin.core.component.inject
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.services.IService

class UdpService : IService() {
    private val logger = Logger.withTag("UdpService")

    private var socketAddress: SocketAddress? = null
    private var sendChannel: SendChannel<Datagram>? = null

    private val params by inject<UdpServiceParams>()

    private val serviceMiddleware by inject<IServiceMiddleware>()

    /**
     * makes sure the address is up to date
     *
     * suspend is necessary else there is an network on main thread error at least on android
     */
    init {
        try {
            sendChannel = aSocket(SelectorManager(Dispatchers.Default)).udp().bind().outgoing

            socketAddress = InetSocketAddress(
                params.udpOutputHost,
                params.udpOutputPort
            )
        } catch (exception: Exception) {
            logger.e(exception) { "" }
        }
    }

    override fun onClose() {
        sendChannel?.close()
        socketAddress = null
    }

    suspend fun streamAudio(byteData: List<Byte>) {
        socketAddress?.also {
            try {
                sendChannel?.send(Datagram(ByteReadPacket(byteData.toByteArray()), it))
                //TODO log if it returns error
            } catch (exception: Exception) {
                logger.e(exception) { "" }
            }
        } ?: run {
            //TODO log
        }
    }

}