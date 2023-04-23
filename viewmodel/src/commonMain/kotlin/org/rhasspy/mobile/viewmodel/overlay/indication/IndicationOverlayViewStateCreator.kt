package org.rhasspy.mobile.viewmodel.overlay.indication

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.services.indication.IndicationService
import org.rhasspy.mobile.platformspecific.combineStateFlow

class IndicationOverlayViewStateCreator(
    private val indicationService: IndicationService
) {
    private val updaterScope = CoroutineScope(Dispatchers.Default)

    operator fun invoke(): StateFlow<IndicationOverlayViewState> {
        val viewState = MutableStateFlow(getViewState())

        updaterScope.launch {
            combineStateFlow(
                indicationService.indicationState,
                indicationService.isShowVisualIndication
            ).collect {
                viewState.value = getViewState()
            }
        }

        return viewState
    }

    private fun getViewState(): IndicationOverlayViewState {
        return IndicationOverlayViewState(
            indicationState = indicationService.indicationState.value,
            isShowVisualIndication = indicationService.isShowVisualIndication.value
        )
    }

}