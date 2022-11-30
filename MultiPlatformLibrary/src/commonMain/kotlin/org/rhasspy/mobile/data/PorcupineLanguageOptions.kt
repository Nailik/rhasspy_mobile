package org.rhasspy.mobile.data

import dev.icerock.moko.resources.FileResource
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class PorcupineLanguageOptions(override val text: StringResource, val file: FileResource) : DataEnum<PorcupineLanguageOptions> {
    EN(MR.strings.english, MR.files.porcupine_params),
    DE(MR.strings.german, MR.files.porcupine_params_de),
    ES(MR.strings.spanish, MR.files.porcupine_params_es),
    FR(MR.strings.french, MR.files.porcupine_params_fr),
    IT(MR.strings.italian, MR.files.porcupine_params_it),
    JA(MR.strings.japanese, MR.files.porcupine_params_ja),
    KO(MR.strings.korean, MR.files.porcupine_params_ko),
    PT(MR.strings.portuguese, MR.files.porcupine_params_pt);

    override fun findValue(value: String): PorcupineLanguageOptions {
        return valueOf(value)
    }
}