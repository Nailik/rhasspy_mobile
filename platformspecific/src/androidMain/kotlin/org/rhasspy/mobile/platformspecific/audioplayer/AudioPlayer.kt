package org.rhasspy.mobile.platformspecific.audioplayer

import co.touchlab.kermit.Logger
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.data.serviceoption.AudioOutputOption

actual class AudioPlayer : Closeable, KoinComponent {

    private val logger = Logger.withTag("AudioPlayer")

    private var _isPlayingState = MutableStateFlow(false)
    actual val isPlayingState: StateFlow<Boolean> get() = _isPlayingState

    private var internalAudioPlayer: InternalAudioPlayer? = null

    actual fun stop() {
        internalAudioPlayer?.stop()
        internalAudioPlayer = null
        _isPlayingState.value = false
    }

    override fun close() {
        stop()
    }

    actual fun playAudio(
        audioSource: AudioSource,
        volume: StateFlow<Float>,
        audioOutputOption: AudioOutputOption,
        onFinished: (exception: Exception?) -> Unit
    ) {
        logger.v { "playSound" }

        if (_isPlayingState.value || internalAudioPlayer?.isPlaying == true) {
            logger.e { "AudioPlayer playSound already playing data" }
            internalAudioPlayer?.stop()
        }

        _isPlayingState.value = true
        internalAudioPlayer = InternalAudioPlayer(
            audioSource = audioSource,
            volume = volume,
            audioOutputOption = audioOutputOption,
            onFinished = {
                internalAudioPlayer = null
                _isPlayingState.value = false
                onFinished.invoke(it)
            }
        )
    }

}