package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import io.ktor.utils.io.*
import org.rhasspy.mobile.services.native.SocketService
import org.rhasspy.mobile.settings.ConfigurationSettings
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object UdpService {
    private val logger = Logger.withTag(this::class.simpleName!!)


    private var channel: ByteWriteChannel? = null
    private var socket: ConnectedDatagramSocket? = null

    /**
     * makes sure the address is up to date
     *
     * suspend is necessary else there is an network on main thread error at least on android
     */
    @Suppress("RedundantSuspendModifier")
    suspend fun start() {
        logger.v { "start" }
        try {
            socket = SocketService.getSocketBuilder().udp().connect(
                NetworkAddress(
                    ConfigurationSettings.udpOutputHost.data,
                    ConfigurationSettings.udpOutputPort.data.toInt()
                )
            )
            channel = socket?.openWriteChannel(true)

        } catch (e: Exception) {
            logger.e(e) { "unable to initialize address with host: ${ConfigurationSettings.udpOutputHost.data} and port ${ConfigurationSettings.udpOutputPort.data}" }
        }
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun stop() {
        channel?.close()
        socket?.close()
    }

    suspend fun streamAudio(byteData: List<Byte>) {
        val data = byteData.toByteArray()
        logger.v { "streamAudio ${data.size}" }

        channel?.writeFully(data)
    }

}