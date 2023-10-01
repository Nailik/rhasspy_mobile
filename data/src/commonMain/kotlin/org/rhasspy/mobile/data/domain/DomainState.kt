package org.rhasspy.mobile.data.domain

import org.rhasspy.mobile.data.viewstate.TextWrapper

sealed interface DomainState {

    data object NoError : DomainState

    data class Error(val textWrapper: TextWrapper) : DomainState

    data object Loading : DomainState

}