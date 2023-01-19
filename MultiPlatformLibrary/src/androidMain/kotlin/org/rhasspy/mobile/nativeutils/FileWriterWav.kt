package org.rhasspy.mobile.nativeutils

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.rhasspy.mobile.Application
import java.io.File

actual class FileWriterWav actual constructor(actual val filename: String) : FileWriter(filename) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var writeJob: Job? = null

    //file to write to
    override val file = File(Application.nativeInstance.cacheDir, "$filename.wav").also {
        if(!it.exists()) {
            it.createNewFile()
        }
    }

    /**
     * append a byte array to the file
     */
    actual fun appendData(byteArray: ByteArray) {
        file.appendBytes(byteArray)
    }

    /**
     * write data into file
     */
    actual fun writeData(byteArray: ByteArray) {
        file.writeBytes(byteArray)
    }

    /**
     * write data into file
     */
    actual suspend fun writeData(receiveChannel: ByteWriteChannel) {
        writeJob?.cancel()
        writeJob = scope.launch {
            file.writeBytes(ByteArray(0))
            receiveChannel.write {
                file.appendBytes(it.array())
            }
        }
    }

    /**
     * write data into file
     */
    actual suspend fun writeData(receiveChannel: ByteReadChannel) {
        writeJob?.cancel()
        writeJob = scope.launch {
            file.writeBytes(ByteArray(0))
            receiveChannel.read {
                file.appendBytes(it.array())
            }
        }
    }

    /**
     * returns the file as byte array
     */
    actual fun getContent(): ByteArray {
        return file.readBytes()
    }

    /**
     * get file content as stream
     */
    actual fun getFileContentStream(): FileStream {
        return FileStream(file)
    }

}