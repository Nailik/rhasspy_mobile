package org.rhasspy.mobile.data.service.option

import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

enum class PorcupineLanguageOption(
    override val text: StableStringResource,
    val file: FileResource
) :
    IOption<PorcupineLanguageOption> {
    EN(MR.strings.english.stable, MR.files.porcupine_params_pv),
    DE(MR.strings.german.stable, MR.files.porcupine_params_de_pv),
    ES(MR.strings.spanish.stable, MR.files.porcupine_params_es_pv),
    FR(MR.strings.french.stable, MR.files.porcupine_params_fr_pv),
    IT(MR.strings.italian.stable, MR.files.porcupine_params_it_pv),
    JA(MR.strings.japanese.stable, MR.files.porcupine_params_ja_pv),
    KO(MR.strings.korean.stable, MR.files.porcupine_params_ko_pv),
    PT(MR.strings.portuguese.stable, MR.files.porcupine_params_pt_pv);

    override fun findValue(value: String): PorcupineLanguageOption {
        return valueOf(value)
    }
}