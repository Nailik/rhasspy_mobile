package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.combineStateNotEquals
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationContentViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.ServiceStateHeaderViewState

@Stable
data class IntentRecognitionConfigurationViewState(
    val intentRecognitionOptionList: ImmutableList<IntentRecognitionOption>,
    val intentRecognitionOption: IntentRecognitionOption,
    val isUseCustomIntentRecognitionHttpEndpoint: Boolean,
    val intentRecognitionHttpEndpoint: String
): IConfigurationContentViewState {

    companion object {
        fun getInitial() = IntentRecognitionConfigurationViewState(
            intentRecognitionOptionList = IntentRecognitionOption.values().toList().toImmutableList(),
            intentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value,
            isUseCustomIntentRecognitionHttpEndpoint = ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value,
            intentRecognitionHttpEndpoint = ConfigurationSetting.intentRecognitionHttpEndpoint.value
        )
    }

    override fun getEditViewState(serviceViewState: StateFlow<ServiceStateHeaderViewState>): IConfigurationEditViewState {
        return IConfigurationEditViewState(
            hasUnsavedChanges = !(intentRecognitionOption == ConfigurationSetting.intentRecognitionOption.value &&
                    isUseCustomIntentRecognitionHttpEndpoint == ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value &&
                    intentRecognitionHttpEndpoint == ConfigurationSetting.intentRecognitionHttpEndpoint.value),
            isTestingEnabled = intentRecognitionOption != IntentRecognitionOption.Disabled,
            serviceViewState = serviceViewState
        )
    }

    override fun save() {
        ConfigurationSetting.intentRecognitionOption.value = intentRecognitionOption
        ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value = isUseCustomIntentRecognitionHttpEndpoint
        ConfigurationSetting.intentRecognitionHttpEndpoint.value = intentRecognitionHttpEndpoint
    }

}