package org.rhasspy.mobile.platformspecific.audioplayer

import android.content.ContentResolver
import android.content.res.Resources
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.annotation.AnyRes
import co.touchlab.kermit.Logger
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.serviceoption.AudioOutputOption
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import java.io.File

actual class AudioPlayer : Closeable, KoinComponent {

    private val logger = Logger.withTag("AudioPlayer")

    private var _isPlayingState = MutableStateFlow(false)
    actual val isPlayingState: StateFlow<Boolean> get() = _isPlayingState

    private var mediaPlayer: MediaPlayer? = null
    private var volumeChange: Job? = null

    //on finished is stored to invoke it when stop is called
    private var onFinished: (() -> Unit)? = null

    private val context = get<NativeApplication>()

    actual fun stop() {
        try {
            mediaPlayer?.stop()
        } catch (_: Exception) {
        }
        mediaPlayer = null
        _isPlayingState.value = false
        onFinished?.invoke()
        onFinished = null
    }

    override fun close() {
        stop()
    }

    actual fun playAudio(
        audioSource: AudioSource,
        volume: StateFlow<Float>,
        audioOutputOption: AudioOutputOption,
        onFinished: (() -> Unit)?,
        onError: ((exception: Exception?) -> Unit)?
    ) {
        logger.v { "playSound" }

        if (_isPlayingState.value) {
            logger.e { "AudioPlayer playSound already playing data" }
            stop()
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
            }

            mediaPlayer?.setVolume(volume.value, volume.value)
            //on completion listener is also called when error occurs
            mediaPlayer?.setOnCompletionListener {
                logger.v { "finished" }
                _isPlayingState.value = false
                mediaPlayer = null
                volumeChange?.cancel()
                volumeChange = null
                onFinished?.invoke()
            }
            mediaPlayer?.setOnPreparedListener {
                try {
                    if (it.isPlaying) {
                        logger.e { "AudioPlayer it.isPlaying true" }
                        it.stop()
                    }
                    volumeChange = CoroutineScope(Dispatchers.IO).launch {
                        volume.collect {
                            mediaPlayer?.setVolume(volume.value, volume.value)
                        }
                    }

                    mediaPlayer?.start()
                } catch (exception: Exception) {
                    logger.e(exception) { "AudioPlayer thrown exception setOnPreparedListener" }
                    mediaPlayer = null
                    volumeChange?.cancel()
                    volumeChange = null
                    _isPlayingState.value = false
                    onError?.invoke(exception)
                }
            }

            _isPlayingState.value = true

            val uri = when (audioSource) {
                is AudioSource.Data -> {
                    val soundFile = File(context.cacheDir, "/playData.wav")
                    if (!soundFile.exists()) {
                        soundFile.createNewFile()
                    }
                    soundFile.writeBytes(audioSource.data)
                    Uri.fromFile(soundFile)
                }

                is AudioSource.File -> {
                    val soundFile = audioSource.path.toFile()
                    Uri.fromFile(soundFile)
                }

                is AudioSource.Resource -> getUriFromResource(audioSource.fileResource.rawResId)
            }
            mediaPlayer?.setDataSource(get<NativeApplication>(), uri)

            //don't use prepare async because it may fail and doesn't throw an error, blocking the whole app
            mediaPlayer?.prepare()
        } catch (exception: Exception) {
            logger.e(exception) { "AudioPlayer thrown exception" }
            mediaPlayer = null
            volumeChange?.cancel()
            volumeChange = null
            _isPlayingState.value = false
            onError?.invoke(exception)
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