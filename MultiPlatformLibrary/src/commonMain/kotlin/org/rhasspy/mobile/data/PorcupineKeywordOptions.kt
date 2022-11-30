package org.rhasspy.mobile.data

import dev.icerock.moko.resources.FileResource
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class PorcupineKeywordOptions(override val text: StringResource, val file: FileResource) : DataEnum<PorcupineKeywordOptions> {
    ALEXA(MR.strings.alexa, MR.files.porcupine_wakeword_alexa_android),
    AMERICANO(MR.strings.americano, MR.files.porcupine_wakeword_americano_android),
    BLUEBERRY(MR.strings.blueberry, MR.files.porcupine_wakeword_blueberry_android),
    BUMBLEBEE(MR.strings.bumblebee, MR.files.porcupine_wakeword_bumblebee_android),
    COMPUTER(MR.strings.computer, MR.files.porcupine_wakeword_computer_android),
    GRAPEFRUIT(MR.strings.grapefruit, MR.files.porcupine_wakeword_grapefruit_android),
    GRASSHOPPER(MR.strings.grasshopper, MR.files.porcupine_wakeword_grasshopper_android),
    HEY_BARISTA(MR.strings.hey_barista, MR.files.porcupine_wakeword_hey_barista_android),
    HEY_GOOGLE(MR.strings.hey_google, MR.files.porcupine_wakeword_hey_google_android),
    HEY_SIRI(MR.strings.hey_siri, MR.files.porcupine_wakeword_hey_siri_android),
    JARVIS(MR.strings.jarvis, MR.files.porcupine_wakeword_jarvis_android),
    OK_GOOGLE(MR.strings.ok_google, MR.files.porcupine_wakeword_ok_google_android),
    PICO_CLOCK(MR.strings.pico_clock, MR.files.porcupine_wakeword_pico_clock_android),
    PICOVOICE(MR.strings.picovoice, MR.files.porcupine_wakeword_picovoice_android),
    PORCUPINE(MR.strings.porcupine, MR.files.porcupine_wakeword_porcupine_android),
    TERMINATOR(MR.strings.terminator, MR.files.terminator_android);

    override fun findValue(value: String): PorcupineKeywordOptions {
        return PorcupineKeywordOptions.valueOf(value)
    }
}