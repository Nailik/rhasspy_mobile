package org.rhasspy.mobile.nativeutils

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel

/**
 * write file with name and maximum size to app storage
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class FileWriterWav(filename: String) : FileWriter {

    val filename : String

    /**
     * append a byte array to the file
     */
    fun appendData(byteArray: ByteArray)

    /**
     * write data into file
     */
    suspend fun writeData(receiveChannel: ByteReadChannel)

    /**
     * write data into file
     */
    suspend fun writeData(receiveChannel: ByteWriteChannel)

    /**
     * write data into file
     */
    fun writeData(byteArray: ByteArray)

    /**
     * returns the file as byte array
     */
    fun getContent(): ByteArray

    /**
     * get file content as stream
     */
    fun getFileContentStream(): FileStream

}