package org.rhasspy.mobile.viewmodel.configuration.domains.intent

import org.rhasspy.mobile.data.data.toIntOrZero
import org.rhasspy.mobile.data.domain.IntentDomainData
import org.rhasspy.mobile.viewmodel.configuration.domains.intent.IntentDomainConfigurationViewState.IntentDomainConfigurationData
import kotlin.time.Duration.Companion.seconds

class IntentDomainConfigurationDataMapper {

    operator fun invoke(data: IntentDomainData): IntentDomainConfigurationData {
        return IntentDomainConfigurationData(
            intentDomainOption = data.option,
            isRhasspy2HermesHttpIntentHandleWithRecognition = data.isRhasspy2HermesHttpHandleWithRecognition,
            rhasspy2HermesHttpIntentHandlingTimeout = data.timeout.inWholeSeconds.toString(),
            timeout = data.timeout.inWholeSeconds.toString(),
        )
    }

    operator fun invoke(data: IntentDomainConfigurationData): IntentDomainData {
        return IntentDomainData(
            option = data.intentDomainOption,
            isRhasspy2HermesHttpHandleWithRecognition = data.isRhasspy2HermesHttpIntentHandleWithRecognition,
            rhasspy2HermesHttpIntentHandlingTimeout = data.timeout.toIntOrZero().seconds,
            timeout = data.timeout.toIntOrZero().seconds,
        )
    }

}