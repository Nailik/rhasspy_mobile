package org.rhasspy.mobile.services.native

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import co.touchlab.kermit.Logger
import org.rhasspy.mobile.services.dialogue.ServiceInterface
import org.rhasspy.mobile.settings.AppSettings
import java.nio.ByteBuffer


actual object AudioPlayer {
    private val logger = Logger.withTag(this::class.simpleName!!)
    private var isEnabled = true

    actual fun playData(data: List<Byte>) {
        if (!isEnabled) {
            logger.v { "AudioPlayer NOT enabled" }
        }

        try {
            logger.v { "start audio stream" }

            //https://stackoverflow.com/questions/13039846/what-do-the-bytes-in-a-wav-file-represent
            val byteArray = data.toByteArray()
            //copyOfRange end is exclusive

            //21-22 Audio format code, a 2 byte (16 bit) integer (short in kotlin). 1 = PCM (pulse code modulation).
            val formatCode = ByteBuffer.wrap(byteArray.copyOfRange(20, 22).reversedArray()).short
            //23-24 Number of channels as a 2 byte (16 bit) integer (short in kotlin). 1 = mono, 2 = stereo, etc.
            val channels = ByteBuffer.wrap(byteArray.copyOfRange(22, 24).reversedArray()).short
            //Sample rate as a 4 byte (32 bit) integer.
            val sampleRate = ByteBuffer.wrap(byteArray.copyOfRange(24, 28).reversedArray()).int
            //41-44 The number of bytes of the data section below this
            val audioDataSize = ByteBuffer.wrap(byteArray.copyOfRange(40, 44).reversedArray()).int / 2 //(pcm)

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
                AudioTrack.MODE_STATIC,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            )

            audioTrack.notificationMarkerPosition = audioDataSize
            audioTrack.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {

                override fun onMarkerReached(p0: AudioTrack?) {
                    ServiceInterface.playFinished()
                }

                override fun onPeriodicNotification(p0: AudioTrack?) {}
            })

            audioTrack.setVolume(AppSettings.volume.data)

            audioTrack.write(byteArray, 40, audioDataSize)
            audioTrack.play()
            audioTrack.flush()

        } catch (e: Exception) {
            logger.e(e) { "Exception while playing audio data" }
        }
    }

}