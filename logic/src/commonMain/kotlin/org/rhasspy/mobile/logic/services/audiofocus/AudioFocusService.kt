package org.rhasspy.mobile.logic.services.audiofocus

import org.rhasspy.mobile.data.audiofocus.AudioFocusOption
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Dialog
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Notification
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Record
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Sound
import org.rhasspy.mobile.platformspecific.media.AudioFocusUtil
import org.rhasspy.mobile.settings.AppSetting

interface IAudioFocusService {

    fun request(reason: AudioFocusRequestReason)
    fun abandon(reason: AudioFocusRequestReason)

}

internal class AudioFocusService : IAudioFocusService {

    private var reason: AudioFocusRequestReason? = null

    private fun shouldRequest(reason: AudioFocusRequestReason): Boolean {
        return when (reason) {
            Notification -> AppSetting.isAudioFocusOnNotification.value
            Sound        -> AppSetting.isAudioFocusOnSound.value
            Record       -> AppSetting.isAudioFocusOnRecord.value
            Dialog       -> AppSetting.isAudioFocusOnDialog.value
        }
    }

    override fun request(reason: AudioFocusRequestReason) {
        if (AppSetting.audioFocusOption.value != AudioFocusOption.Disabled) {

            if (shouldRequest(reason)) {
                this.reason = reason
                AudioFocusUtil.request(reason, AppSetting.audioFocusOption.value)
            }

        }
    }

    override fun abandon(reason: AudioFocusRequestReason) {
        if (AppSetting.audioFocusOption.value != AudioFocusOption.Disabled) {

            if (shouldRequest(reason) && (reason.ordinal > (this.reason?.ordinal ?: -1))) {
                this.reason = null
                AudioFocusUtil.abandon(reason, AppSetting.audioFocusOption.value)
            }

        }
    }

}