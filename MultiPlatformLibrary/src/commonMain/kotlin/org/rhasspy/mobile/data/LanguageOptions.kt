package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class LanguageOptions(override val text: StringResource) : DataEnum<LanguageOptions> {
    English(MR.strings.en),
    German(MR.strings.de);

    override fun findValue(value: String): LanguageOptions {
        return valueOf(value)
    }
}