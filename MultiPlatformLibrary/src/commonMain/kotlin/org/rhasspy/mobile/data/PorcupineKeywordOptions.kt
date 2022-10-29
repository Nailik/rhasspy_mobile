package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class PorcupineKeywordOptions(override val text: StringResource) : DataEnum<PorcupineKeywordOptions> {
    ALEXA(MR.strings.alexa),
    AMERICANO(MR.strings.americano),
    BLUEBERRY(MR.strings.blueberry),
    BUMBLEBEE(MR.strings.bumblebee),
    COMPUTER(MR.strings.computer),
    GRAPEFRUIT(MR.strings.grapefruit),
    GRASSHOPPER(MR.strings.grasshopper),
    HEY_GOOGLE(MR.strings.hey_google),
    HEY_SIRI(MR.strings.hey_siri),
    JARVIS(MR.strings.jarvis),
    OK_GOOGLE(MR.strings.ok_google),
    PICOVOICE(MR.strings.picovoice),
    PORCUPINE(MR.strings.porcupine),
    TERMINATOR(MR.strings.terminator);

    override fun findValue(value: String): PorcupineKeywordOptions {
        return PorcupineKeywordOptions.valueOf(value)
    }
}