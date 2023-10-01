package org.rhasspy.mobile.platformspecific.features

import android.media.audiofx.AutomaticGainControl
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

actual object FeatureAvailability {

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    actual val isPauseRecordingOnPlaybackFeatureEnabled: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    actual val isUseAutomaticGainControlEnabled: Boolean = AutomaticGainControl.isAvailable()

}