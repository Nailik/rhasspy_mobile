package org.rhasspy.mobile.data.service.option

import dev.icerock.moko.resources.FileResource
import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

@Serializable
enum class PorcupineLanguageOption(override val text: StableStringResource, val file: FileResource) : IOption {

    EN(MR.strings.english.stable, MR.files.porcupine_params),
    AR(MR.strings.arabic.stable, MR.files.porcupine_params_ar),
    DE(MR.strings.german.stable, MR.files.porcupine_params_de),
    ES(MR.strings.spanish.stable, MR.files.porcupine_params_es),
    FA(MR.strings.farsi.stable, MR.files.porcupine_params_fa),
    FR(MR.strings.french.stable, MR.files.porcupine_params_fr),
    HI(MR.strings.hindi.stable, MR.files.porcupine_params_hi),
    IT(MR.strings.italian.stable, MR.files.porcupine_params_it),
    JA(MR.strings.japanese.stable, MR.files.porcupine_params_ja),
    KO(MR.strings.korean.stable, MR.files.porcupine_params_ko),
    NL(MR.strings.dutch.stable, MR.files.porcupine_params_nl),
    PL(MR.strings.polish.stable, MR.files.porcupine_params_pl),
    PT(MR.strings.portuguese.stable, MR.files.porcupine_params_pt),
    RU(MR.strings.russian.stable, MR.files.porcupine_params_ru),
    SV(MR.strings.swedish.stable, MR.files.porcupine_params_sv),
    VN(MR.strings.vietnamese.stable, MR.files.porcupine_params_vn),
    ZH(MR.strings.chinese.stable, MR.files.porcupine_params_zh);

}