package org.rhasspy.mobile.data.serviceoption

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class WakeWordKeywordOption(override val text: StringResource) :
    IOption<WakeWordKeywordOption> {
    ALEXA(MR.strings.alexa),
    AMERICANO(MR.strings.americano),
    BLUEBERRY(MR.strings.blueberry),
    BUMBLEBEE(MR.strings.bumblebee),
    COMPUTER(MR.strings.computer),
    GRAPEFRUIT(MR.strings.grapefruit),
    GRASSHOPPER(MR.strings.grasshopper),
    HEY_GOOGLE(MR.strings.heyGoogle),
    HEY_SIRI(MR.strings.heySiri),
    JARVIS(MR.strings.jarvis),
    OK_GOOGLE(MR.strings.okGoogle),
    PICOVOICE(MR.strings.picovoice),
    PORCUPINE(MR.strings.porcupine),
    TERMINATOR(MR.strings.terminator);

    override fun findValue(value: String): WakeWordKeywordOption {
        return valueOf(value)
    }
}