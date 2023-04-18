package org.rhasspy.mobile.viewmodel.overlay.indication

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.logic.getSafe
import org.rhasspy.mobile.logic.services.indication.IndicationService
import org.rhasspy.mobile.logic.services.indication.IndicationState
import org.rhasspy.mobile.platformspecific.readOnly

class IndicationOverlayViewModel(
    indicationService: IndicationService
) : ViewModel(), KoinComponent {

    private val _viewState = MutableStateFlow(
        IndicationOverlayViewState.getInitialViewState(indicationService)
    )
    val viewState = _viewState.readOnly

    init {
        IndicationOverlayViewStateUpdater(
            _viewState = _viewState,
            indicationService = indicationService
        )
    }

    val indicationState
        get() = getSafe<IndicationService>()?.indicationState
            ?: MutableStateFlow(IndicationState.Idle).readOnly

    val isShowVisualIndication
        get() = getSafe<IndicationService>()?.isShowVisualIndication
            ?: MutableStateFlow(false).readOnly

}