package org.rhasspy.mobile.logic.connections.http

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.*
import io.ktor.client.utils.buildHeaders
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.data.connection.HttpClientResult.HttpClientError
import org.rhasspy.mobile.data.connection.HttpConnectionData
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.ConnectionState
import org.rhasspy.mobile.data.service.ConnectionState.*
import org.rhasspy.mobile.data.viewstate.TextWrapper.TextWrapperStableStringResource
import org.rhasspy.mobile.logic.connections.IConnection
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.ktor.configureEngine
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.ISetting

internal abstract class IHttpConnection(settings: ISetting<HttpConnectionData>) : IConnection, KoinComponent {

    protected abstract val logger: Logger

    override val connectionState = MutableStateFlow<ConnectionState>(Loading)

    protected val nativeApplication by inject<NativeApplication>()

    protected val audioContentType = ContentType("audio", "wav")
    protected val jsonContentType = ContentType("application", "json")
    protected fun HttpMessageBuilder.authorization(bearerToken: String?) = bearerToken?.let { this.header("Authorization", "Bearer $bearerToken") } ?: this

    protected var httpConnectionParams = settings.value

    private var coroutineScope = CoroutineScope(Dispatchers.IO)

    protected var httpClient: HttpClient? = null

    private var testConnectionJob: Job? = null

    /**
     * builds client
     */
    private fun buildClient(params: HttpConnectionData): HttpClient {
        return HttpClient(CIO) {
            expectSuccess = true
            install(WebSockets)
            install(HttpTimeout) {
                requestTimeoutMillis = params.timeout.inWholeMilliseconds
            }
            engine {
                configureEngine(params.isSSLVerificationDisabled)
            }
        }
    }

    private suspend fun testConnection() {
        connectionState.value = Loading
        connectionState.value = try {
            httpClient?.request(httpConnectionParams.host) {
                headers {
                    append(HttpHeaders.Accept, "*/*")
                    authorization(httpConnectionParams.bearerToken)
                }
            }
            Success
        } catch (exception: Exception) {
            ErrorState(exception)
        }
    }

    init {
        coroutineScope.launch {
            settings.data.collectLatest(::collectParams)
        }
    }

    private suspend fun collectParams(params: HttpConnectionData) {
        httpConnectionParams = params
        httpClient?.cancel()

        try {
            httpClient = buildClient(params)
            testConnectionJob?.cancel()
            testConnectionJob = coroutineScope.launch {
                testConnection()
            }
        } catch (exception: Exception) {
            logger.e(exception) { "error on building client" }
            connectionState.value = ErrorState(exception)
        }
    }

    fun close() {
        coroutineScope.cancel()
        httpClient?.close()
    }

    protected suspend inline fun postWebsocket(
        path: String,
        noinline request: HttpRequestBuilder.() -> Unit,
        noinline block: suspend DefaultClientWebSocketSession.() -> Unit
    ): HttpClientResult<Frame> {
        val resultFlow = MutableSharedFlow<Frame>()

        httpClient?.let { client ->
            try {
                client.webSocket(
                    method = HttpMethod.Post,
                    request = {
                        request() //TODO automatically wss?
                        buildHeaders {
                            authorization(httpConnectionParams.bearerToken)
                        }
                    },
                    path = path,
                    block = {
                        block()
                        val received = incoming.receive()
                        logger.e { "received $received" }
                        resultFlow.emit(received)
                        close()
                    }
                )
            } catch (exception: Exception) {
                logger.e(exception) { "post result error" }
                mapError<Frame>(exception)
            }
        } ?: run {
            logger.a { "post client not initialized" }
            HttpClientError<Frame>(TextWrapperStableStringResource(MR.strings.unknown_error.stable))
        }

        val result = HttpClientResult.Success(resultFlow.first())

        connectionState.value = result.toConnectionState()

        return result
    }

    /**
     * post data to endpoint
     * handles even in event logger
     */
    protected suspend inline fun <reified T> post(
        url: String,
        crossinline block: HttpRequestBuilder.() -> Unit,
    ): HttpClientResult<T> {
        val result = httpClient?.let { client ->
            try {
                val request = client.post("${httpConnectionParams.host}$url") {
                    block()
                    buildHeaders {
                        authorization(httpConnectionParams.bearerToken)
                    }
                }
                val result = request.body<T>()
                if (result is ByteArray) {
                    logger.d { "post result size: ${result.size}" }
                } else {
                    logger.d { "post result data: $result" }
                }

                HttpClientResult.Success(result)

            } catch (exception: Exception) {

                logger.e(exception) { "post result error" }
                mapError(exception)

            }
        } ?: run {
            logger.a { "post client not initialized" }
            HttpClientError(TextWrapperStableStringResource(MR.strings.unknown_error.stable))
        }

        connectionState.value = result.toConnectionState()

        return result
    }

    /**
     * Evaluate if the Error is a know exception to help the user
     */
    protected fun <T> mapError(exception: Exception): HttpClientResult<T> {
        val type = if (exception::class.simpleName == "IllegalArgumentException") {
            if (exception.message == "Invalid TLS record type code: 72") {
                MR.strings.invalid_tls_record_type.stable
            } else {
                MR.strings.illegal_argument_exception.stable
            }
        } else if (exception::class.simpleName == "UnresolvedAddressException") {
            MR.strings.unresolved_address_exception.stable
        } else if (exception::class.simpleName == "ConnectException") {
            if (exception.message == "Connection refused") {
                MR.strings.connection_refused.stable
            } else {
                MR.strings.connection_exception.stable
            }
            MR.strings.unresolved_address_exception.stable
        } else {
            null
        }

        return HttpClientError(type ?: return HttpClientError(exception))
    }


}