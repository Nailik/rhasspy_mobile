package org.rhasspy.mobile.viewmodel.screens.dialog

import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.logic.services.dialog.SessionData

data class DialogScreenViewState(
    val history: ImmutableList<DialogTransitionItem>
) {


    data class DialogTransitionItem(
        val action: DialogActionViewState,
        val state: DialogStateViewState
    ) {

        data class DialogStateViewState(
            val name: StableStringResource,
            val sessionData: SessionData?
        )

        data class DialogActionViewState(
            val name: StableStringResource,
            val source: SourceViewState,
            val information: StableStringResource?
        ) {

            data class SourceViewState(
                val type: SourceType,
                val name: StableStringResource
            ) {

                enum class SourceType {
                    Http, Local, MQTT
                }

            }

        }

    }

}
