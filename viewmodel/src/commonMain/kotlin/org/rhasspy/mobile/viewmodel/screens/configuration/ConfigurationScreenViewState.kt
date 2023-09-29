package org.rhasspy.mobile.viewmodel.screens.configuration

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.option.*
import org.rhasspy.mobile.data.viewstate.TextWrapper

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
        val dialogManagementOption: DialogManagementOption,
    )

    @Stable
    data class MicDomainItemViewState internal constructor(
        val serviceState: ServiceViewState
    )

    @Stable
    data class WakeDomainItemViewState internal constructor(
        val wakeWordValueOption: WakeWordOption,
        val error: TextWrapper?,
    )

    @Stable
    data class AsrDomainItemViewState internal constructor(
        val speechToTextOption: SpeechToTextOption,
    )

    @Stable
    data class VadDomainItemViewState internal constructor(
        val voiceActivityDetectionOption: VoiceActivityDetectionOption,
    )

    @Stable
    data class IntentDomainItemViewState internal constructor(
        val intentRecognitionOption: IntentRecognitionOption,
    )

    @Stable
    data class HandleDomainItemViewState internal constructor(
        val intentHandlingOption: IntentHandlingOption,
    )

    @Stable
    data class TtsDomainItemViewState internal constructor(
        val textToSpeechOption: TextToSpeechOption,
    )

    @Stable
    data class SndDomainItemViewState internal constructor(
        val audioPlayingOption: AudioPlayingOption,
    )
}