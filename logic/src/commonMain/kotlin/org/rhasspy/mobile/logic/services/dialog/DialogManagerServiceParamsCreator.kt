package org.rhasspy.mobile.logic.services.dialog

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.combineStateFlow

class DialogManagerServiceParamsCreator {

    private val updaterScope = CoroutineScope(Dispatchers.Default)
    private val paramsFlow = MutableStateFlow(getParams())

    operator fun invoke(): StateFlow<DialogManagerServiceParams> {

        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.dialogManagementOption.data,
                ConfigurationSetting.wakeWordOption.data,
                ConfigurationSetting.speechToTextOption.data,
                ConfigurationSetting.intentRecognitionOption.data,
                ConfigurationSetting.textAsrTimeout.data,
                ConfigurationSetting.intentRecognitionTimeout.data,
                ConfigurationSetting.recordingTimeout.data,
            ).onEach {
                paramsFlow.value = getParams()
            }
        }

        return paramsFlow
    }

    private fun getParams(): DialogManagerServiceParams {
        return DialogManagerServiceParams(
            option = ConfigurationSetting.dialogManagementOption.value,
            wakeWordOption = ConfigurationSetting.wakeWordOption.value,
            speechToTextOption = ConfigurationSetting.speechToTextOption.value,
            intentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value,
            asrTimeout = ConfigurationSetting.textAsrTimeout.value,
            intentRecognitionTimeout = ConfigurationSetting.intentRecognitionTimeout.value,
            recordingTimeout = ConfigurationSetting.recordingTimeout.value,
        )
    }

}