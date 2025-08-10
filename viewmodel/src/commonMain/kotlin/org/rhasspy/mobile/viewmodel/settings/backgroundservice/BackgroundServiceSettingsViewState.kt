package org.rhasspy.mobile.viewmodel.settings.backgroundservice

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.resource.StableStringResource

@Stable
data class BackgroundServiceSettingsViewState(
    val isBackgroundServiceEnabled: Boolean,
    val isBatteryOptimizationDeactivationEnabled: Boolean,
    val snackBarText: StableStringResource? = null,
)