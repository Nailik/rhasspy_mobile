package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class LanguageOptions(override val text: StringResource, val code: String) :
    DataEnum<LanguageOptions> {
    English(MR.strings.en, "en"),
    German(MR.strings.de, "de");

    override fun findValue(value: String): LanguageOptions {
        return valueOf(value)
    }
}