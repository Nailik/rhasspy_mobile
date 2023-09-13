package org.rhasspy.mobile.viewmodel.configuration.audioinput

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.domain.AudioInputDomainData

@Stable
data class AudioInputConfigurationViewState internal constructor(
    val data: AudioInputDomainData,
)