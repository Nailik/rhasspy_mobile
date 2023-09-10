package org.rhasspy.mobile.platformspecific.file

actual sealed class SystemFolderType(actual val folder: String) {

    actual data object Download : SystemFolderType("") //TODO #514

}