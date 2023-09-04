package org.rhasspy.mobile.logic.domains.audioplaying

import org.rhasspy.mobile.data.connection.HttpConnectionParams
import org.rhasspy.mobile.data.service.option.AudioPlayingOption

internal data class AudioPlayingServiceParams(
    val audioPlayingOption: AudioPlayingOption,
    val httpConnectionParams: HttpConnectionParams
)