package org.rhasspy.mobile.viewmodel.overlay.indication

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.services.indication.IndicationService
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.viewmodel.overlay.indication.IndicationOverlayViewState.Companion.getInitialViewState

class IndicationOverlayViewStateUpdater(
    private val _viewState: MutableStateFlow<IndicationOverlayViewState>,
    private val indicationService: IndicationService
) {

    private val updaterScope = CoroutineScope(Dispatchers.Default)

    init {
        updaterScope.launch {
            combineStateFlow(
                indicationService.indicationState,
                indicationService.isShowVisualIndication
            ).collect {
                _viewState.value = getInitialViewState(indicationService)
            }
        }
    }

}