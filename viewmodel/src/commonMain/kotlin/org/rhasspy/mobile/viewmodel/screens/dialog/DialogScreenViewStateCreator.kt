package org.rhasspy.mobile.viewmodel.screens.dialog

import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.AppSetting

class DialogScreenViewStateCreator(
    private val userConnection: IUserConnection
) {

    operator fun invoke(): StateFlow<DialogScreenViewState> {
        return combineStateFlow(
            AppSetting.isDialogAutoscroll.data,
        ).mapReadonlyState {
            getViewState()
        }
    }

    private fun getViewState(): DialogScreenViewState {
        return DialogScreenViewState(
            isDialogAutoscroll = AppSetting.isDialogAutoscroll.value,
            history = userConnection.pipelineHistory.mapReadonlyState { it.toImmutableList() }
        )
    }

}