package org.rhasspy.mobile.platformspecific.media

import org.rhasspy.mobile.data.audiofocus.AudioFocusOption
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason

expect object AudioFocusUtil {

    fun request(reason: AudioFocusRequestReason, audioFocusOption: AudioFocusOption)

    fun abandon(reason: AudioFocusRequestReason, audioFocusOption: AudioFocusOption)

}