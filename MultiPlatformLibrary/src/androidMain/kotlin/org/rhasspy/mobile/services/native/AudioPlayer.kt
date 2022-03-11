package org.rhasspy.mobile.services.native

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import java.nio.ByteBuffer


actual object AudioPlayer {

    actual fun startStream(byteArray: ByteArray): AudioStreamInterface {

        //https://stackoverflow.com/questions/13039846/what-do-the-bytes-in-a-wav-file-represent

        //copyOfRange end is exclusive

        //21-22 Audio format code, a 2 byte (16 bit) integer (short in kotlin). 1 = PCM (pulse code modulation).
        val formatCode = ByteBuffer.wrap(byteArray.copyOfRange(20, 22).reversedArray()).short
        //23-24 Number of channels as a 2 byte (16 bit) integer (short in kotlin). 1 = mono, 2 = stereo, etc.
        val channels = ByteBuffer.wrap(byteArray.copyOfRange(22, 24).reversedArray()).short
        //Sample rate as a 4 byte (32 bit) integer.
        val sampleRate = ByteBuffer.wrap(byteArray.copyOfRange(24, 28).reversedArray()).int

        val audioTrack = AudioTrack(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build(),
            AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(formatCode.toInt())
                .setChannelMask(if (channels.toInt() == 1) AudioFormat.CHANNEL_OUT_MONO else AudioFormat.CHANNEL_OUT_STEREO).build(),
            byteArray.size,
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )

        audioTrack.play()
        audioTrack.write(byteArray, 0, byteArray.size)

        return object : AudioStreamInterface {

            override fun enqueue(byteArray: ByteArray) {
                audioTrack.write(byteArray, 0, byteArray.size)
            }

        }
    }

}