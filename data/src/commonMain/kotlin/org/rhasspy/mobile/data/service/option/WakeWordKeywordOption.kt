package org.rhasspy.mobile.data.service.option

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

enum class WakeWordKeywordOption(override val text: StableStringResource) : IOption<WakeWordKeywordOption> {

    ALEXA(MR.strings.alexa.stable),
    AMERICANO(MR.strings.americano.stable),
    BLUEBERRY(MR.strings.blueberry.stable),
    BUMBLEBEE(MR.strings.bumblebee.stable),
    COMPUTER(MR.strings.computer.stable),
    GRAPEFRUIT(MR.strings.grapefruit.stable),
    GRASSHOPPER(MR.strings.grasshopper.stable),
    HEY_GOOGLE(MR.strings.heyGoogle.stable),
    HEY_SIRI(MR.strings.heySiri.stable),
    JARVIS(MR.strings.jarvis.stable),
    OK_GOOGLE(MR.strings.okGoogle.stable),
    PICOVOICE(MR.strings.picovoice.stable),
    PORCUPINE(MR.strings.porcupine.stable),
    TERMINATOR(MR.strings.terminator.stable);

    override val internalEntries get() = entries

}