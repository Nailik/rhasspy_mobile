package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import org.rhasspy.mobile.data.domain.IntentDomainData
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewState.IntentRecognitionConfigurationData

class IntentRecognitionConfigurationDataMapper {

    operator fun invoke(data: IntentDomainData): IntentRecognitionConfigurationData {
        return IntentRecognitionConfigurationData(
            intentDomainOption = data.option,
            isRhasspy2HermesHttpHandleWithRecognition = data.isRhasspy2HermesHttpHandleWithRecognition,
            rhasspy2HermesHttpHandleTimeout = data.rhasspy2HermesHttpHandleTimeout,
            rhasspy2HermesMqttHandleTimeout = data.rhasspy2HermesMqttHandleTimeout,
        )
    }

    operator fun invoke(data: IntentRecognitionConfigurationData): IntentDomainData {
        return IntentDomainData(
            option = data.intentDomainOption,
            isRhasspy2HermesHttpHandleWithRecognition = data.isRhasspy2HermesHttpHandleWithRecognition,
            rhasspy2HermesHttpHandleTimeout = data.rhasspy2HermesHttpHandleTimeout,
            rhasspy2HermesMqttHandleTimeout = data.rhasspy2HermesMqttHandleTimeout,
        )
    }

}