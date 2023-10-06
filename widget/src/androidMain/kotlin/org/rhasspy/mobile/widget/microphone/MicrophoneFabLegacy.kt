package org.rhasspy.mobile.widget.microphone

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import org.rhasspy.mobile.widget.R

@DrawableRes
fun getContainerForMicrophoneFabLegacy(
    isRecording: Boolean
): Int {
    return when {
        isRecording -> R.drawable.microphone_widget_background_error
        else        -> R.drawable.microphone_widget_background_primary
    }
}

@ColorRes
fun getMicrophoneFabIconLegacy(
    isMicOn: Boolean,
    isRecording: Boolean
): Int {
    return when {
        isRecording -> if (isMicOn) R.drawable.ic_mic_on_error_container else R.drawable.ic_mic_off_on_error_container
        else        -> if (isMicOn) R.drawable.ic_mic_on_primary_container else R.drawable.ic_mic_off_on_primary_container
    }
}