package org.rhasspy.mobile.data.domain

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption

@Serializable
data class IntentDomainData(
    val option: IntentRecognitionOption,
    val isRhasspy2HermesHttpHandleWithRecognition: Boolean,
)