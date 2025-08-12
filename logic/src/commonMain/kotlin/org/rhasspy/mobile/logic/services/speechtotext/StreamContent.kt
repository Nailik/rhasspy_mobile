package org.rhasspy.mobile.logic.services.speechtotext

import co.touchlab.kermit.Logger
import io.ktor.http.content.OutgoingContent
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeFully
import okio.Path
import okio.buffer
import okio.use
import org.rhasspy.mobile.logic.services.webserver.WebServerService
import org.rhasspy.mobile.platformspecific.extensions.commonSize
import org.rhasspy.mobile.platformspecific.extensions.commonSource

private val logger = Logger.withTag("StreamContent")

internal class StreamContent(private val filePath: Path) : OutgoingContent.WriteChannelContent() {

    override suspend fun writeTo(channel: ByteWriteChannel) {
        val buffer = ByteArray(1024)
        filePath.commonSource().buffer().use { source ->
            while (source.read(buffer) != -1 && !channel.isClosedForWrite) {
                channel.writeFully(buffer)
                channel.flush()
            }
        }
    }

    override val contentType = WebServerService.audioContentType

    override val contentLength: Long = filePath.commonSize() ?: run {
        logger.a { "contentLength is null for ${filePath.name}" }
        0
    }

}