package org.rhasspy.mobile.services.udp

import co.touchlab.kermit.Logger
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import org.koin.core.component.inject
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.ServiceResponse
import org.rhasspy.mobile.services.ServiceWatchdog
import org.rhasspy.mobile.services.statemachine.StateMachineService

class UdpService : IService() {
    private val logger = Logger.withTag("UdpService")

    private var socketAddress: SocketAddress? = null
    private var sendChannel: SendChannel<Datagram>? = null

    private val params by inject<UdpServiceParams>()
    private val stateMachineService by inject<StateMachineService>()
    private val serviceWatchdog by inject<ServiceWatchdog>()

    /**
     * makes sure the address is up to date
     *
     * suspend is necessary else there is an network on main thread error at least on android
     */
    init {
        if (params.isUdpOutputEnabled) {
            logger.v { "start" }

            try {
                sendChannel = aSocket(SelectorManager(Dispatchers.Default)).udp().bind().outgoing

                socketAddress = InetSocketAddress(
                    params.udpOutputHost,
                    params.udpOutputPort
                )
            } catch (e: Exception) {
                serviceWatchdog.udpServiceError(e)
                logger.e(e) { "unable to initialize address with host: ${params.udpOutputHost} and port ${params.udpOutputPort}" }
            }
        } else {
            logger.v { "not enabled" }
        }
    }

    override fun onClose() {
        sendChannel?.close()
        socketAddress = null
    }

    suspend fun streamAudio(byteData: List<Byte>): ServiceResponse<*> {
        socketAddress?.also {
            sendChannel?.send(Datagram(ByteReadPacket(byteData.toByteArray()), it)) ?: run {
                return ServiceResponse.NotInitialized()
            }
        } ?: run {
            return ServiceResponse.NotInitialized()
        }
        return ServiceResponse.Success(Unit)
    }

}