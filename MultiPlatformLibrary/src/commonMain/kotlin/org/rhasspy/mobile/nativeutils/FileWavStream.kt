package org.rhasspy.mobile.nativeutils

import io.ktor.utils.io.ByteWriteChannel

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class FileWavStream {
    val length: Long

    suspend fun copyTo(out: ByteWriteChannel, bufferSize: Int = 1024): Long

    fun close()
}