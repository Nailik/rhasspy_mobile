package org.rhasspy.mobile.nativeutils

import android.content.ContentResolver
import android.content.res.Resources
import android.media.*
import android.net.Uri
import android.os.Build
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
        data: List<Byte>,
        volume: Float,
        audioOutputOption: AudioOutputOption,
        onFinished: (() -> Unit)?,
        onError: ((exception: Exception?) -> Unit)?
    ) {
        val soundFile = File(Application.nativeInstance.cacheDir, "/playData.wav")
        //soundFile.deleteOnExit()
        val fos = FileOutputStream(soundFile)
        fos.write(data.toByteArray())
        fos.close()

        when (audioOutputOption) {
            AudioOutputOption.Sound -> {
                playSound(Uri.fromFile(soundFile), MutableStateFlow(volume), onFinished, onError)
            }

            AudioOutputOption.Notification -> {
                playNotification(
                    Uri.fromFile(soundFile),
                    MutableStateFlow(volume),
                    onFinished,
                    onError
                )
            }
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
                playNotification(
                    getUriFromResource(fileResource.rawResId),
                    volume,
                    onFinished,
                    onError
                )
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

        val soundFile = File(Application.nativeInstance.filesDir, filename)

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
        try {
            //notification may throw an internal error
            audioTrack?.stop()
            mediaPlayer?.stop()
            notification?.stop()
        } catch (_: Exception) {
        }
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
            mediaPlayer = MediaPlayer().apply {
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
                    try {
                        while (isPlaying) {
                            _isPlayingState.value = true
                            awaitFrame()
                        }
                    } catch (e: IllegalStateException) {
                        //can happen when isPlaying is request because ringtone doesn't check if media player is initialized and not released yet
                        _isPlayingState.value = false
                    }
                    _isPlayingState.value = false
                    notification = null
                    volumeChange?.cancel()
                    volumeChange = null
                    onFinished?.invoke()
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                //notification set volume was added in api 28
                volumeChange = CoroutineScope(Dispatchers.IO).launch {
                    volume.collect {
                        notification?.volume = volume.value
                    }
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