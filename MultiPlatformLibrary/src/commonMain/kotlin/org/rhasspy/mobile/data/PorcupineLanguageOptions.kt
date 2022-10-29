package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class PorcupineLanguageOptions(override val text: StringResource) : DataEnum<PorcupineLanguageOptions> {
    EN(MR.strings.english),
    DE(MR.strings.german),
    ES(MR.strings.spanish),
    FR(MR.strings.french),
    IT(MR.strings.italian),
    JA(MR.strings.japanese),
    KO(MR.strings.korean),
    PT(MR.strings.portuguese);

    override fun findValue(value: String): PorcupineLanguageOptions {
        return valueOf(value)
    }
}