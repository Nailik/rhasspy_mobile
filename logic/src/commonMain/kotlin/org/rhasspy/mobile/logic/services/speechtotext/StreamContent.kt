package org.rhasspy.mobile.logic.services.speechtotext

import co.touchlab.kermit.Logger
import io.ktor.http.content.OutgoingContent
import io.ktor.utils.io.ByteWriteChannel
import kotlinx.io.files.Path
import kotlinx.io.files.source
import org.rhasspy.mobile.logic.services.webserver.WebServerService
import org.rhasspy.mobile.platformspecific.file.FileUtils

private val logger = Logger.withTag("StreamContent")

internal class StreamContent(private val filePath: Path) : OutgoingContent.WriteChannelContent() {

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun writeTo(channel: ByteWriteChannel) {
        val buffer = ByteArray(1024)
        filePath.source().use { source ->
            while (!source.exhausted()) {
                val actualSize = source.readAtMostTo(buffer, buffer.size)
                channel.writeFully(buffer, offset = 0, length = actualSize)
                channel.flush()
            }
            source.close()
        }
    }

    override val contentType = WebServerService.audioContentType

    override val contentLength: Long = FileUtils.getSize(filePath)

}