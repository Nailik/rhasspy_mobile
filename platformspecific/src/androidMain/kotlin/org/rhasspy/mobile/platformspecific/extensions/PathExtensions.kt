package org.rhasspy.mobile.platformspecific.extensions

import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import co.touchlab.kermit.Severity
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okio.FileHandle
import okio.FileSystem
import okio.Path
import okio.Source
import okio.source
import org.rhasspy.mobile.data.log.LogElement
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectResult.Result
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectResult.Success
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequestIntention
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.SequenceInputStream
import java.util.Collections

actual fun Path.Companion.commonInternalPath(
    nativeApplication: NativeApplication,
    fileName: String
): Path = "${nativeApplication.filesDir?.let { "$it/" } ?: ""}$fileName".toPath()

actual fun Path?.commonExists(): Boolean = this?.let { !FileSystem.SYSTEM.exists(this) } ?: false

actual fun Path.commonDelete() {
    FileSystem.SYSTEM.delete(this)
}

actual fun Path.commonSize(): Long? = FileSystem.SYSTEM.metadata(this).size

actual fun Path.commonSource(): Source = this.toFile().source()

actual fun Path.commonReadWrite(): FileHandle =
    FileSystem.SYSTEM.openReadWrite(this, !FileSystem.SYSTEM.exists(this))

@OptIn(ExperimentalSerializationApi::class)
actual inline fun <reified T> Path.commonDecodeLogList(): T =
    Json.decodeFromStream(this.toFile().inputStream().modify())

fun InputStream.modify(): InputStream {
    val streams = listOf(
        ByteArrayInputStream(
            "[${
                Json.encodeToString(
                    LogElement(
                        time = "",
                        severity = Severity.Assert,
                        tag = "",
                        message = "",
                        throwable = null
                    )
                )
            }".toByteArray()
        ),
        this,
        ByteArrayInputStream("]".toByteArray())
    )
    return SequenceInputStream(Collections.enumeration(streams))
}

actual fun Path.commonShare(
    nativeApplication: NativeApplication,
    externalResultRequest: IExternalResultRequest
): Boolean {
    val fileUri: Uri = FileProvider.getUriForFile(
        nativeApplication, nativeApplication.packageName.toString() + ".provider",
        this.toFile()
    )

    val result = externalResultRequest.launch(
        ExternalResultRequestIntention.ShareFile(
            fileUri = fileUri.toString(),
            mimeType = "text/html"
        )
    )

    return result is Success
}

actual suspend fun Path.commonSave(
    nativeApplication: NativeApplication,
    externalResultRequest: IExternalResultRequest,
    fileName: String,
    fileType: String
): Boolean {

    val result = externalResultRequest.launchForResult(
        ExternalResultRequestIntention.CreateDocument(
            fileName,
            fileType
        )
    )

    return if (result is Result) {
        nativeApplication.contentResolver.openOutputStream(result.data.toUri())
            ?.also { outputStream ->
                this.toFile().inputStream().copyTo(outputStream)
                outputStream.flush()
                outputStream.close()
            }

        true
    } else false
}