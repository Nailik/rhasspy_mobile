package org.rhasspy.mobile.viewmodel.settings.backgroundservice

import androidx.compose.runtime.Stable

@Stable
data class BackgroundServiceViewState internal constructor(
    val isBackgroundServiceEnabled: Boolean,
    val isBatteryOptimizationDisabled: Boolean
)