package org.rhasspy.mobile.platformspecific.media

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SPEECH
import android.media.AudioAttributes.USAGE_VOICE_COMMUNICATION
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.Build
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual object MediaUtils: KoinComponent {

    private val request = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        AudioFocusRequest
            .Builder(AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
            .setAudioAttributes(
                AudioAttributes
                    .Builder()
                    .setContentType(CONTENT_TYPE_SPEECH)
                    .setUsage(USAGE_VOICE_COMMUNICATION)
                    .build()
            )
            .build()
    } else null

    actual fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (get<NativeApplication>().getSystemService(Context.AUDIO_SERVICE) as AudioManager)
                .requestAudioFocus(request!!)
        } else {
            val listener = OnAudioFocusChangeListener { }
            @Suppress("DEPRECATION")
            (get<NativeApplication>().getSystemService(Context.AUDIO_SERVICE) as AudioManager)
                .requestAudioFocus(listener, AudioManager.STREAM_MUSIC, AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)

        }
    }

    actual fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (get<NativeApplication>().getSystemService(Context.AUDIO_SERVICE) as AudioManager)
                .abandonAudioFocusRequest(AudioFocusRequest.Builder(AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE).build())
        } else {
            @Suppress("DEPRECATION")
            (get<NativeApplication>().getSystemService(Context.AUDIO_SERVICE) as AudioManager)
                .abandonAudioFocus { }

        }
    }

}