package org.rhasspy.mobile.settings

enum class FileType(val folderName: String, val fileTypes: Array<String>) {
    SOUND("sounds", arrayOf("audio/x-wav")),
    CERTIFICATE("certificates", arrayOf("application/x-java-keystore", "application/octet-stream")),
    PORCUPINE("porcupine", arrayOf("application/octet-stream", "application/zip"))
}