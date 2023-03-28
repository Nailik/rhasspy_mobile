package org.rhasspy.mobile.data.language

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.serviceoption.IOption

enum class LanguageType(override val text: StringResource, val code: String) :
    IOption<LanguageType> {
    English(MR.strings.en, "en"),
    German(MR.strings.de, "de");

    override fun findValue(value: String): LanguageType {
        return valueOf(value)
    }
}