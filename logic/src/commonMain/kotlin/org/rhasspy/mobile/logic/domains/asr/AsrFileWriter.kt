package org.rhasspy.mobile.logic.domains.asr

import okio.FileHandle
import okio.Path
import org.rhasspy.mobile.logic.domains.mic.MicAudioChunk
import org.rhasspy.mobile.logic.local.file.IFileStorage
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorderUtils
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonReadWrite

class AsrFileWriter(
    private val fileStorage: IFileStorage,
) {

    private var fileHandle: FileHandle? = null
    private var latestChunk: MicAudioChunk? = null

    val filePath: Path get() = fileStorage.speechToTextAudioFile

    fun openFile() {
        fileStorage.speechToTextAudioFile.commonDelete()
        fileHandle = fileStorage.speechToTextAudioFile.commonReadWrite()
    }

    fun writeToFile(chunk: MicAudioChunk) {
        latestChunk = chunk
        fileHandle?.write(
            fileOffset = fileHandle?.size() ?: 0,
            array = chunk.data,
            arrayOffset = 0,
            byteCount = chunk.data.size
        )
    }

    fun closeFile() {
        if(fileHandle == null) return

        saveAudioHeader(latestChunk ?: return)

        fileHandle?.flush()
        fileHandle?.close()
        fileHandle = null

        fileHandle?.apply {
            flush()
            close()
        }
        fileHandle = null
    }

    private fun saveAudioHeader(chunk: MicAudioChunk) {
        val header = AudioRecorderUtils.getWavHeader(
            sampleRate = chunk.sampleRate,
            encoding = chunk.encoding,
            channel = chunk.channel,
            audioSize = fileHandle?.size() ?: 0
        )
        fileHandle?.write(0, header, 0, header.size)
    }

}