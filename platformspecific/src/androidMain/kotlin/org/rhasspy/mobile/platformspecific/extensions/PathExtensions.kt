package org.rhasspy.mobile.platformspecific.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import co.touchlab.kermit.Severity
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okio.*
import org.rhasspy.mobile.data.log.LogElement
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.SequenceInputStream
import java.util.Collections

actual fun Path.Companion.commonInternalPath(nativeApplication: NativeApplication, fileName: String): Path = "${nativeApplication.filesDir}/$fileName".toPath()

actual fun Path.commonDelete() {
    FileSystem.SYSTEM.delete(this)
}

actual fun Path.commonSize(): Long? = FileSystem.SYSTEM.metadata(this).size

actual fun Path.commonSource(): Source = this.toNioPath().source()

actual fun Path.commonReadWrite(): FileHandle = FileSystem.SYSTEM.openReadWrite(this)

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

actual fun Path.commonShare(nativeApplication: NativeApplication) {
    val fileUri: Uri = FileProvider.getUriForFile(
        nativeApplication,
        nativeApplication.packageName.toString() + ".provider",
        this.toFile()
    )

    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, fileUri)
        type = "text/html"
    }
    nativeApplication.startActivity(Intent.createChooser(shareIntent, null).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}

actual fun Path.commonSave(nativeApplication: NativeApplication, fileName: String, fileType: String) {
    nativeApplication.currentActivity?.createDocument(fileName, fileType) {
        if (it.resultCode == Activity.RESULT_OK) {
            it.data?.data?.also { uri ->
                nativeApplication.contentResolver.openOutputStream(uri)
                    ?.also { outputStream ->
                        this.toFile().inputStream().copyTo(outputStream)
                        outputStream.flush()
                        outputStream.close()
                    }
            }
        }
    }
}