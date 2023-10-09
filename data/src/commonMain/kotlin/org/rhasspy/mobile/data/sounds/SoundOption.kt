package org.rhasspy.mobile.data.sounds

import kotlinx.serialization.Serializable

@Serializable
sealed interface SoundOption {
    data class Custom(
        val name: String
    ) : SoundOption

    data object Disabled : SoundOption
    data object Default : SoundOption
}