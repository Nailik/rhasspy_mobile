package org.rhasspy.mobile.logic.fileutils

sealed class FolderType(protected val folderName: String, val fileTypes: Array<String>) {
    companion object {
        fun values(): List<FolderType> {
            val list = mutableListOf<FolderType>()
            list.addAll(SoundFolder.getValues())
            list.addAll(CertificateFolder.getValues())
            list.add(PorcupineFolder)
            return list
        }
    }

    sealed class SoundFolder(private val subfolder: String) :
        FolderType("sounds", arrayOf("audio/x-wav")) {
        object Wake : SoundFolder("wake")
        object Recorded : SoundFolder("recorded")
        object Error : SoundFolder("error")

        override fun toString(): String {
            return "$folderName/$subfolder"
        }

        companion object {
            fun getValues() = listOf(Wake, Recorded, Error)
        }
    }

    sealed class CertificateFolder(private val subfolder: String) : FolderType(
        "certificates",
        arrayOf("application/x-java-keystore", "application/octet-stream")
    ) {
        object WebServer : CertificateFolder("webserver")
        object Mqtt : CertificateFolder("mqtt")

        override fun toString(): String {
            return "$folderName/$subfolder"
        }

        companion object {
            fun getValues() = listOf(WebServer, Mqtt)
        }
    }

    object PorcupineFolder :
        FolderType("porcupine", arrayOf("application/octet-stream", "application/zip"))

    override fun toString(): String {
        return folderName
    }

}