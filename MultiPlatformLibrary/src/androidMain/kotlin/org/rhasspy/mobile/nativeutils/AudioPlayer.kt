package org.rhasspy.mobile.nativeutils

import android.content.ContentResolver
import android.content.res.Resources
import android.media.*
import android.net.Uri
import androidx.annotation.AnyRes
import co.touchlab.kermit.Logger
import dev.icerock.moko.resources.FileResource
import kotlinx.coroutines.*
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.data.AudioOutputOptions
import org.rhasspy.mobile.settings.AppSettings
import java.io.File
import java.nio.ByteBuffer


actual class AudioPlayer {


    private val logger = Logger.withTag("AudioPlayer")
    private var isEnabled = true

    private var _isPlayingState = MutableStateFlow(false)
    actual val isPlayingState: StateFlow<Boolean> get() = _isPlayingState
    private var audioTrack: AudioTrack? = null
    private var onFinished: (suspend () -> Unit)? = null
    private var mediaPlayer: MediaPlayer? = null
    private var notification: Ringtone? = null
    private var volumeChange: Job? = null

    actual suspend fun playData(data: List<Byte>, onFinished: suspend () -> Unit) {
        if (!isEnabled) {
            logger.v { "AudioPlayer NOT enabled" }
            return
        }

        if (_isPlayingState.value) {
            logger.e { "AudioPlayer playData already playing data" }
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
            val audioDataSize =
                ByteBuffer.wrap(byteArray.copyOfRange(40, 44).reversedArray()).int / 2 //(pcm)

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
                notificationMarkerPosition = audioDataSize

                setPlaybackPositionUpdateListener(object :
                    AudioTrack.OnPlaybackPositionUpdateListener {

                    override fun onMarkerReached(p0: AudioTrack?) {
                        logger.v { "finished playing audio stream" }
                        _isPlayingState.value = false
                        CoroutineScope(Dispatchers.Default).launch {
                            onFinished()
                        }
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
            _isPlayingState.value = false
            onFinished()
        }
    }

    actual fun stopPlayingData() {
        CoroutineScope(Dispatchers.Default).launch {
            onFinished?.invoke()
        }
        audioTrack?.stop()
        mediaPlayer?.stop()
        notification?.stop()
        audioTrack = null
        mediaPlayer = null
        notification = null
        _isPlayingState.value = false
    }

    private fun playSound(uri: Uri, volume: StateFlow<Float>) {
        logger.v { "playSound" }

        if (_isPlayingState.value) {
            logger.e { "AudioPlayer playSound already playing data" }
            return
        }

        _isPlayingState.value = true

        mediaPlayer = MediaPlayer.create(Application.Instance, uri).apply {
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
            }
            start()
        }

        volumeChange = CoroutineScope(Dispatchers.IO).launch {
            volume.collect {
                mediaPlayer?.setVolume(volume.value, volume.value)
            }
        }
    }


    private fun playNotification(uri: Uri, volume: StateFlow<Float>) {
        notification = RingtoneManager.getRingtone(Application.Instance, uri).apply {
            play()
            CoroutineScope(Dispatchers.IO).launch {
                while (isPlaying) {
                    _isPlayingState.value = true
                    awaitFrame()
                }
                _isPlayingState.value = false
            }
        }

        volumeChange = CoroutineScope(Dispatchers.IO).launch {
            volume.collect {
                notification?.volume = volume.value
            }
        }
    }

    /**
     * play audio resource
     */
    actual fun playSoundFileResource(
        fileResource: FileResource,
        volume: StateFlow<Float>,
        audioOutputOptions: AudioOutputOptions
    ) {
        logger.v { "playSoundFileResource" }

        when (audioOutputOptions) {
            AudioOutputOptions.Sound -> {
                playSound(getUriFromResource(fileResource.rawResId), volume)
            }
            AudioOutputOptions.Notification -> {
                playNotification(getUriFromResource(fileResource.rawResId), volume)
            }
        }
    }

    @Throws(Resources.NotFoundException::class)
    fun getUriFromResource(@AnyRes resId: Int): Uri {
        val res: Resources = Application.Instance.resources
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + res.getResourcePackageName(resId)
                    + '/' + res.getResourceTypeName(resId)
                    + '/' + res.getResourceEntryName(resId)
        )
    }

    /**
     * play some sound file
     */
    actual fun playSoundFile(
        subfolder: String,
        filename: String,
        volume: StateFlow<Float>,
        audioOutputOptions: AudioOutputOptions
    ) {
        logger.v { "playSoundFile $subfolder/$filename" }

        val soundFile = File(Application.Instance.filesDir, "sounds/$subfolder/$filename")

        when (audioOutputOptions) {
            AudioOutputOptions.Sound -> {
                playSound(Uri.fromFile(soundFile), volume)
            }
            AudioOutputOptions.Notification -> {
                playNotification(Uri.fromFile(soundFile), volume)
            }
        }
    }

}