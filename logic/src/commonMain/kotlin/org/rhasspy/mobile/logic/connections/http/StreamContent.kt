package org.rhasspy.mobile.logic.connections.http

import co.touchlab.kermit.Logger
import io.ktor.http.content.OutgoingContent
import io.ktor.utils.io.ByteWriteChannel
import okio.buffer
import okio.use
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnection
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.extensions.commonData
import org.rhasspy.mobile.platformspecific.extensions.commonSize
import org.rhasspy.mobile.platformspecific.extensions.commonSource

internal class StreamContent(private val data: AudioSource) : OutgoingContent.WriteChannelContent(), KoinComponent {

    private val logger = Logger.withTag("StreamContent")

    private val bufferSize = 1024

    override suspend fun writeTo(channel: ByteWriteChannel) {
        var bytesRead: Int

        when (data) {
            is AudioSource.Data     -> {
                for (offset in 0 until contentLength.toInt() step bufferSize) {
                    //TODO#466 what happens if last buffer is not filled?
                    channel.writeFully(data.data, offset = offset, length = bufferSize)
                    channel.flush()
                }
            }

            is AudioSource.File     -> {
                val buffer = ByteArray(bufferSize)
                data.path.commonSource().buffer().use { source ->
                    while (source.read(buffer).also { bytesRead = it } != -1 && !channel.isClosedForWrite) {
                        channel.writeFully(buffer, offset = 0, length = bytesRead)
                        channel.flush()
                    }
                }
            }

            is AudioSource.Resource -> {
                val data = data.fileResource.commonData(get<NativeApplication>())
                for (offset in 0 until contentLength.toInt() step bufferSize) {
                    //TODO#466 what happens if last buffer is not filled?
                    channel.writeFully(data, offset = offset, length = bufferSize)
                    channel.flush()
                }
            }
        }

    }

    override val contentType = WebServerConnection.audioContentType

    override val contentLength: Long = when (data) {
        is AudioSource.Data     -> data.data.size.toLong()
        is AudioSource.File     -> data.path.commonSize()
        is AudioSource.Resource -> 0L //TODO #466
    } ?: run {
        logger.a { "contentLength is null for $data" }
        0
    }

}