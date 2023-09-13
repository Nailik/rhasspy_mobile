package org.rhasspy.mobile.platformspecific.media

import org.rhasspy.mobile.data.audiofocus.AudioFocusOption
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason

actual object AudioFocusUtil {

    actual fun request(reason: AudioFocusRequestReason, audioFocusOption: AudioFocusOption) {
        //TODO #514
    }

    actual fun abandon(reason: AudioFocusRequestReason, audioFocusOption: AudioFocusOption) {
        //TODO #514
    }

}