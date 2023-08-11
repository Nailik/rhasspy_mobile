package org.rhasspy.mobile.data.theme

import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

enum class ThemeType(override val text: StableStringResource) : IOption<LanguageType> {

    System(MR.strings.system.stable),
    Light(MR.strings.light.stable),
    Dark(MR.strings.dark.stable);

    override fun findValue(value: String): LanguageType {
        return LanguageType.valueOf(value)
    }
}