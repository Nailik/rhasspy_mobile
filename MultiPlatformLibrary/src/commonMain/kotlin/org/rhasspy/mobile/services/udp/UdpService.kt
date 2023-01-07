package org.rhasspy.mobile.services.udp

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.server.engine.internal.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.settings.AppSetting

class UdpService(host: String, port: Int) : IService() {
    private val logger = LogType.UdpService.logger()

    private var socketAddress: SocketAddress? = null
    private var sendChannel: SendChannel<Datagram>? = null

    /**
     * makes sure the address is up to date
     *
     * suspend is necessary else there is an network on main thread error at least on android
     */
    init {
        logger.d { "initialization" }
        try {
            sendChannel = aSocket(SelectorManager(Dispatchers.Default)).udp().bind().outgoing

            socketAddress = InetSocketAddress(host, port)
        } catch (exception: Exception) {
            logger.e(exception) { "initialization error" }
        }
    }

    override fun onClose() {
        logger.d { "onClose" }
        try {
            sendChannel?.close()
        } catch (exception: ClosedChannelException) {
            //nothing to important
        } catch (exception: Exception) {
            logger.a(exception) { "UdpService close exception" }
        }
        socketAddress = null
    }

    suspend fun streamAudio(data: List<Byte>): Exception? {
        if (AppSetting.isLogAudioFramesEnabled.value) {
            logger.d { "stream audio dataSize: ${data.size}" }
        }
        socketAddress?.also {
            try {
                sendChannel?.send(Datagram(ByteReadPacket(data.toByteArray()), it))
            } catch (exception: Exception) {
                if (exception::class.simpleName == "JobCancellationException") {
                    //no error, can happen
                    return null
                }
                logger.e(exception) { "streamAudio error" }
                return exception
            }
        } ?: run {
            logger.a { "stream audio socketAddress not initialized" }
        }
        return null
    }

}