package org.rhasspy.mobile.viewmodel.overlay

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.logic.getSafe
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.logic.services.indication.IndicationService
import org.rhasspy.mobile.logic.services.indication.IndicationState

class IndicationOverlayViewModel : ViewModel(), KoinComponent {

    val indicationState
        get() = getSafe<IndicationService>()?.indicationState
            ?: MutableStateFlow(IndicationState.Idle).readOnly

    val isShowVisualIndication
        get() = getSafe<IndicationService>()?.isShowVisualIndication
            ?: MutableStateFlow(false).readOnly

}