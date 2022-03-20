package org.rhasspy.mobile.services.native

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers

actual object SocketService {

    actual fun getSocketBuilder(): SocketBuilder {
        return aSocket(ActorSelectorManager(Dispatchers.IO))
    }

}