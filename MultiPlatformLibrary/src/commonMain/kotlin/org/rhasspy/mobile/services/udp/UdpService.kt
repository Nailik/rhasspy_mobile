package org.rhasspy.mobile.services.udp

import co.touchlab.kermit.Logger
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import org.koin.core.component.inject
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.settings.AppSettings

class UdpService : IService() {
    private val logger = Logger.withTag("UdpService")

    private var socketAddress: SocketAddress? = null
    private var sendChannel: SendChannel<Datagram>? = null

    private val params by inject<UdpServiceParams>()

    /**
     * makes sure the address is up to date
     *
     * suspend is necessary else there is an network on main thread error at least on android
     */
    fun start(scope: CoroutineScope) {
        if (params.isUdpOutputEnabled) {

            logger.v { "start" }

            try {
                sendChannel = aSocket(SelectorManager(Dispatchers.Default)).udp().bind().outgoing

                socketAddress = InetSocketAddress(
                    params.udpOutputHost,
                    params.udpOutputPort
                )
            } catch (e: Exception) {
                logger.e(e) { "unable to initialize address with host: ${params.udpOutputHost} and port ${params.udpOutputPort}" }
            }
        } else {
            logger.v { "not enabled" }
        }
    }

    fun stop() {
        sendChannel?.close()
        socketAddress = null
    }

    suspend fun streamAudio(byteData: List<Byte>) {
        val data = byteData.toByteArray()

        if (AppSettings.isLogAudioFramesEnabled.value) {
            logger.v { "streamAudio ${data.size}" }
        }

        socketAddress?.also {
            sendChannel?.send(Datagram(ByteReadPacket(data), it))
        }
    }

}