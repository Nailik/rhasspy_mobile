package org.rhasspy.mobile.logic.pipeline

import org.rhasspy.mobile.logic.domains.asr.IAsrDomain
import org.rhasspy.mobile.logic.domains.audio.IAudioDomain
import org.rhasspy.mobile.logic.domains.handle.IHandleDomain
import org.rhasspy.mobile.logic.domains.intent.IIntentDomain
import org.rhasspy.mobile.logic.domains.mic.IMicDomain
import org.rhasspy.mobile.logic.domains.snd.ISndDomain
import org.rhasspy.mobile.logic.domains.tts.ITtsDomain
import org.rhasspy.mobile.logic.domains.vad.IVadDomain
import org.rhasspy.mobile.logic.domains.wake.IWakeDomain

internal data class DomainBundle(
    val wakeDomain: IWakeDomain,
    val audioDomain: IAudioDomain,
    val asrDomain: IAsrDomain,
    val handleDomain: IHandleDomain,
    val intentDomain: IIntentDomain,
    val micDomain: IMicDomain,
    val sndDomain: ISndDomain,
    val ttsDomain: ITtsDomain,
    val vadDomain: IVadDomain,
) {

    fun dispose() {
        wakeDomain.dispose()
        audioDomain.dispose()
        asrDomain.dispose()
        handleDomain.dispose()
        intentDomain.dispose()
        micDomain.dispose()
        sndDomain.dispose()
        ttsDomain.dispose()
        vadDomain.dispose()
    }

}