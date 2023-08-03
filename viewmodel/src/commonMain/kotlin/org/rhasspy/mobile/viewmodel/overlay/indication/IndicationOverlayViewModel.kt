package org.rhasspy.mobile.viewmodel.overlay.indication

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class IndicationOverlayViewModel(
    viewStateCreator: IndicationOverlayViewStateCreator
) : ScreenViewModel() {

    val viewState: StateFlow<IndicationOverlayViewState> = viewStateCreator()

}