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
import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.settings.option.AudioOutputOption
import java.io.File
import java.io.FileOutputStream

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
        data: FileStream,
        volume: Float,
        audioOutputOption: AudioOutputOption,
        onFinished: (() -> Unit)?,
        onError: ((exception: Exception?) -> Unit)?
    ) {
        val soundFile = File(Application.nativeInstance.cacheDir, "/playData.wav")

        val fos = FileOutputStream(soundFile)
        data.copyTo(fos)
        fos.close()

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
        playAudio(getUriFromResource(fileResource.rawResId), volume,audioOutputOption, onFinished, onError)
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
        val soundFile = File(Application.nativeInstance.filesDir, filename)
        playAudio(Uri.fromFile(soundFile), volume, audioOutputOption, onFinished, onError)
    }

    actual fun stop() {
        audioTrack?.stop()
        mediaPlayer?.stop()
        notification?.stop()
        audioTrack = null
        mediaPlayer = null
        notification = null
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
                setDataSource(Application.nativeInstance, uri)

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
        val res: Resources = Application.nativeInstance.resources
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + res.getResourcePackageName(resId)
                    + '/' + res.getResourceTypeName(resId)
                    + '/' + res.getResourceEntryName(resId)
        )
    }
}