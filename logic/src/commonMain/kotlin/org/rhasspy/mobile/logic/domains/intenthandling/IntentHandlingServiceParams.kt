package org.rhasspy.mobile.logic.domains.intenthandling

import org.rhasspy.mobile.data.service.option.IntentHandlingOption

internal data class IntentHandlingServiceParams(
    val intentHandlingOption: IntentHandlingOption,
    val httpConnectionId: Long?
)