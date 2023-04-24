package org.rhasspy.mobile.platformspecific.media

import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason
import org.rhasspy.mobile.data.audiofocus.AudioFocusType

expect object AudioFocusUtil {

    fun request(reason: AudioFocusRequestReason, audioFocusType: AudioFocusType)

    fun abandon(reason: AudioFocusRequestReason, audioFocusType: AudioFocusType)

}