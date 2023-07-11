package org.rhasspy.mobile.platformspecific.extensions

import co.touchlab.kermit.Severity
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.*
import org.rhasspy.mobile.data.log.LogElement
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask


private val fileManager = NSFileManager.defaultManager

@OptIn(ExperimentalForeignApi::class)
private fun readDocumentsDirectory(): String {
    val documentsDirectoryUrl = fileManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )!!

    createDirectoryIfNotExists(documentsDirectoryUrl)
    return documentsDirectoryUrl.absoluteString!!
}

@OptIn(ExperimentalForeignApi::class)
private fun createDirectoryIfNotExists(parentDirectory: NSURL) {
    if (!fileManager.fileExistsAtPath(parentDirectory.path!!)) {
        fileManager.createDirectoryAtPath(parentDirectory.path!!, true, null, null)
    }
}

actual fun Path.Companion.commonInternalPath(nativeApplication: NativeApplication, fileName: String): Path = "${readDocumentsDirectory().replace("file:", "")}/$fileName".toPath()

actual fun Path.commonDelete() {
    FileSystem.SYSTEM.delete(this, mustExist = false)
}

actual fun Path.commonSize(): Long? = FileSystem.SYSTEM.metadata(this).size

actual fun Path.commonSource(): Source = FileSystem.SYSTEM.source(this)

actual fun Path.commonReadWrite(): FileHandle = FileSystem.SYSTEM.openReadWrite(this, !FileSystem.SYSTEM.exists(this), mustExist = false)

actual inline fun <reified T> Path.commonDecodeLogList(): T {
    val fileSource = this.commonSource().buffer()
    val json = fileSource.use {
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
        }" + it.readUtf8() + "]"
    }
    println(json)
    return Json.decodeFromString(json)
}

actual fun Path.commonShare(
    nativeApplication: NativeApplication,
    externalResultRequest: IExternalResultRequest
): Boolean {
    //TODO("Not yet implemented")
    return true
}

actual suspend fun Path.commonSave(
    nativeApplication: NativeApplication,
    externalResultRequest: IExternalResultRequest,
    fileName: String,
    fileType: String
): Boolean {
    //TODO("Not yet implemented")
    return true
}