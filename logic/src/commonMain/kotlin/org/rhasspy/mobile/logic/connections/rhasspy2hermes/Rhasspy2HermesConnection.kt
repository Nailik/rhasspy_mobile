package org.rhasspy.mobile.logic.connections.rhasspy2hermes

import co.touchlab.kermit.Logger
import io.ktor.client.request.setBody
import io.ktor.client.utils.buildHeaders
import io.ktor.http.contentType
import okio.Path
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.logic.connections.IConnection
import org.rhasspy.mobile.logic.connections.http.IHttpConnection
import org.rhasspy.mobile.logic.connections.http.StreamContent
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource.*
import org.rhasspy.mobile.platformspecific.extensions.commonData
import org.rhasspy.mobile.settings.ConfigurationSetting

internal interface IRhasspy2HermesConnection : IConnection {

    suspend fun speechToText(audioFilePath: Path): HttpClientResult<String>
    suspend fun recognizeIntent(text: String): HttpClientResult<String>
    suspend fun textToSpeech(text: String, volume: Float?, siteId: String?): HttpClientResult<ByteArray>
    suspend fun playWav(audioSource: AudioSource): HttpClientResult<String>

}

/**
 * contains client to send data to http endpoints
 *
 * functions return the result or an exception
 */
internal class Rhasspy2HermesConnection : IRhasspy2HermesConnection, IHttpConnection(ConfigurationSetting.rhasspy2Connection) {

    override val logger = Logger.withTag("Rhasspy2HermesConnection")

    /**
     * /api/speech-to-text
     * POST a WAV file and have Rhasspy return the text transcription
     * Set Accept: application/json to receive JSON with more details
     * ?noheader=true - send raw 16-bit 16Khz mono audio without a WAV header
     */
    override suspend fun speechToText(audioFilePath: Path): HttpClientResult<String> {
        logger.d { "speechToText: audioFilePath.name" }

        return post(
            url = "/api/speech-to-text",
            block = {
                setBody(StreamContent(audioFilePath))
            }
        )
    }

    /**
     * /api/text-to-intent
     * POST text and have Rhasspy process it as command
     * Returns intent JSON when command has been processed
     * ?nohass=true - stop Rhasspy from handling the intent
     * ?entity=<entity>&value=<value> - set custom entity/value in recognized intent
     *
     * returns null if the intent is not found
     */
    override suspend fun recognizeIntent(text: String): HttpClientResult<String> {
        logger.d { "recognizeIntent text: $text" }

        return post(
            url = "/api/text-to-intent",
            block = {
                setBody(text)
            }
        )
    }

    /**
     * api/text-to-speech
     * POST text and have Rhasspy speak it
     * ?voice=<voice> - override default TTS voice
     * ?language=<language> - override default TTS language or locale
     * ?repeat=true - have Rhasspy repeat the last sentence it spoke
     * ?volume=<volume> - volume level to speak at (0 = off, 1 = full volume)
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    override suspend fun textToSpeech(text: String, volume: Float?, siteId: String?): HttpClientResult<ByteArray> {
        logger.d { "textToSpeech text: $text" }

        return post(
            url = "/api/text-to-speech/${volume?.let { "?volume=$it" } ?: ""}${siteId?.let { "?siteId=$it" } ?: ""}",
            block = {
                setBody(text)
            }
        )
    }

    /**
     * /api/play-wav
     * POST to play WAV data
     * Make sure to set Content-Type to audio/wav
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    override suspend fun playWav(audioSource: AudioSource): HttpClientResult<String> {
        logger.d { "playWav size: $audioSource" }
        @Suppress("DEPRECATION", "IMPLICIT_CAST_TO_ANY")
        val body = when (audioSource) {
            is Data -> audioSource.data
            is File -> StreamContent(audioSource.path)
            is Resource -> audioSource.fileResource.commonData(nativeApplication)
        }
        return post(
            url = "/api/play-wav",
            block = {
                buildHeaders {
                    contentType(audioContentType)
                }
                setBody(body)
            },
        )
    }

}