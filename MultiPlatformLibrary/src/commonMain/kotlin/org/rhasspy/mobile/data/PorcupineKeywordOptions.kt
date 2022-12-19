package org.rhasspy.mobile.data

import dev.icerock.moko.resources.FileResource
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class PorcupineKeywordOptions(override val text: StringResource, val file: FileResource, val language: PorcupineLanguageOptions) :
    DataEnum<PorcupineKeywordOptions> {
    ALEXA(MR.strings.alexa, MR.files.porcupine_wakeword_en_alexa_android, PorcupineLanguageOptions.EN),
    AMERICANO(MR.strings.americano, MR.files.porcupine_wakeword_en_americano_android, PorcupineLanguageOptions.EN),
    BLUEBERRY(MR.strings.blueberry, MR.files.porcupine_wakeword_en_blueberry_android, PorcupineLanguageOptions.EN),
    BUMBLEBEE(MR.strings.bumblebee, MR.files.porcupine_wakeword_en_bumblebee_android, PorcupineLanguageOptions.EN),
    COMPUTER(MR.strings.computer, MR.files.porcupine_wakeword_en_computer_android, PorcupineLanguageOptions.EN),
    GRAPEFRUIT(MR.strings.grapefruit, MR.files.porcupine_wakeword_en_grapefruit_android, PorcupineLanguageOptions.EN),
    GRASSHOPPER(MR.strings.grasshopper, MR.files.porcupine_wakeword_en_grasshopper_android, PorcupineLanguageOptions.EN),
    HEY_BARISTA(MR.strings.hey_barista, MR.files.porcupine_wakeword_en_hey_barista_android, PorcupineLanguageOptions.EN),
    HEY_GOOGLE(MR.strings.hey_google, MR.files.porcupine_wakeword_en_hey_google_android, PorcupineLanguageOptions.EN),
    HEY_SIRI(MR.strings.hey_siri, MR.files.porcupine_wakeword_en_hey_siri_android, PorcupineLanguageOptions.EN),
    JARVIS(MR.strings.jarvis, MR.files.porcupine_wakeword_en_jarvis_android, PorcupineLanguageOptions.EN),
    OK_GOOGLE(MR.strings.ok_google, MR.files.porcupine_wakeword_en_ok_google_android, PorcupineLanguageOptions.EN),
    PICO_CLOCK(MR.strings.pico_clock, MR.files.porcupine_wakeword_en_pico_clock_android, PorcupineLanguageOptions.EN),
    PICOVOICE(MR.strings.picovoice, MR.files.porcupine_wakeword_en_picovoice_android, PorcupineLanguageOptions.EN),
    PORCUPINE(MR.strings.porcupine, MR.files.porcupine_wakeword_en_porcupine_android, PorcupineLanguageOptions.EN),
    TERMINATOR(MR.strings.terminator, MR.files.porcupine_wakeword_en_terminator_android, PorcupineLanguageOptions.EN),
    ANANAS(MR.strings.ananas, MR.files.porcupine_wakeword_de_ananas_android, PorcupineLanguageOptions.DE),
    HEUSCHRECKE(MR.strings.heuschrecke, MR.files.porcupine_wakeword_de_heuschrecke_android, PorcupineLanguageOptions.DE),
    HIMBEERE(MR.strings.himbeere, MR.files.porcupine_wakeword_de_himbeere_android, PorcupineLanguageOptions.DE),
    LEGUAN(MR.strings.leguan, MR.files.porcupine_wakeword_de_leguan_android, PorcupineLanguageOptions.DE),
    STACHELSCHWEIN(MR.strings.stachelschwein, MR.files.porcupine_wakeword_de_stachelschwein_android, PorcupineLanguageOptions.DE),
    EMPAREDADO(MR.strings.emparedado, MR.files.porcupine_wakeword_es_emparedado_android, PorcupineLanguageOptions.ES),
    LEOPARDO(MR.strings.leopardo, MR.files.porcupine_wakeword_es_leopardo_android, PorcupineLanguageOptions.ES),
    MANZANA(MR.strings.manzana, MR.files.porcupine_wakeword_es_manzana_android, PorcupineLanguageOptions.ES),
    MURCIELAGO(MR.strings.murcielago, MR.files.porcupine_wakeword_es_murcielago_android, PorcupineLanguageOptions.ES),
    FRAMBOISE(MR.strings.framboise, MR.files.porcupine_wakeword_fr_framboise_android, PorcupineLanguageOptions.FR),
    MON_CHOUCHOU(MR.strings.monChouchou, MR.files.porcupine_wakeword_fr_mon_chouchou_android, PorcupineLanguageOptions.FR),
    PARAPLUIE(MR.strings.parapluie, MR.files.porcupine_wakeword_fr_parapluie_android, PorcupineLanguageOptions.FR),
    PERROQUET(MR.strings.perroquet, MR.files.porcupine_wakeword_fr_perroquet_android, PorcupineLanguageOptions.FR),
    TOURNESOL(MR.strings.tournesol, MR.files.porcupine_wakeword_fr_tournesol_android, PorcupineLanguageOptions.FR),
    CAMERIERE(MR.strings.cameriere, MR.files.porcupine_wakeword_it_cameriere_android, PorcupineLanguageOptions.IT),
    ESPRESSO(MR.strings.espresso, MR.files.porcupine_wakeword_it_espresso_android, PorcupineLanguageOptions.IT),
    PORCOSPINO(MR.strings.porcospino, MR.files.porcupine_wakeword_it_porcospino_android, PorcupineLanguageOptions.IT),
    SILENZIO_BRUNO(MR.strings.silencioBruno, MR.files.porcupine_wakeword_it_silenzio_bruno_android, PorcupineLanguageOptions.IT),
    BUSHI(MR.strings.bushi, MR.files.porcupine_wakeword_ja_bushi_android, PorcupineLanguageOptions.JA),
    NINJA(MR.strings.ninja, MR.files.porcupine_wakeword_ja_ninja_android, PorcupineLanguageOptions.JA),
    RINGO(MR.strings.ringo, MR.files.porcupine_wakeword_ja_ringo_android, PorcupineLanguageOptions.JA),
    AISEUKEULIM(MR.strings.aieseukeulim, MR.files.porcupine_wakeword_ko_aiseukeulim_android, PorcupineLanguageOptions.KO),
    BIGSEUBI(MR.strings.bigseubi, MR.files.porcupine_wakeword_ko_bigseubi_android, PorcupineLanguageOptions.KO),
    KOPPULSO(MR.strings.koppulso, MR.files.porcupine_wakeword_ko_koppulso_android, PorcupineLanguageOptions.KO),
    ABACAXI(MR.strings.abacaxi, MR.files.porcupine_wakeword_pt_abacaxi_android, PorcupineLanguageOptions.PT),
    FENOMENO(MR.strings.fenomeno, MR.files.porcupine_wakeword_pt_fenomeno_android, PorcupineLanguageOptions.PT),
    FORMIGA(MR.strings.formiga, MR.files.porcupine_wakeword_pt_formiga_android, PorcupineLanguageOptions.PT),
    PORCO_ESPINHO(MR.strings.porcoEspinho, MR.files.porcupine_wakeword_pt_porco_espinho_android, PorcupineLanguageOptions.PT);

    override fun findValue(value: String): PorcupineKeywordOptions {
        return PorcupineKeywordOptions.valueOf(value)
    }
}
/*
EN(MR.strings.english, MR.files.porcupine_params),
    DE(MR.strings.german, MR.files.porcupine_params_de),
    ES(MR.strings.spanish, MR.files.porcupine_params_es),
    FR(MR.strings.french, MR.files.porcupine_params_fr),
    IT(MR.strings.italian, MR.files.porcupine_params_it),
    JA(MR.strings.japanese, MR.files.porcupine_params_ja),
    KO(MR.strings.korean, MR.files.porcupine_params_ko),
    PT(MR.strings.portuguese, MR.files.porcupine_params_pt);

    porcupine_wakeword_es_murciélago_android
 */