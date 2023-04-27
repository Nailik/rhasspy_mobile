package org.rhasspy.mobile.platformspecific.audiorecorder

import org.rhasspy.mobile.data.audiorecorder.AudioRecorderChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderSampleRateType

object AudioRecorderUtils {

    private val sampleRate get() = AudioRecorderSampleRateType.default.value //in hz
    private val channel get() = AudioRecorderChannelType.default.value.toByte()
    private val bitRate get() = AudioRecorderEncodingType.default.bitRate
    private val byteRate get() = (sampleRate * channel * bitRate) / 8

    /**
     * use the settings of the audio recorder
     * (samplingRate, channels, bitrate) and the audioSize
     * to create wav header and add it in front of the given data
     */
    fun ByteArray.appendWavHeader(): ByteArray =
        getWavHeader(this.size.toLong()) + this

    fun getWavHeader(audioSize: Long): ByteArray {
        val totalLength = audioSize + 36
        val header = arrayOf(
            'R'.code.toByte(),
            'I'.code.toByte(),
            'F'.code.toByte(),
            'F'.code.toByte(),
            (totalLength and 0xff).toByte(),
            ((totalLength shr 8) and 0xff).toByte(),
            ((totalLength shr 16) and 0xff).toByte(),
            ((totalLength shr 24) and 0xff).toByte(),
            'W'.code.toByte(),
            'A'.code.toByte(),
            'V'.code.toByte(),
            'E'.code.toByte(),
            'f'.code.toByte(), // 'fmt ' chunk
            'm'.code.toByte(),
            't'.code.toByte(),
            ' '.code.toByte(),
            16, // 4 bytes: size of 'fmt ' chunk
            0,
            0,
            0,
            1, // format = 1
            0,
            channel,
            0,
            (sampleRate and 0xff).toByte(),
            ((sampleRate shr 8) and 0xff).toByte(),
            ((sampleRate shr 16) and 0xff).toByte(),
            ((sampleRate shr 24) and 0xff).toByte(),
            (byteRate and 0xff).toByte(),
            ((byteRate shr 8) and 0xff).toByte(),
            ((byteRate shr 16) and 0xff).toByte(),
            ((byteRate shr 24) and 0xff).toByte(),
            1, // block align
            0,
            bitRate.toByte(), // bits per sample
            0,
            'd'.code.toByte(),
            'a'.code.toByte(),
            't'.code.toByte(),
            'a'.code.toByte(),
            (audioSize and 0xff).toByte(),
            ((audioSize shr 8) and 0xff).toByte(),
            ((audioSize shr 16) and 0xff).toByte(),
            ((audioSize shr 24) and 0xff).toByte() //40-43 data size of rest
        )
        val wavHeader = header.toByteArray()
        println(wavHeader)
        return wavHeader
    }

}