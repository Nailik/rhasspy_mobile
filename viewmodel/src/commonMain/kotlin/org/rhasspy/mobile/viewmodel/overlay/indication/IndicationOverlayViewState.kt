package org.rhasspy.mobile.viewmodel.overlay.indication

import org.rhasspy.mobile.logic.services.indication.IndicationService
import org.rhasspy.mobile.logic.services.indication.IndicationState

data class IndicationOverlayViewState internal constructor(
    val indicationState: IndicationState,
    val isShowVisualIndication: Boolean
) {

    companion object {
        fun getInitialViewState(indicationService: IndicationService): IndicationOverlayViewState {
            return IndicationOverlayViewState(
                indicationState = indicationService.indicationState.value,
                isShowVisualIndication = indicationService.isShowVisualIndication.value
            )
        }
    }

}