package org.rhasspy.mobile.platformspecific.media

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SPEECH
import android.media.AudioAttributes.USAGE_VOICE_COMMUNICATION
import android.media.AudioFocusRequest.Builder
import android.media.AudioManager
import android.media.AudioManager.*
import android.os.Build
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.*
import org.rhasspy.mobile.data.audiofocus.AudioFocusType
import org.rhasspy.mobile.data.audiofocus.AudioFocusType.*
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual object AudioFocusUtil: KoinComponent {

    private val nativeApplication by inject<NativeApplication>()

    private fun requestTypeInt(audioFocusType: AudioFocusType): Int {
        return when (audioFocusType) {
            Disabled -> -1
            PauseAndResume -> AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE
            Duck -> AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
        }
    }

    actual fun request(reason: AudioFocusRequestReason, audioFocusType: AudioFocusType) {
        val audioManager = nativeApplication.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val requestType = requestTypeInt(audioFocusType)

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

    }

    actual fun abandon(reason: AudioFocusRequestReason, audioFocusType: AudioFocusType) {

        val audioManager = nativeApplication.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val requestType = requestTypeInt(audioFocusType)

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                audioManager.abandonAudioFocusRequest(Builder(requestType).build())

            else -> @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus {  /* unused */ }
        }

    }

}