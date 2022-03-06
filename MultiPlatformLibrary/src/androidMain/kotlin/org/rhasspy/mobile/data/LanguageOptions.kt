package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class LanguageOptions(override val text: StringResource) : DataEnum {
    English(MR.strings.en),
    German(MR.strings.de)
}