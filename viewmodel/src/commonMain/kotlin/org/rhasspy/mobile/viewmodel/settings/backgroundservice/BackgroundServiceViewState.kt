package org.rhasspy.mobile.viewmodel.settings.backgroundservice

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.resource.StableStringResource

@Stable
data class BackgroundServiceViewState internal constructor(
    val isBackgroundServiceEnabled: Boolean,
    val isBatteryOptimizationDisabled: Boolean,
    val snackBarText: StableStringResource? = null
)