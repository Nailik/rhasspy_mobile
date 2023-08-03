package org.rhasspy.mobile.viewmodel.settings.microphoneoverlay

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.platformspecific.toImmutableList

@Stable
data class MicrophoneOverlaySettingsViewState internal constructor(
    val microphoneOverlaySizeOption: MicrophoneOverlaySizeOption,
    val isMicrophoneOverlayWhileAppEnabled: Boolean
) {

    val microphoneOverlaySizeOptions: ImmutableList<MicrophoneOverlaySizeOption> =
        MicrophoneOverlaySizeOption.values().toImmutableList()

}