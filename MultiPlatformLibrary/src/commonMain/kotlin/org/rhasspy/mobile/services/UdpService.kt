package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object UdpService {
    private val logger = Logger.withTag("UdpService")

    private var socketAddress: SocketAddress? = null
    private var sendChannel: SendChannel<Datagram>? = null

    /**
     * makes sure the address is up to date
     *
     * suspend is necessary else there is an network on main thread error at least on android
     */
    @Suppress("RedundantSuspendModifier")
    suspend fun start() {
        if (!ConfigurationSettings.isUDPOutput.data) {
            logger.v { "not enabled" }
            return
        }

        logger.v { "start" }

        try {
            sendChannel = aSocket(SelectorManager(Dispatchers.Default)).udp().bind().outgoing

            socketAddress = InetSocketAddress(
                ConfigurationSettings.udpOutputHost.data,
                ConfigurationSettings.udpOutputPort.data.toInt()
            )
        } catch (e: Exception) {
            logger.e(e) { "unable to initialize address with host: ${ConfigurationSettings.udpOutputHost.data} and port ${ConfigurationSettings.udpOutputPort.data}" }
        }
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun stop() {
        sendChannel?.close()
        socketAddress = null
    }

    suspend fun streamAudio(byteData: List<Byte>) {
        val data = byteData.toByteArray()
        if (AppSettings.isLogAudioFrames.data) {
            logger.v { "streamAudio ${data.size}" }
        }

        socketAddress?.also {
            sendChannel?.send(Datagram(ByteReadPacket(data), it))
        }
    }

}