package org.rhasspy.mobile.platformspecific.file

expect sealed class SystemFolderType {
    val folder: String

    object Download : SystemFolderType
}