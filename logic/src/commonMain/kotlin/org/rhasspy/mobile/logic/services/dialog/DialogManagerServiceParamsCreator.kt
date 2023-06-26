package org.rhasspy.mobile.logic.services.dialog

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting

class DialogManagerServiceParamsCreator {

    operator fun invoke(): StateFlow<DialogManagerServiceParams> {
        return combineStateFlow(
            ConfigurationSetting.dialogManagementOption.data,
            ConfigurationSetting.wakeWordOption.data,
            ConfigurationSetting.speechToTextOption.data,
            ConfigurationSetting.intentRecognitionOption.data,
            ConfigurationSetting.textAsrTimeout.data,
            ConfigurationSetting.intentRecognitionTimeout.data,
            ConfigurationSetting.recordingTimeout.data,
        ).mapReadonlyState {
            getParams()
        }
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