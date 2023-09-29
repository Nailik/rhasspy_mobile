package org.rhasspy.mobile.logic.domains

import okio.FileHandle
import okio.Path
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorderUtils
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonReadWrite

class AudioFileWriter(
    val path: Path,
    private val channel: Int,
    private val sampleRate: Int,
    private val bitRate: Int,
) {

    private var fileHandle: FileHandle? = null

    fun openFile() {
        path.commonDelete()
        fileHandle = path.commonReadWrite()
    }

    fun writeToFile(data: ByteArray) {
        fileHandle?.write(
            fileOffset = fileHandle?.size() ?: 0,
            array = data,
            arrayOffset = 0,
            byteCount = data.size
        )
    }

    fun closeFile() {
        if (fileHandle == null) return

        val header = AudioRecorderUtils.getWavHeader(
            sampleRate = sampleRate,
            bitRate = bitRate,
            channel = channel,
            audioSize = fileHandle?.size() ?: 0
        )
        fileHandle?.write(0, header, 0, header.size)

        fileHandle?.flush()
        fileHandle?.close()
        fileHandle = null

        fileHandle?.apply {
            flush()
            close()
        }
        fileHandle = null
    }

}