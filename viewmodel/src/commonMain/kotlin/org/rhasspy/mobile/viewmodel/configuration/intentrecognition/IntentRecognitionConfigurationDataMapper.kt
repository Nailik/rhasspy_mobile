package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import org.rhasspy.mobile.data.domain.IntentDomainData
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewState.IntentRecognitionConfigurationData

class IntentRecognitionConfigurationDataMapper {

    operator fun invoke(data: IntentDomainData): IntentRecognitionConfigurationData {
        return IntentRecognitionConfigurationData(
            intentRecognitionOption = data.option
        )
    }

    operator fun invoke(data: IntentRecognitionConfigurationData): IntentDomainData {
        return IntentDomainData(
            option = data.intentRecognitionOption
        )
    }

}