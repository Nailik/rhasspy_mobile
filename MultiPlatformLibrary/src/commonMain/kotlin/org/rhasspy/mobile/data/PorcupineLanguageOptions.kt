package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class PorcupineLanguageOptions(override val text: StringResource) : DataEnum<PorcupineLanguageOptions> {
    EN(MR.strings.english),
    DE(MR.strings.german),
    FR(MR.strings.french),
    ES(MR.strings.spanish);

    override fun findValue(value: String): PorcupineLanguageOptions {
        return valueOf(value)
    }
}