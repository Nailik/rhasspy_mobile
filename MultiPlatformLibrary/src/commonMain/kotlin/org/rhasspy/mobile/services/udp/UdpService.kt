package org.rhasspy.mobile.services.udp

import co.touchlab.kermit.Logger
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import org.koin.core.component.inject
import org.rhasspy.mobile.middleware.ErrorType.UdpServiceErrorType.NotInitialized
import org.rhasspy.mobile.middleware.EventType.UdpServiceEventType.Start
import org.rhasspy.mobile.middleware.EventType.UdpServiceEventType.StreamAudio
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.ServiceResponse

class UdpService : IService() {

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
        val startEvent = serviceMiddleware.createEvent(Start)

        try {
            sendChannel = aSocket(SelectorManager(Dispatchers.Default)).udp().bind().outgoing

            socketAddress = InetSocketAddress(
                params.udpOutputHost,
                params.udpOutputPort
            )

            startEvent.success()
        } catch (e: Exception) {
            startEvent.error(e)
        }
    }

    override fun onClose() {
        sendChannel?.close()
        socketAddress = null
    }

    suspend fun streamAudio(byteData: List<Byte>): ServiceResponse<*> {
        val streamAudioEvent = serviceMiddleware.createEvent(StreamAudio)

        socketAddress?.also {
            sendChannel?.send(Datagram(ByteReadPacket(byteData.toByteArray()), it)) ?: run {
                streamAudioEvent.error(NotInitialized)
                return ServiceResponse.NotInitialized
            }
        } ?: run {
            streamAudioEvent.error(NotInitialized)
            return ServiceResponse.NotInitialized
        }
        streamAudioEvent.success()
        return ServiceResponse.Success(Unit)
    }

}