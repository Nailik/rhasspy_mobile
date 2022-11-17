package org.rhasspy.mobile.services.webserver

import co.touchlab.kermit.Logger
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.dataconversion.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.nativeutils.installCallLogging
import org.rhasspy.mobile.nativeutils.installCompression
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.webserver.WebServerServiceStateType.RECEIVING
import org.rhasspy.mobile.services.webserver.WebServerServiceStateType.STARTING

//https://rhasspy.readthedocs.io/en/latest/reference/#http-api
/**
 * same endpoints as a rhasspy server sends the requests to the according service (mostly state machine)
 */
class WebServerService(
    private val isHttpApiEnabled: Boolean,
    private val port: Int,
    private val isSSLEnabled: Boolean
) : IService() {
    private val logger = Logger.withTag("WebServerService")
    private var server: CIOApplicationEngine? = null
    private val _receivedRequest = MutableSharedFlow<Pair<ApplicationCall, WebServerPath>>()
    val receivedRequest: SharedFlow<Pair<ApplicationCall, WebServerPath>> = _receivedRequest

    fun start() {
        if (isHttpApiEnabled) {
            loading(STARTING)

            logger.v { "starting server" }
            server = getServer(port)

            CoroutineScope(Dispatchers.Default).launch {
                //necessary else netty has problems when the coroutine scope is closed
                try {
                    server?.start()
                    success(STARTING)
                } catch (e: Exception) {
                    error(STARTING, e.cause?.message ?: e.message)
                }
            }
        } else {
            logger.v { "Server disabled" }
        }
    }
    fun destroy() {
        server?.stop()
    }

    private fun getServer(port: Int): CIOApplicationEngine {
        return embeddedServer(factory = CIO, port = port, watchPaths = emptyList()) {
            //install(WebSockets)
            installCallLogging()
            install(DataConversion)
            //Greatly reduces the amount of data that's needed to be sent to the client by
            //gzipping outgoing content when applicable.
            installCompression()

            // configures Cross-Origin Resource Sharing. CORS is needed to make calls from arbitrary
            // JavaScript clients, and helps us prevent issues down the line.
            install(CORS) {
                methods.add(HttpMethod.Get)
                methods.add(HttpMethod.Post)
                methods.add(HttpMethod.Delete)
                anyHost()
            }

            routing {
                WebServerPath.values().forEach { path ->
                    when (path.type) {
                        WebServerCallType.POST -> post(path.path) {
                            logger.v { "post ${path.path}" }
                            _receivedRequest.emit(Pair(call, path))
                            success(RECEIVING, path.path)
                        }
                        WebServerCallType.GET -> get(path.path) {
                            logger.v { "get ${path.path}" }
                            _receivedRequest.emit(Pair(call, path))
                            success(RECEIVING, path.path)
                        }
                    }
                }
            }
        }
    }

}
