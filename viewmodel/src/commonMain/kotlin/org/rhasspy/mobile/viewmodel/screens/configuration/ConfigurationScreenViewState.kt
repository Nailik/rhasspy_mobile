package org.rhasspy.mobile.viewmodel.screens.configuration

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.domain.DomainState
import org.rhasspy.mobile.data.service.option.*

@Stable
data class ConfigurationScreenViewState internal constructor(
    val siteId: SiteIdViewState,
    val connectionsItemViewState: ConnectionsItemViewState,
    val pipelineItemViewState: PipelineItemViewState,
    val micDomainItemViewState: MicDomainItemViewState,
    val wakeDomainItemViewState: WakeDomainItemViewState,
    val vadDomainItemViewState: VadDomainItemViewState,
    val asrDomainItemViewState: AsrDomainItemViewState,
    val intentDomainItemViewState: IntentDomainItemViewState,
    val handleDomainItemViewState: HandleDomainItemViewState,
    val ttsDomainItemViewState: TtsDomainItemViewState,
    val sndDomainItemViewState: SndDomainItemViewState,
) {

    @Stable
    data class SiteIdViewState internal constructor(
        val text: StateFlow<String>
    )

    @Stable
    data class ConnectionsItemViewState internal constructor(
        val hasError: Boolean,
    )

    @Stable
    data class PipelineItemViewState internal constructor(
        val pipelineManagerOption: PipelineManagerOption,
    )

    @Stable
    data class MicDomainItemViewState internal constructor(
        val errorStateFlow: StateFlow<DomainState>,
    )

    @Stable
    data class WakeDomainItemViewState internal constructor(
        val wakeWordValueOption: WakeDomainOption,
        val errorStateFlow: StateFlow<DomainState>,
    )

    @Stable
    data class AsrDomainItemViewState internal constructor(
        val asrDomainOption: AsrDomainOption,
    )

    @Stable
    data class VadDomainItemViewState internal constructor(
        val vadDomainOption: VadDomainOption,
    )

    @Stable
    data class IntentDomainItemViewState internal constructor(
        val intentDomainOption: IntentDomainOption,
    )

    @Stable
    data class HandleDomainItemViewState internal constructor(
        val handleDomainOption: HandleDomainOption,
    )

    @Stable
    data class TtsDomainItemViewState internal constructor(
        val ttsDomainOption: TtsDomainOption,
    )

    @Stable
    data class SndDomainItemViewState internal constructor(
        val sndDomainOption: SndDomainOption,
    )
}