package org.rhasspy.mobile.fileutils

import org.rhasspy.mobile.nativeutils.FileWriterWav

object SoundCacheFileWriterFactory {

    private val fileWriters = mutableListOf<FileWriterWav>()

    fun getFileWriter(fileType: SoundCacheFileType) : FileWriterWav {
        return fileWriters.firstOrNull { it.filename == fileType.name } ?: run {
            val newFileWriter = FileWriterWav(fileType.name)
            newFileWriter
        }
    }

}