package org.rhasspy.mobile.services.native

import io.ktor.network.sockets.*

expect object SocketService {

    fun getSocketBuilder(): SocketBuilder

}