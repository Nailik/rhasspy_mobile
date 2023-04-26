package org.rhasspy.mobile.platformspecific.extensions

import okio.FileHandle
import okio.FileSystem
import okio.Path
import okio.Source
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual fun Path.Companion.commonInternalPath(nativeApplication: NativeApplication, fileName: String): Path = fileName.toPath()

actual fun Path.commonDelete() {
    FileSystem.SYSTEM.delete(this, mustExist = false)
}

actual fun Path.commonSize(): Long? = FileSystem.SYSTEM.metadata(this).size

actual fun Path.commonSource(): Source = FileSystem.SYSTEM.source(this)

actual fun Path.commonReadWrite(): FileHandle = FileSystem.SYSTEM.openReadWrite(this, mustCreate = true, mustExist = false)

actual inline fun <reified T> Path.commonDecodeLogList(): T = TODO("Not yet implemented")

actual fun Path.commonShare(nativeApplication: NativeApplication): Boolean {
    TODO("Not yet implemented")
}

actual suspend fun Path.commonSave(nativeApplication: NativeApplication, fileName: String, fileType: String): Boolean {
    TODO("Not yet implemented")
}