package org.rhasspy.mobile.nativeutils

import android.content.ContentResolver
import android.content.res.Resources
import android.media.*
import android.net.Uri
import androidx.annotation.AnyRes
import co.touchlab.kermit.Logger
import dev.icerock.moko.resources.FileResource
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.settings.option.AudioOutputOption
import java.io.File
import java.nio.ByteBuffer


actual class AudioPlayer : Closeable {

    private val logger = Logger.withTag("AudioPlayer")

    private var _isPlayingState = MutableStateFlow(false)
    actual val isPlayingState: StateFlow<Boolean> get() = _isPlayingState

    private var audioTrack: AudioTrack? = null
    private var mediaPlayer: MediaPlayer? = null
    private var notification: Ringtone? = null
    private var volumeChange: Job? = null

    //on finished is stored to invoke it when stop is called
    private var onFinished: (() -> Unit)? = null

    /**
     * play byte list
     *
     * on Finished is called when playing has been finished
     * on Error is called when an playback error occurs
     */
    actual fun playData(
        data: List<Byte>,
        volume: Float,
        onFinished: (() -> Unit)?,
        onError: ((exception: Exception?) -> Unit)?
    ) {
        if (_isPlayingState.value) {
            logger.e { "AudioPlayer playData already playing data" }
            onError?.invoke(null)
            return
        }

        this.onFinished = onFinished
        _isPlayingState.value = true

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
                    .setChannelMask(if (channels.toInt() == 1) AudioFormat.CHANNEL_OUT_MONO else AudioFormat.CHANNEL_OUT_STEREO)
                    .build(),
                byteArray.size,
                AudioTrack.MODE_STATIC,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            ).apply {
                notificationMarkerPosition = audioDataSize / channels.toInt() //TODO returns error evaluate?

                setPlaybackPositionUpdateListener(object :
                    AudioTrack.OnPlaybackPositionUpdateListener {

                    override fun onMarkerReached(p0: AudioTrack?) {
                        logger.v { "finished playing audio stream" }
                        _isPlayingState.value = false
                        onFinished?.invoke()
                    }

                    override fun onPeriodicNotification(p0: AudioTrack?) {
                        println(p0?.playbackHeadPosition)
                        //TODO when playback position doesn't update it's finished
                    }
                })

                setVolume(volume)

                write(byteArray, 0, byteArray.size)
                play()
                flush()
            }


        } catch (e: Exception) {
            logger.e(e) { "Exception while playing audio data" }
            _isPlayingState.value = false
            onError?.invoke(e)
        }
    }

    /**
     * play file from resources
     *
     * volume is the playback volume, can be changed live
     * audio output option defines the channel (sound or notification)
     * on Finished is called when playing has been finished
     * on Error is called when an playback error occurs
     */
    actual fun playFileResource(
        fileResource: FileResource,
        volume: StateFlow<Float>,
        audioOutputOption: AudioOutputOption,
        onFinished: (() -> Unit)?,
        onError: ((exception: Exception?) -> Unit)?
    ) {
        logger.v { "playSoundFileResource" }

        when (audioOutputOption) {
            AudioOutputOption.Sound -> {
                playSound(getUriFromResource(fileResource.rawResId), volume, onFinished, onError)
            }
            AudioOutputOption.Notification -> {
                playNotification(getUriFromResource(fileResource.rawResId), volume, onFinished, onError)
            }
        }
    }

    /**
     * play file from storage
     *
     * volume is the playback volume, can be changed live
     * audio output option defines the channel (sound or notification)
     * on Finished is called when playing has been finished
     * on Error is called when an playback error occurs
     */
    actual fun playSoundFile(
        filename: String,
        volume: StateFlow<Float>,
        audioOutputOption: AudioOutputOption,
        onFinished: (() -> Unit)?,
        onError: ((exception: Exception?) -> Unit)?
    ) {
        logger.v { "playSoundFile $filename" }

        val soundFile = File(Application.nativeInstance.filesDir, "sounds/$filename")

        when (audioOutputOption) {
            AudioOutputOption.Sound -> {
                playSound(Uri.fromFile(soundFile), volume, onFinished, onError)
            }
            AudioOutputOption.Notification -> {
                playNotification(Uri.fromFile(soundFile), volume, onFinished, onError)
            }
        }
    }

    actual fun stop() {
        onFinished?.invoke()
        audioTrack?.stop()
        mediaPlayer?.stop()
        notification?.stop()
        audioTrack = null
        mediaPlayer = null
        notification = null
        _isPlayingState.value = false
    }

    override fun close() {
        stop()
    }

    private fun playSound(
        uri: Uri,
        volume: StateFlow<Float>,
        onFinished: (() -> Unit)?,
        onError: ((exception: Exception?) -> Unit)?
    ) {
        logger.v { "playSound" }

        if (_isPlayingState.value) {
            logger.e { "AudioPlayer playSound already playing data" }
            onError?.invoke(null)
            return
        }

        try {
            mediaPlayer = MediaPlayer.create(Application.nativeInstance, uri).apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )

                setVolume(volume.value, volume.value)
                //on completion listener is also called when error occurs
                setOnCompletionListener {
                    _isPlayingState.value = false
                    mediaPlayer = null
                    volumeChange?.cancel()
                    volumeChange = null
                    onFinished?.invoke()
                }

                _isPlayingState.value = true
                start()
            }

            volumeChange = CoroutineScope(Dispatchers.IO).launch {
                volume.collect {
                    mediaPlayer?.setVolume(volume.value, volume.value)
                }
            }
        } catch (e: Exception) {
            mediaPlayer = null
            volumeChange?.cancel()
            volumeChange = null
            _isPlayingState.value = false
            onError?.invoke(e)
        }
    }


    private fun playNotification(
        uri: Uri,
        volume: StateFlow<Float>,
        onFinished: (() -> Unit)?,
        onError: ((exception: Exception?) -> Unit)?
    ) {
        try {
            notification = RingtoneManager.getRingtone(Application.nativeInstance, uri).apply {
                play()
                CoroutineScope(Dispatchers.IO).launch {
                    while (isPlaying) {
                        _isPlayingState.value = true
                        awaitFrame()
                    }
                    _isPlayingState.value = false
                    notification = null
                    volumeChange?.cancel()
                    volumeChange = null
                    onFinished?.invoke()
                }
            }

            volumeChange = CoroutineScope(Dispatchers.IO).launch {
                volume.collect {
                    notification?.volume = volume.value
                }
            }
        } catch (e: Exception) {
            _isPlayingState.value = false
            notification = null
            volumeChange?.cancel()
            volumeChange = null
            onError?.invoke(e)
        }
    }


    @Throws(Resources.NotFoundException::class)
    fun getUriFromResource(@AnyRes resId: Int): Uri {
        val res: Resources = Application.nativeInstance.resources
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + res.getResourcePackageName(resId)
                    + '/' + res.getResourceTypeName(resId)
                    + '/' + res.getResourceEntryName(resId)
        )
    }
}