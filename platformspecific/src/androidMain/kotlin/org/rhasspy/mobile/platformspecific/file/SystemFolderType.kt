package org.rhasspy.mobile.platformspecific.file

actual sealed class SystemFolderType(actual val folder: String) {
    actual object Download : SystemFolderType("content://com.android.externalstorage.documents/document/primary:Download")
}