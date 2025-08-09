package org.rhasspy.mobile.viewmodel.settings.microphoneoverlay

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption

@Stable
data class MicrophoneOverlaySettingsViewState(
    val microphoneOverlaySizeOption: MicrophoneOverlaySizeOption,
    val isMicrophoneOverlayWhileAppEnabled: Boolean
) {

    val microphoneOverlaySizeOptions: ImmutableList<MicrophoneOverlaySizeOption> =
        MicrophoneOverlaySizeOption.entries.toImmutableList()

}