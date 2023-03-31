package org.rhasspy.mobile.platformspecific.extensions

import okio.FileHandle
import okio.Path
import okio.Source
import org.rhasspy.mobile.platformspecific.application.NativeApplication

expect fun Path.Companion.commonExternalPath(fileName: String): Path

expect fun Path.Companion.commonInternalPath(nativeApplication: NativeApplication, fileName: String): Path

expect fun Path.commonDelete()

expect fun Path.commonSize(): Long?

expect fun Path.commonSource(): Source

expect fun Path.commonReadWrite(): FileHandle

expect inline fun <reified T> Path.commonDecode(): T

expect fun Path.commonShare(nativeApplication: NativeApplication)

expect fun Path.commonSave(nativeApplication: NativeApplication, fileName: String, fileType: String)