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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.serviceoption.AudioOutputOption
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import java.io.File

/**
 * handles any issues that can happen with MediaPlayer
 */
class InternalAudioPlayer(
    audioSource: AudioSource,
    private val volume: StateFlow<Float>,
    private val audioOutputOption: AudioOutputOption,
    private val onFinished: (exception: Exception?) -> Unit
) : KoinComponent {

    fun stop() {
        logger.v { "stop" }
        try {
            mediaPlayer.stop()
            mediaPlayer.release()
        } catch (exception: Exception) {
            logger.e { "stop exception" }
            onFinished(exception)
        }
    }

    private val logger = Logger.withTag("InternalAudioPlayer")
    val isPlaying: Boolean
        get() = mediaPlayer.isPlaying

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

    init {
        try {
            mediaPlayer.setVolume(volume.value, volume.value)
            mediaPlayer.setOnPreparedListener { onPrepared() }
            mediaPlayer.setOnCompletionListener { onMediaPlayerCompletion() }
            mediaPlayer.setOnErrorListener { _, _, _ -> onMediaPlayerError(); true }
            mediaPlayer.setDataSource(get<NativeApplication>(), uri)
            mediaPlayer.prepare()
        } catch (exception: Exception) {
            logger.e { "start exception" }
            onFinished(exception)
        }
    }

    private var volumeChange = CoroutineScope(Dispatchers.IO).launch(start = CoroutineStart.LAZY) {
        volume.collect {
            mediaPlayer.setVolume(volume.value, volume.value)
        }
    }

    private fun onPrepared() {
        logger.v { "onPrepared" }
        mediaPlayer.start()
        volumeChange.start()
    }

    private fun onMediaPlayerCompletion() {
        logger.v { "onMediaPlayerCompletion" }
        mediaPlayer.stop()
        mediaPlayer.release()
        onFinished(null)
    }

    private fun onMediaPlayerError() {
        logger.v { "onMediaPlayerError" }
        mediaPlayer.stop()
        mediaPlayer.release()
        onFinished(RuntimeException())
    }

    @Throws(Resources.NotFoundException::class)
    private fun getUriFromResource(@AnyRes resId: Int): Uri {
        val res: Resources = get<NativeApplication>().resources
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + res.getResourcePackageName(resId)
                    + '/' + res.getResourceTypeName(resId)
                    + '/' + res.getResourceEntryName(resId)
        )
    }

    private fun getUriFromData(data: ByteArray): Uri {
        val soundFile = File(get<NativeApplication>().cacheDir, "/playData.wav")
        if (!soundFile.exists()) {
            soundFile.createNewFile()
        }
        soundFile.writeBytes(data)
        return Uri.fromFile(soundFile)
    }

}