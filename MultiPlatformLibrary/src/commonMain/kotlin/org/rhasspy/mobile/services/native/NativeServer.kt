package org.rhasspy.mobile.services.native

import org.rhasspy.mobile.services.api.HttpCallWrapper

expect class NativeServer {

    companion object {
        fun getServer(routing: List<HttpCallWrapper>): NativeServer
    }

    fun start()

    fun stop()

}