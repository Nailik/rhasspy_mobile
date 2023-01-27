package org.rhasspy.mobile.logic.nativeutils

import android.content.ContentResolver
import android.content.res.Resources
import android.media.*
import android.net.Uri
import androidx.annotation.AnyRes
import co.touchlab.kermit.Logger
import dev.icerock.moko.resources.FileResource
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.logic.settings.option.AudioOutputOption
import java.io.File

actual class AudioPlayer : Closeable, KoinComponent {

    private val logger = Logger.withTag("AudioPlayer")

    private var _isPlayingState = MutableStateFlow(false)
    actual val isPlayingState: StateFlow<Boolean> get() = _isPlayingState

    private var audioTrack: AudioTrack? = null
    private var mediaPlayer: MediaPlayer? = null
    private var volumeChange: Job? = null

    //on finished is stored to invoke it when stop is called
    private var onFinished: (() -> Unit)? = null

    private val context = get<NativeApplication>()

    /**
     * play byte list
     *
     * on Finished is called when playing has been finished
     * on Error is called when an playback error occurs
     */
    actual fun playData(
        data: ByteArray,
        volume: Float,
        audioOutputOption: AudioOutputOption,
        onFinished: (() -> Unit)?,
        onError: ((exception: Exception?) -> Unit)?
    ) {
        val soundFile = File(context.cacheDir, "/playData.wav")
        if(!soundFile.exists()) {
            soundFile.createNewFile()
        }
        soundFile.writeBytes(data)
        playAudio(Uri.fromFile(soundFile), MutableStateFlow(volume), audioOutputOption, onFinished, onError)
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
        playAudio(getUriFromResource(fileResource.rawResId), volume, audioOutputOption, onFinished, onError)
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
        val soundFile = File(context.filesDir, filename)
        playAudio(Uri.fromFile(soundFile), volume, audioOutputOption, onFinished, onError)
    }

    actual fun stop() {
        audioTrack?.stop()
        mediaPlayer?.stop()
        audioTrack = null
        mediaPlayer = null
        _isPlayingState.value = false
        onFinished?.invoke()
        onFinished = null
    }

    override fun close() {
        stop()
    }

    private fun playAudio(
        uri: Uri,
        volume: StateFlow<Float>,
        audioOutputOption: AudioOutputOption,
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
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder().apply {
                        when (audioOutputOption) {
                            AudioOutputOption.Sound -> {
                                this.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                this.setUsage(AudioAttributes.USAGE_MEDIA)
                            }

                            AudioOutputOption.Notification -> {
                                this.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                this.setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                            }
                        }
                    }.build()
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
                setOnPreparedListener {
                    if (!it.isPlaying) {
                        volumeChange = CoroutineScope(Dispatchers.IO).launch {
                            volume.collect {
                                mediaPlayer?.setVolume(volume.value, volume.value)
                            }
                        }

                        start()
                    }
                }

                _isPlayingState.value = true
                setDataSource(get<NativeApplication>(), uri)

                prepareAsync()
            }
        } catch (e: Exception) {
            mediaPlayer = null
            volumeChange?.cancel()
            volumeChange = null
            _isPlayingState.value = false
            onError?.invoke(e)
        }
    }

    @Throws(Resources.NotFoundException::class)
    fun getUriFromResource(@AnyRes resId: Int): Uri {
        val res: Resources = context.resources
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + res.getResourcePackageName(resId)
                    + '/' + res.getResourceTypeName(resId)
                    + '/' + res.getResourceEntryName(resId)
        )
    }
}