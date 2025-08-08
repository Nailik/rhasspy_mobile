package org.rhasspy.mobile.platformspecific.audioplayer

import android.content.ContentResolver
import android.content.res.Resources
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.annotation.AnyRes
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import java.io.File

/**
 * handles any issues that can happen with MediaPlayer
 */
class InternalAudioPlayer(
    audioSource: AudioSource,
    private val volume: StateFlow<Float>,
    private val audioOutputOption: AudioOutputOption,
    private val onFinished: () -> Unit
) : KoinComponent {

    private val nativeApplication = get<NativeApplication>()

    private var finishCalled = false
    private var coroutineScope = CoroutineScope(Dispatchers.IO)
    private var timeoutJob: Job? = null

    fun stop() {
        timeoutJob?.cancel()
        timeoutJob = null
        logger.v { "stop" }
        try {
            volumeChange.cancel()
            mediaPlayer.stop()
            mediaPlayer.release()
        } catch (exception: Exception) {
            logger.e(exception) { "stop exception" }
            callOnFinish()
        }
    }

    private val logger = Logger.withTag("InternalAudioPlayer")
    val isPlaying: Boolean
        get() = mediaPlayer.isPlaying

    @Suppress("DEPRECATION")
    private val uri = when (audioSource) {
        is AudioSource.Data -> getUriFromData(audioSource.data)
        is AudioSource.File -> Uri.fromFile(audioSource.path.toFile())
        is AudioSource.Resource -> getUriFromResource(audioSource.fileResource.rawResId)
    }

    private val mediaPlayer = MediaPlayer().apply {
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

    private var volumeChange = CoroutineScope(Dispatchers.IO).launch(start = CoroutineStart.LAZY) {
        volume.collect {
            try {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.setVolume(volume.value, volume.value)
                }
            } catch (exception: Exception) {
                //eventually called in false state (not yet playing, already stopped)
                logger.a(exception) { "crash on volume change" }
            }
        }
    }

    init {
        try {
            mediaPlayer.setVolume(volume.value, volume.value)
            mediaPlayer.setOnCompletionListener { onMediaPlayerCompletion() }
            mediaPlayer.setOnErrorListener { _, _, _ -> onMediaPlayerError(); false }
            mediaPlayer.setDataSource(nativeApplication, uri)
            mediaPlayer.prepare()
            volumeChange.start()
            val duration = mediaPlayer.duration
            timeoutJob = coroutineScope.launch {
                try {
                    delay(duration.toLong() + 100)
                    onMediaPlayerCompletion()
                } catch (_: Exception) {
                    callOnFinish()
                }
            }
            mediaPlayer.start()
        } catch (exception: Exception) {
            logger.e(exception) { "start exception" }
            callOnFinish()
        }
    }

    private fun onMediaPlayerCompletion() {
        logger.v { "onMediaPlayerCompletion" }
        if (timeoutJob?.isActive == true) {
            timeoutJob?.cancel()
        }
        if (volumeChange.isActive) {
            volumeChange.cancel()
        }
        timeoutJob = null
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.release()
        } catch (_: Exception) {
        }
        callOnFinish()
    }

    private fun onMediaPlayerError() {
        logger.v { "onMediaPlayerError" }
        if (timeoutJob?.isActive == true) {
            timeoutJob?.cancel()
        }
        if (volumeChange.isActive) {
            volumeChange.cancel()
        }
        timeoutJob = null
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.release()
        } catch (_: Exception) {
        }
        callOnFinish()
    }

    @Throws(Resources.NotFoundException::class)
    private fun getUriFromResource(@AnyRes resId: Int): Uri {
        val res: Resources = nativeApplication.resources
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + res.getResourcePackageName(resId)
                    + '/' + res.getResourceTypeName(resId)
                    + '/' + res.getResourceEntryName(resId)
        )
    }

    private fun getUriFromData(data: ByteArray): Uri {
        val soundFile = File(nativeApplication.cacheDir, "/playData.wav")
        if (!soundFile.exists()) {
            soundFile.createNewFile()
        }
        soundFile.writeBytes(data)
        return Uri.fromFile(soundFile)
    }

    private fun callOnFinish() {
        if (!finishCalled) {
            finishCalled = true
            onFinished()
        }
    }

}