package org.rhasspy.mobile.data.language

import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption

enum class LanguageType(override val text: StableStringResource, val code: String) : IOption<LanguageType> {

    English(MR.strings.en.stable, "en"),
    German(MR.strings.de.stable, "de");

    override fun findValue(value: String): LanguageType {
        return valueOf(value)
    }
}