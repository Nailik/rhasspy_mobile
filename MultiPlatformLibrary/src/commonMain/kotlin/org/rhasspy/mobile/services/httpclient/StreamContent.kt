package org.rhasspy.mobile.services.httpclient

import io.ktor.http.content.OutgoingContent
import io.ktor.utils.io.ByteWriteChannel
import org.rhasspy.mobile.nativeutils.FileWavStream
import org.rhasspy.mobile.services.webserver.WebServerService

class StreamContent(private val fileWavStream: FileWavStream): OutgoingContent.WriteChannelContent() {

    override suspend fun writeTo(channel: ByteWriteChannel) {
        fileWavStream.copyTo(channel, 1024)
    }

    override val contentType = WebServerService.audioContentType

    override val contentLength: Long = fileWavStream.length

}