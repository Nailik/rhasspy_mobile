package org.rhasspy.mobile.logic.connections.mqtt

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.viewstate.TextWrapper
import org.rhasspy.mobile.data.viewstate.TextWrapper.TextWrapperStableStringResource
import org.rhasspy.mobile.data.viewstate.TextWrapper.TextWrapperString

internal sealed interface MqttResult {

    data object Success : MqttResult
    data class Error(val message: TextWrapper) : MqttResult {

        constructor(text: String) : this(TextWrapperString(text))
        constructor(resource: StableStringResource) : this(TextWrapperStableStringResource(resource))
        constructor(exception: Exception) : this(TextWrapperString("$exception ${exception.message}"))

    }

}