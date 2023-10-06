package org.rhasspy.mobile.viewmodel.overlay.indication

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState

class IndicationOverlayViewStateCreator(
    private val userConnection: IUserConnection,
) {

    operator fun invoke(): StateFlow<IndicationOverlayViewState> {

        return combineStateFlow(
            userConnection.indicationState,
            userConnection.showVisualIndicationState
        ).mapReadonlyState {
            getViewState()
        }

    }

    private fun getViewState(): IndicationOverlayViewState {
        return IndicationOverlayViewState(
            indicationState = userConnection.indicationState.value,
            isShowVisualIndication = userConnection.showVisualIndicationState.value
        )
    }

}