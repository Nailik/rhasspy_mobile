package org.rhasspy.mobile.nativeutils

import android.media.*
import android.net.Uri
import co.touchlab.kermit.Logger
import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.observer.MutableObservable
import org.rhasspy.mobile.settings.AppSettings
import java.io.File
import java.nio.ByteBuffer

actual object AudioPlayer {
    private val logger = Logger.withTag("AudioPlayer")
    private var isEnabled = true

    private var isPlaying = MutableObservable(false)
    actual val isPlayingState = isPlaying.readOnly()
    private var audioTrack: AudioTrack? = null
    private var onFinished: (() -> Unit)? = null

    actual fun playData(data: List<Byte>, onFinished: () -> Unit) {
        if (!isEnabled) {
            logger.v { "AudioPlayer NOT enabled" }
            return
        }

        if (isPlaying.value) {
            logger.e { "AudioPlayer playData already playing data" }
            return
        }

        this.onFinished = onFinished
        isPlaying.value = true

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

            audioTrack = AudioTrack(
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
            ).apply {
                notificationMarkerPosition = audioDataSize
                setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {

                    override fun onMarkerReached(p0: AudioTrack?) {
                        logger.v { "finished playing audio stream" }
                        isPlaying.value = false
                        onFinished()
                    }

                    override fun onPeriodicNotification(p0: AudioTrack?) {}
                })

                setVolume(AppSettings.volume.value)

                write(byteArray, 0, byteArray.size)
                play()
                flush()
            }


        } catch (e: Exception) {
            logger.e(e) { "Exception while playing audio data" }
            isPlaying.value = false
            onFinished()
        }
    }

    actual fun stopPlayingData() {
        if (onFinished != null && audioTrack != null && isPlaying.value) {
            isPlaying.value = false
            onFinished?.invoke()
            audioTrack?.stop()
        }
    }

    private fun playSound(mediaPlayer: MediaPlayer) {
        logger.v { "playSound" }

        if (isPlaying.value) {
            logger.e { "AudioPlayer playSound already playing data" }
            return
        }

        isPlaying.value = true

        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )

        mediaPlayer.setVolume(AppSettings.soundVolume.value, AppSettings.soundVolume.value)
        //on completion listener is also called when error occurs
        mediaPlayer.setOnCompletionListener {
            isPlaying.value = false
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
                Uri.fromFile(soundFile)
            )
        )
    }

}