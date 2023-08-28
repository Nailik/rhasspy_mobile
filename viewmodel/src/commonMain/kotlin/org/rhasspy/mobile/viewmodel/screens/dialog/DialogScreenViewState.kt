package org.rhasspy.mobile.viewmodel.screens.dialog

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.Instant
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.logic.domains.dialog.SessionData

data class DialogScreenViewState(
    val isDialogAutoscroll: Boolean,
    val history: ImmutableList<DialogInformationItem>
)

@Stable
sealed interface DialogInformationItem {

    data class DialogStateViewState(
        val name: StableStringResource,
        val timeStamp: Instant,
        val sessionData: SessionData?
    ) : DialogInformationItem

    data class DialogActionViewState(
        val name: StableStringResource,
        val timeStamp: Instant,
        val source: SourceViewState,
        val information: StableStringResource?
    ) : DialogInformationItem {

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