package org.rhasspy.mobile.logic.services.udp

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.SocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.server.engine.internal.ClosedChannelException
import io.ktor.utils.io.core.ByteReadPacket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.nativeutils.AudioRecorder.Companion.appendWavHeader
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.settings.AppSetting

class UdpService(host: String, port: Int) : IService() {
    private val logger = LogType.UdpService.logger()

    private var socketAddress: SocketAddress? = null
    private var sendChannel: SendChannel<Datagram>? = null
    private var hasLoggedError = false

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

    suspend fun streamAudio(data: ByteArray): Exception? {
        if (AppSetting.isLogAudioFramesEnabled.value) {
            logger.d { "stream audio dataSize: ${data.size}" }
        }
        socketAddress?.also {
            try {
                sendChannel?.send(Datagram(ByteReadPacket(data.appendWavHeader()), it))
            } catch (exception: Exception) {
                if (AppSetting.isLogAudioFramesEnabled.value) {
                    if (exception::class.simpleName == "JobCancellationException") {
                        //no error, can happen
                        return null
                    }
                    logger.e(exception) { "streamAudio error" }
                }
                return exception
            }
        } ?: run {
            if (!hasLoggedError) {
                hasLoggedError = true
                logger.a { "stream audio socketAddress not initialized" }
            }
        }
        return null
    }

}