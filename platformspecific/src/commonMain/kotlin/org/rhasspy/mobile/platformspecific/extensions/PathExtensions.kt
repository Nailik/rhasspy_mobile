package org.rhasspy.mobile.platformspecific.extensions

import okio.FileHandle
import okio.Path
import okio.Source
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest

expect fun Path.Companion.commonInternalPath(
    nativeApplication: NativeApplication,
    fileName: String
): Path

expect fun Path.Companion.commonDatabasePath(
    nativeApplication: NativeApplication,
    fileName: String
): Path


expect fun Path?.commonExists(): Boolean

expect fun Path.commonDelete()

expect fun Path.commonSize(): Long?

expect fun Path.commonSource(): Source

expect fun Path.commonReadWrite(): FileHandle

expect fun Path.commonShare(
    nativeApplication: NativeApplication,
    externalResultRequest: IExternalResultRequest
): Boolean

expect suspend fun Path.commonSave(
    nativeApplication: NativeApplication,
    externalResultRequest: IExternalResultRequest,
    fileName: String,
    fileType: String
): Boolean