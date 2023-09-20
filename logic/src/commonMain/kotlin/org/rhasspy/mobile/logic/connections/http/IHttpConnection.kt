package org.rhasspy.mobile.logic.connections.http

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.utils.buildHeaders
import io.ktor.http.ContentType
import io.ktor.http.HttpMessageBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.data.connection.HttpClientErrorType
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.data.connection.HttpConnectionData
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.ErrorState
import org.rhasspy.mobile.data.service.ServiceState.Pending
import org.rhasspy.mobile.logic.connections.IConnection
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.ktor.configureEngine
import org.rhasspy.mobile.settings.ISetting

abstract class IHttpConnection(settings: ISetting<HttpConnectionData>) : IConnection, KoinComponent {

    protected abstract val logger: Logger

    override val connectionState = MutableStateFlow<ServiceState>(Pending)

    protected val nativeApplication by inject<NativeApplication>()

    protected val audioContentType = ContentType("audio", "wav")
    protected val jsonContentType = ContentType("application", "json")
    protected fun HttpMessageBuilder.authorization(bearerToken: String?) = bearerToken?.let { this.header("Authorization", "Bearer $bearerToken") } ?: this


    protected var httpConnectionParams = settings.value

    protected var coroutineScope = CoroutineScope(Dispatchers.IO)

    protected var httpClient: HttpClient? = null

    /**
     * builds client
     */
    private fun buildClient(params: HttpConnectionData): HttpClient {
        return HttpClient(CIO) {
            expectSuccess = true
            install(WebSockets)
            install(HttpTimeout) {
                requestTimeoutMillis = params.timeout
            }
            engine {
                configureEngine(params.isSSLVerificationDisabled)
            }
        }
    }

    init {
        coroutineScope.launch {
            settings.data.collectLatest(::collectParams)
        }
    }

    private fun collectParams(params: HttpConnectionData) {
        connectionState.value = Pending
        httpConnectionParams = params
        httpClient?.cancel()

        try {
            httpClient = buildClient(params)
            connectionState.value = Pending
        } catch (exception: Exception) {
            logger.e(exception) { "error on building client" }
            connectionState.value = ErrorState.Exception(exception = exception)
        }
    }

    fun close() {
        coroutineScope.cancel()
        httpClient?.close()
    }

    /**
     * post data to endpoint
     * handles even in event logger
     */
    protected inline fun <reified T> post(
        url: String,
        crossinline block: HttpRequestBuilder.() -> Unit,
        crossinline onResult: (result: HttpClientResult<T>) -> Unit
    ) {
        coroutineScope.launch {
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
                HttpClientResult.Error(Exception())
            }

            connectionState.value = result.toServiceState()

            onResult(result)
        }
    }

    /**
     * Evaluate if the Error is a know exception to help the user
     */
    protected fun <T> mapError(exception: Exception): HttpClientResult<T> {
        val type = if (exception::class.simpleName == "IllegalArgumentException") {
            if (exception.message == "Invalid TLS record type code: 72") {
                HttpClientErrorType.InvalidTLSRecordType
            } else {
                HttpClientErrorType.IllegalArgumentError
            }
        } else if (exception::class.simpleName == "UnresolvedAddressException") {
            HttpClientErrorType.UnresolvedAddressError
        } else if (exception::class.simpleName == "ConnectException") {
            if (exception.message == "Connection refused") {
                HttpClientErrorType.ConnectionRefused
            } else {
                HttpClientErrorType.ConnectError
            }
            HttpClientErrorType.UnresolvedAddressError
        } else {
            null
        }

        return if (type == null) HttpClientResult.Error(exception) else HttpClientResult.KnownError(type)
    }


}