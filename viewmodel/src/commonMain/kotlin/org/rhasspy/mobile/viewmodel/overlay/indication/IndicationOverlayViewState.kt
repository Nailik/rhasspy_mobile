package org.rhasspy.mobile.viewmodel.overlay.indication

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.indication.IndicationState

@Stable
data class IndicationOverlayViewState(
    val indicationState: IndicationState,
    val isShowVisualIndication: Boolean
)