package org.rhasspy.mobile.platformspecific.extensions

import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import okio.*
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectResult.Result
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectResult.Success
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequestIntention
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest
import java.io.File

actual fun Path.Companion.commonInternalPath(
    nativeApplication: NativeApplication,
    fileName: String
): Path = "${nativeApplication.filesDir?.let { "$it/" } ?: ""}$fileName".toPath()

actual fun Path.Companion.commonDatabasePath(
    nativeApplication: NativeApplication,
    fileName: String
): Path = nativeApplication.getDatabasePath(fileName).toOkioPath()

actual fun Path?.commonExists(): Boolean = this?.let { !FileSystem.SYSTEM.exists(this) } ?: false

actual fun Path.commonDelete() {
    FileSystem.SYSTEM.delete(this)
}

actual fun Path.commonSize(): Long? = FileSystem.SYSTEM.metadata(this).size

actual fun Path.commonSource(): Source = this.toFile().source()

actual fun Path.commonReadWrite(): FileHandle = FileSystem.SYSTEM.openReadWrite(this, !FileSystem.SYSTEM.exists(this))

actual fun Path.commonShare(
    nativeApplication: NativeApplication,
    externalResultRequest: IExternalResultRequest
): Boolean {
    val shareFile = File("${nativeApplication.filesDir}/share-${this.name}")
    this.toFile().copyTo(shareFile, overwrite = true)

    val fileUri: Uri = FileProvider.getUriForFile(
        nativeApplication,
        nativeApplication.packageName.toString() + ".provider",
        shareFile
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