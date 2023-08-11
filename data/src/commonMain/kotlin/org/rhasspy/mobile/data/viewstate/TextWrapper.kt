package org.rhasspy.mobile.data.viewstate

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.resource.StableStringResource

@Stable
sealed interface TextWrapper {

    data class TextWrapperStableStringResource(
        val data: StableStringResource
    ) : TextWrapper

    data class TextWrapperString(
        val data: String
    ) : TextWrapper

}