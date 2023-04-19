package org.rhasspy.mobile.viewmodel.overlay.indication

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.logic.services.indication.IndicationState

@Stable
data class IndicationOverlayViewState internal constructor(
    val indicationState: IndicationState,
    val isShowVisualIndication: Boolean
)