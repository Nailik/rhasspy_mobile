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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.nativeutils.installCallLogging
import org.rhasspy.mobile.nativeutils.installCompression
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IServiceLink
import org.rhasspy.mobile.services.ServiceError
import org.rhasspy.mobile.services.webserver.data.WebServerCall
import org.rhasspy.mobile.services.webserver.data.WebServerCallType
import org.rhasspy.mobile.services.webserver.data.WebServerLinkStateType
import org.rhasspy.mobile.services.webserver.data.WebServerPath

//https://rhasspy.readthedocs.io/en/latest/reference/#http-api
/**
 * same endpoints as a rhasspy server sends the requests to the according service (mostly state machine)
 */
class WebServerLink(
    private val isHttpApiEnabled: Boolean,
    private val port: Int,
    private val isSSLEnabled: Boolean
) : IServiceLink {

    private val logger = Logger.withTag("WebServerLink")

    private lateinit var server: CIOApplicationEngine
    private val _receivedRequest = MutableSharedFlow<WebServerCall>()
    val receivedRequest = _receivedRequest.readOnly

    private val _currentError = MutableStateFlow<ServiceError<WebServerLinkStateType>?>(null)
    val currentError = _currentError.readOnly

    private val _isServerRunning = MutableStateFlow(false)
    val isServerRunning = _isServerRunning.readOnly

    override fun start(scope: CoroutineScope) {
        if (isHttpApiEnabled) {
            logger.v { "starting server" }
            server = getServer(port)

            scope.launch {
                //necessary else netty has problems when the coroutine scope is closed
                try {
                    server.start()
                    _isServerRunning.value = true
                } catch (e: Exception) {
                    _currentError.value = ServiceError(e, WebServerLinkStateType.STARTING, MR.strings.error)
                    logger.e(e) { "While Starting server" }
                }
            }
        } else {
            logger.v { "Server disabled" }
        }
    }

    override fun destroy() {
        if (::server.isInitialized) {
            server.stop()
        }
        _isServerRunning.value = false
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
                    try {
                        when (path.type) {
                            WebServerCallType.POST -> post(path.path) {
                                logger.v { "post ${path.path}" }
                                _receivedRequest.emit(WebServerCall(call, path))
                            }
                            WebServerCallType.GET -> get(path.path) {
                                logger.v { "get ${path.path}" }
                                _receivedRequest.emit(WebServerCall(call, path))
                            }
                        }
                    } catch (e: Exception) {
                        _currentError.value = ServiceError(e, WebServerLinkStateType.RECEIVING, MR.strings.error)
                        logger.e(e) { "While receiving" }
                    }
                }
            }
        }
    }

}
