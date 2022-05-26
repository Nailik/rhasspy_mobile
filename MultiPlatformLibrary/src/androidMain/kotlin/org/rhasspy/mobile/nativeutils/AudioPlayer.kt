package org.rhasspy.mobile.nativeutils

import android.media.*
import androidx.core.net.toUri
import co.touchlab.kermit.Logger
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.subject.publish.PublishSubject
import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.settings.AppSettings
import java.io.File
import java.nio.ByteBuffer

actual object AudioPlayer {
    private val logger = Logger.withTag("AudioPlayer")
    private var isEnabled = true

    private val isPlayingStateSubject = PublishSubject<Boolean>()
    private var isPlaying: Boolean = false
        set(value) {
            field = value
            isPlayingStateSubject.onNext(value)
        }
    actual val isPlayingState = isPlayingStateSubject.observeOn(ioScheduler)

    init {
        isPlayingStateSubject.onNext(false)
    }

    actual fun playData(data: List<Byte>) {
        if (!isEnabled) {
            logger.v { "AudioPlayer NOT enabled" }
            return
        }

        if (isPlaying) {
            logger.e { "AudioPlayer playData already playing data" }
            return
        }

        isPlaying = true

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
                    logger.v { "finished playing audio stream" }
                    isPlaying = false
                }

                override fun onPeriodicNotification(p0: AudioTrack?) {}
            })

            audioTrack.setVolume(AppSettings.volume.data)

            audioTrack.write(byteArray, 40, audioDataSize)
            audioTrack.play()
            audioTrack.flush()

        } catch (e: Exception) {
            logger.e(e) { "Exception while playing audio data" }
            isPlaying = false
        }
    }


    private fun playSound(mediaPlayer: MediaPlayer) {
        logger.v { "playSound" }

        if (isPlaying) {
            logger.e { "AudioPlayer playSound already playing data" }
            return
        }

        isPlaying = true

        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )

        mediaPlayer.setVolume(AppSettings.soundVolume.data, AppSettings.soundVolume.data)
        //on completion listener is also called when error occurs
        mediaPlayer.setOnCompletionListener {
            isPlaying = false
        }
        mediaPlayer.start()
    }

    /**
     * play audio resource
     */
    actual fun playSoundFileResource(fileResource: FileResource) {
        logger.v { "playSoundFileResource" }

        playSound(
            MediaPlayer.create(
                Application.Instance,
                fileResource.rawResId
            )
        )
    }

    /**
     * play some sound file
     */
    actual fun playSoundFile(filename: String) {
        logger.v { "playSoundFile $filename" }

        val soundFile = File(Application.Instance.filesDir, "sounds/$filename")

        playSound(
            MediaPlayer.create(
                Application.Instance,
                soundFile.toUri()
            )
        )
    }

}