package org.rhasspy.mobile.viewmodel.overlay.indication

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.viewmodel.KViewModel

@Stable
class IndicationOverlayViewModel(
    viewStateCreator: IndicationOverlayViewStateCreator
) : KViewModel() {

    val viewState: StateFlow<IndicationOverlayViewState> = viewStateCreator()

}