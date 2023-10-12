package org.rhasspy.mobile.data.sounds

import kotlinx.serialization.Serializable

@Serializable
sealed interface IndicationSoundOption {

    data class Custom(val file: String) : IndicationSoundOption

    data object Disabled : IndicationSoundOption

    data object Default : IndicationSoundOption

}