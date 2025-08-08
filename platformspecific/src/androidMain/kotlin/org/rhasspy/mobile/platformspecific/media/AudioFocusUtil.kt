package org.rhasspy.mobile.platformspecific.media

import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SPEECH
import android.media.AudioAttributes.USAGE_VOICE_COMMUNICATION
import android.media.AudioFocusRequest.Builder
import android.media.AudioManager
import android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE
import android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
import android.media.AudioManager.STREAM_MUSIC
import android.os.Build
import androidx.core.content.getSystemService
import co.touchlab.kermit.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption.Disabled
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption.Duck
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption.PauseAndResume
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason
import org.rhasspy.mobile.platformspecific.application.NativeApplication


actual object AudioFocusUtil : KoinComponent {

    private val logger = Logger.withTag("AudioFocusUtil")
    private val nativeApplication by inject<NativeApplication>()

    private fun requestTypeInt(audioFocusOption: AudioFocusOption): Int {
        return when (audioFocusOption) {
            Disabled -> -1
            PauseAndResume -> AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE
            Duck -> AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
        }
    }

    actual fun request(reason: AudioFocusRequestReason, audioFocusOption: AudioFocusOption) {
        nativeApplication.getSystemService<AudioManager>()?.also { audioManager ->
            val requestType = requestTypeInt(audioFocusOption)

            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    val request = Builder(requestType)
                        .setAudioAttributes(
                            AudioAttributes
                                .Builder()
                                .setContentType(CONTENT_TYPE_SPEECH)
                                .setUsage(USAGE_VOICE_COMMUNICATION)
                                .build()
                        )
                        .build()

                    audioManager.requestAudioFocus(request)
                }

                else -> {
                    @Suppress("DEPRECATION")
                    audioManager.requestAudioFocus({ /* unused */ }, STREAM_MUSIC, requestType)
                }
            }
        } ?: {
            logger.e { "request audioManager is null" }
        }

    }

    actual fun abandon(reason: AudioFocusRequestReason, audioFocusOption: AudioFocusOption) {

        nativeApplication.getSystemService<AudioManager>()?.also { audioManager ->
            val requestType = requestTypeInt(audioFocusOption)

            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                    audioManager.abandonAudioFocusRequest(Builder(requestType).build())

                else -> @Suppress("DEPRECATION")
                audioManager.abandonAudioFocus {  /* unused */ }
            }
        } ?: {
            logger.e { "request audioManager is null" }
        }

    }

}