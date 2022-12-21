package org.rhasspy.mobile.viewModels.overlay

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.getSafe
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.indication.IndicationService
import org.rhasspy.mobile.services.indication.IndicationState

class IndicationOverlayViewModel : ViewModel(), KoinComponent {

    val indicationState
        get() = getSafe<IndicationService>()?.indicationState ?: MutableStateFlow(
            IndicationState.Idle
        ).readOnly
    val isShowVisualIndication
        get() = getSafe<IndicationService>()?.isShowVisualIndication
            ?: MutableStateFlow(false).readOnly

}