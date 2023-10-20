package org.rhasspy.mobile.logic.connections.rhasspy3wyoming

import co.touchlab.kermit.Logger
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.data.connection.HttpClientResult.HttpClientError
import org.rhasspy.mobile.data.connection.HttpClientResult.Success
import org.rhasspy.mobile.logic.connections.IConnection
import org.rhasspy.mobile.logic.connections.http.IHttpConnection
import org.rhasspy.mobile.logic.connections.http.StreamContent
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.settings.ConfigurationSetting

internal interface IRhasspy3WyomingConnection : IConnection {

    /**
     * /wake/detect
     * Detect wake word in WAV input
     * Produces JSON
     */
    suspend fun detectWake(
        sampleRate: Int,
        bitRate: Int,
        channel: Int,
        data: Flow<ByteArray>,
    ): HttpClientResult<String>


    suspend fun detectWake(data: AudioSource): HttpClientResult<String>


    /**
     * /asr/transcribe
     * Transcribe audio from WAV input
     * Produces JSON
     */
    suspend fun transcribe(
        sampleRate: Int,
        bitRate: Int,
        channel: Int,
        data: Flow<ByteArray>,
    ): HttpClientResult<String>

    suspend fun transcribe(data: AudioSource): HttpClientResult<String>


    /**
     * /intent/recognize
     * Recognizes intent from text body (POST) or text (GET)
     * Override intent_program or pipeline
     */
    suspend fun recognize(text: String): HttpClientResult<String>

    /**
     * /handle/handle
     * Handles intent/text from body (POST) or input (GET)
     * Content-Type must be application/json for intent input
     */
    suspend fun handle(text: String): HttpClientResult<String>

    /**
     * /tts/synthesize
     * Synthesizes audio from text body (POST) or text (GET)
     * Produces WAV audio
     */
    suspend fun synthesize(text: String): HttpClientResult<ByteArray>

    /**
     * /snd/play
     * Plays WAV audio via snd
     */
    suspend fun play(
        sampleRate: Int,
        bitRate: Int,
        channel: Int,
        data: Flow<ByteArray>,
    ): HttpClientResult<String>

    /**
     * /snd/play
     * Plays WAV audio via snd
     */
    suspend fun play(data: AudioSource): HttpClientResult<String>

}

/**
 * contains client to send data to http endpoints
 *
 * functions return the result or an exception
 */
//TODO IHttpConnection needs websockets for result (error handling) and authentication
internal class Rhasspy3WyomingConnection : IRhasspy3WyomingConnection, IHttpConnection(ConfigurationSetting.rhasspy3Connection) {

    override val logger = Logger.withTag("Rhasspy3WyomingConnection")

    override suspend fun detectWake(
        sampleRate: Int,
        bitRate: Int,
        channel: Int,
        data: Flow<ByteArray>
    ): HttpClientResult<String> {
        val result = postWebsocket(
            path = "/wake/detect",
            request = {
                buildMessage {
                    put("rate", sampleRate)
                    put("width", bitRate)
                    put("channels", channel)
                }
            },
        ) {
            data.collectLatest { send(Frame.Binary(true, it)) } //TODO check if stopped by close
        }

        return when (result) {
            is Success         -> Success(result.data.data.toString())
            is HttpClientError -> result.toType()
        }
    }

    override suspend fun detectWake(data: AudioSource): HttpClientResult<String> {
        return post(
            url = "/wake/detect",
            block = {
                setBody(StreamContent(data))
            }
        )
    }

    override suspend fun transcribe(sampleRate: Int, bitRate: Int, channel: Int, data: Flow<ByteArray>): HttpClientResult<String> {
        val result = postWebsocket(
            path = "/asr/transcribe",
            request = {
                buildMessage {
                    put("rate", sampleRate)
                    put("width", bitRate)
                    put("channels", channel)
                }
            },
        ) {
            data.collectLatest { send(Frame.Binary(true, it)) }
        }

        return when (result) {
            is Success         -> Success(result.data.data.toString())
            is HttpClientError -> result.toType()
        }
    }

    override suspend fun transcribe(data: AudioSource): HttpClientResult<String> {
        return post(
            url = "/asr/transcribe",
            block = {
                setBody(StreamContent(data))
            }
        )
    }

    override suspend fun recognize(text: String): HttpClientResult<String> {
        return post(
            url = "/intent/recognize",
            block = {
                buildMessage {
                    put("text", text)
                }
            }
        )
    }

    override suspend fun handle(text: String): HttpClientResult<String> {
        return post(
            url = "/intent/handle",
            block = {
                buildMessage {
                    put("text", text)
                }
            }
        )
    }

    override suspend fun synthesize(text: String): HttpClientResult<ByteArray> {
        return post(
            url = "/tts/synthesize",
            block = {
                buildMessage {
                    put("text", text)
                }
            }
        )
    }

    override suspend fun play(
        sampleRate: Int,
        bitRate: Int,
        channel: Int,
        data: Flow<ByteArray>,
    ): HttpClientResult<String> {
        val result = postWebsocket(
            path = "/snd/play",
            request = {
                buildMessage {
                    put("rate", sampleRate)
                    put("width", bitRate)
                    put("channels", channel)
                }
            },//rate, width, channels, data
            block = {
                data.collectLatest { send(Frame.Binary(true, it)) }
            }
        )

        return when (result) {
            is Success -> Success(result.data.data.toString())
            is HttpClientError -> result.toType()
        }
    }

    override suspend fun play(data: AudioSource): HttpClientResult<String> {
        return post(
            url = "/snd/play",
            block = {
                setBody(StreamContent(data))
            }
        )
    }

    private fun HttpRequestBuilder.buildMessage(builderAction: JsonObjectBuilder.() -> Unit) {
        setBody(Json.encodeToString(buildJsonObject(builderAction)))
    }

}