package org.rhasspy.mobile.audio

import android.media.AudioAttributes
import android.media.MediaPlayer
import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.services.Application

actual object Audio {

    actual fun play(fileResource: FileResource) {

        val mediaPlayer = MediaPlayer.create(
            Application.Instance,
            fileResource.rawResId
        )

        mediaPlayer.setAudioAttributes(
            AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        mediaPlayer.start()
    }

}