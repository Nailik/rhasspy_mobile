package org.rhasspy.mobile.viewmodel.overlay.indication

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.local.indication.IIndicationService
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState

class IndicationOverlayViewStateCreator(
    private val indicationService: IIndicationService
) {

    operator fun invoke(): StateFlow<IndicationOverlayViewState> {

        return combineStateFlow(
            indicationService.indicationState,
            indicationService.isShowVisualIndication
        ).mapReadonlyState {
            getViewState()
        }

    }

    private fun getViewState(): IndicationOverlayViewState {
        return IndicationOverlayViewState(
            indicationState = indicationService.indicationState.value,
            isShowVisualIndication = indicationService.isShowVisualIndication.value
        )
    }

}