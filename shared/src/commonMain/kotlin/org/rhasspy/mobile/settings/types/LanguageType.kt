package org.rhasspy.mobile.settings.types

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.settings.option.IOption

enum class LanguageType(override val text: StringResource, val code: String) :
    IOption<LanguageType> {
    English(MR.strings.en, "en"),
    German(MR.strings.de, "de");

    override fun findValue(value: String): LanguageType {
        return valueOf(value)
    }
}