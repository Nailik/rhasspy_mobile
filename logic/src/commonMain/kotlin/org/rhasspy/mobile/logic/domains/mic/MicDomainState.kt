package org.rhasspy.mobile.logic.domains.mic

import org.rhasspy.mobile.data.domain.DomainState
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.viewstate.TextWrapper
import org.rhasspy.mobile.data.viewstate.TextWrapper.TextWrapperStableStringResource
import org.rhasspy.mobile.resources.MR

sealed interface MicDomainState {

    data object NoError : MicDomainState

    data object Loading : MicDomainState

    data class Error(val textWrapper: TextWrapper) : MicDomainState

    data object MicrophonePermissionMissing : MicDomainState

    fun asDomainState(): DomainState {
        return when (this) {
            is Error                       -> DomainState.Error(this.textWrapper)
            is Loading                     -> DomainState.Loading
            is MicrophonePermissionMissing -> DomainState.Error(TextWrapperStableStringResource(MR.strings.microphonePermissionMissing.stable))
            is NoError                     -> DomainState.NoError
        }
    }

}