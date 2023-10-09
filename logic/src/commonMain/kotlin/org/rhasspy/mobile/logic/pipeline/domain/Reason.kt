package org.rhasspy.mobile.logic.pipeline.domain

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.viewstate.TextWrapper

sealed interface Reason {

    data object Disabled : Reason

    data object Timeout : Reason

    data class Error(val information: TextWrapper) : Reason {

        constructor(text: String) : this(TextWrapper.TextWrapperString(text))
        constructor(resource: StableStringResource) : this(TextWrapper.TextWrapperStableStringResource(resource))
        constructor(exception: Exception) : this(TextWrapper.TextWrapperString("$exception ${exception.message}"))

    }
}