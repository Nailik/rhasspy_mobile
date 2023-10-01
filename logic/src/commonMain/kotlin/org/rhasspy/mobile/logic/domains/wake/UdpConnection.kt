package org.rhasspy.mobile.logic.domains.wake

import co.touchlab.kermit.Logger
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.SocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.server.engine.internal.ClosedChannelException
import io.ktor.utils.io.core.ByteReadPacket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.SendChannel
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorderUtils.appendWavHeader
import org.rhasspy.mobile.settings.AppSetting

internal class UdpConnection(
    private val host: String,
    private val port: Int
) {
    private val logger = Logger.withTag("UdpConnection")

    private var socketAddress: SocketAddress? = null
    private var sendChannel: SendChannel<Datagram>? = null
    private var hasLoggedError = false

    /**
     * makes sure the address is up to date
     *
     * suspend is necessary else there is an network on main thread error at least on android
     */
    suspend fun connect(): Exception? {
        logger.d { "initialization" }
        return try {
            val channel = aSocket(SelectorManager(Dispatchers.IO)).udp().bind().outgoing
            sendChannel = channel

            val socket = InetSocketAddress(host, port)
            socketAddress = socket

            return try {
                channel.send(
                    Datagram(
                        packet = ByteReadPacket(ByteArray(1)),
                        address = socket
                    )
                )
                null
            } catch (exception: Exception) {
                logger.e(exception) { "initialization error" }
                exception
            }

        } catch (exception: Exception) {
            logger.e(exception) { "initialization error" }
            exception
        }
    }

    fun close() {
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

    suspend fun streamAudio(
        sampleRate: AudioFormatSampleRateType,
        encoding: AudioFormatEncodingType,
        channel: AudioFormatChannelType,
        data: ByteArray,
    ): Exception? {
        val dataToSend = data.appendWavHeader(
            sampleRate = sampleRate.value,
            bitRate = encoding.bitRate,
            channel = channel.value,
        )

        if (AppSetting.isLogAudioFramesEnabled.value) {
            logger.d { "stream audio dataSize: ${dataToSend.size}" }
        }
        socketAddress?.also {
            try {
                sendChannel?.send(
                    Datagram(
                        packet = ByteReadPacket(dataToSend),
                        address = it
                    )
                )
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