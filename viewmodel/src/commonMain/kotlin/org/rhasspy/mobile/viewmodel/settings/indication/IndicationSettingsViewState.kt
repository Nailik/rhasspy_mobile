package org.rhasspy.mobile.viewmodel.settings.indication

import androidx.compose.runtime.Stable

@Stable
data class IndicationSettingsViewState internal constructor(
    val isWakeWordLightIndicationEnabled: Boolean,
    val isWakeWordDetectionTurnOnDisplayEnabled: Boolean,
)