package org.rhasspy.mobile.platformspecific.extensions

import kotlinx.serialization.json.Json
import okio.FileHandle
import okio.FileSystem
import okio.Path
import okio.Source
import kotlinx.serialization.decodeFromString
import kotlinx.cinterop.*
import platform.Foundation.*
import org.rhasspy.mobile.platformspecific.application.NativeApplication


private val fileManager = NSFileManager.defaultManager

private fun readDocumentsDirectory() : String {
    val documentsDirectoryUrl =
        fileManager.URLForDirectory(
            NSDocumentDirectory,
            NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null)!!

    createDirectoryIfNotExists(documentsDirectoryUrl)
    return documentsDirectoryUrl.absoluteString!!
}

private fun createDirectoryIfNotExists(parentDirectory: NSURL) {
    if (!fileManager.fileExistsAtPath(parentDirectory.path!!)) {
        fileManager.createDirectoryAtURL(parentDirectory, false, null, null)
    }
}

private fun createFileIfNotExists(path: String) {
    if (!fileManager.fileExistsAtPath(path)) {
        fileManager.createFileAtPath(path, null, null)
    }
}

actual fun Path.Companion.commonInternalPath(nativeApplication: NativeApplication, fileName: String) : Path {
    val path = "${readDocumentsDirectory()}/$fileName".toPath()
    createFileIfNotExists(path.toString())
    return path
}

actual fun Path.commonDelete() {
    FileSystem.SYSTEM.delete(this, mustExist = false)
}

actual fun Path.commonSize(): Long? = FileSystem.SYSTEM.metadata(this).size

actual fun Path.commonSource(): Source = FileSystem.SYSTEM.source(this)

actual fun Path.commonReadWrite(): FileHandle = FileSystem.SYSTEM.openReadWrite(this, mustCreate = true, mustExist = false)

actual inline fun <reified T> Path.commonDecodeLogList(): T = Json.decodeFromString("")

actual fun Path.commonShare(nativeApplication: NativeApplication): Boolean {
    //TODO("Not yet implemented")
    return true
}

actual suspend fun Path.commonSave(nativeApplication: NativeApplication, fileName: String, fileType: String): Boolean {
    //TODO("Not yet implemented")
    return true
}