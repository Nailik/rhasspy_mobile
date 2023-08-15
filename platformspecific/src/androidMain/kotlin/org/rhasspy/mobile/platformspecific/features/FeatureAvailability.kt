package org.rhasspy.mobile.platformspecific.features

import android.os.Build

actual object FeatureAvailability {

    actual val isPauseRecordingOnPlaybackFeatureEnabled: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

}