package org.rhasspy.mobile.viewmodel.overlay.indication

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent

@Stable
class IndicationOverlayViewModel(
    viewStateCreator: IndicationOverlayViewStateCreator
) : ViewModel(), KoinComponent {

    val viewState: StateFlow<IndicationOverlayViewState> = viewStateCreator()

}