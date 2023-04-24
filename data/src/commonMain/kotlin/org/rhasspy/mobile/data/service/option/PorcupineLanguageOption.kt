package org.rhasspy.mobile.data.service.option

import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable

enum class PorcupineLanguageOption(override val text: StableStringResource, val file: FileResource) :
    IOption<PorcupineLanguageOption> {
    EN(MR.strings.english.stable, MR.files.porcupine_params_en),
    DE(MR.strings.german.stable, MR.files.porcupine_params_de),
    ES(MR.strings.spanish.stable, MR.files.porcupine_params_es),
    FR(MR.strings.french.stable, MR.files.porcupine_params_fr),
    IT(MR.strings.italian.stable, MR.files.porcupine_params_it),
    JA(MR.strings.japanese.stable, MR.files.porcupine_params_ja),
    KO(MR.strings.korean.stable, MR.files.porcupine_params_ko),
    PT(MR.strings.portuguese.stable, MR.files.porcupine_params_pt);

    override fun findValue(value: String): PorcupineLanguageOption {
        return valueOf(value)
    }
}