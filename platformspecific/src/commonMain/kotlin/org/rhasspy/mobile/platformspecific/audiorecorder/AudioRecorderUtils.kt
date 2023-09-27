package org.rhasspy.mobile.platformspecific.audiorecorder

object AudioRecorderUtils {

    /**
     * use the settings of the audio recorder
     * (samplingRate, channels, bitrate) and the audioSize
     * to create wav header and add it in front of the given data
     */
    fun ByteArray.appendWavHeader(
        sampleRate: Int,
        bitRate: Int,
        channel: Int,
    ): ByteArray {
        return getWavHeader(
            sampleRate = sampleRate,
            bitRate = bitRate,
            channel = channel,
            audioSize = this.size.toLong()
        ) + this
    }


    fun ByteArray.getWavHeaderSampleRate()  : Int{
        return byteArrayToIntLittleEndian(copyOfRange(24, 28))
    }

    fun ByteArray.getWavHeaderBitRate() : Int{
        return byteArrayToIntLittleEndian(copyOfRange(34, 36))
    }

    fun ByteArray.getWavHeaderChannel(): Int {
        return this[22].toInt()
    }

    private fun byteArrayToIntLittleEndian(byteArray: ByteArray): Int {
        var result = 0
        for (i in byteArray.indices) {
            result = result or (byteArray[i].toInt() and 0xFF shl (i * 8))
        }
        return result
    }

    fun getWavHeader(
        sampleRate: Int,
        bitRate: Int,
        channel: Int,
        audioSize: Long
    ): ByteArray {
        //info https://docs.fileformat.com/audio/wav/
        val byteRate = (sampleRate * channel * bitRate) / 8
        val bitPerSampleChannels = (bitRate * channel) / 8

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
            16, // 4 bytes: size of 'fmt ' chunk,
            0,
            0,
            0,
            1, // Type of format (1 is PCM) - 2 byte integer
            0,
            channel.toByte(),
            0,
            (sampleRate and 0xff).toByte(),
            ((sampleRate shr 8) and 0xff).toByte(),
            ((sampleRate shr 16) and 0xff).toByte(),
            ((sampleRate shr 24) and 0xff).toByte(),
            (byteRate and 0xff).toByte(),
            ((byteRate shr 8) and 0xff).toByte(),
            ((byteRate shr 16) and 0xff).toByte(),
            ((byteRate shr 24) and 0xff).toByte(),
            (bitPerSampleChannels and 0xff).toByte(),
            ((bitPerSampleChannels shr 8) and 0xff).toByte(),
            (bitRate and 0xff).toByte(),
            ((bitRate shr 8) and 0xff).toByte(),
            'd'.code.toByte(),
            'a'.code.toByte(),
            't'.code.toByte(),
            'a'.code.toByte(),
            (audioSize and 0xff).toByte(),
            ((audioSize shr 8) and 0xff).toByte(),
            ((audioSize shr 16) and 0xff).toByte(),
            ((audioSize shr 24) and 0xff).toByte() //40-43 data size of rest
        )
        return header.toByteArray()
    }

}