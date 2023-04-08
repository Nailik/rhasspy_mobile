package org.rhasspy.mobile.viewmodel.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.combineAny
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.combineStateNotEquals
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.configuration.event.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.test.DialogManagementConfigurationTest

/**
 * ViewModel for Dialog Management Configuration
 *
 * Current Option
 * all Options as list
 */
class DialogManagementConfigurationViewModel : IConfigurationViewModel() {

    override val testRunner by inject<DialogManagementConfigurationTest>()
    override val logType = LogType.DialogManagerService
    override val serviceState get() = get<DialogManagerService>().serviceState

    //unsaved data
    private val _dialogManagementOption = MutableStateFlow(ConfigurationSetting.dialogManagementOption.value)
    private val _textAsrTimeoutText = MutableStateFlow(ConfigurationSetting.textAsrTimeout.value.toString())
    private val _textAsrTimeout = MutableStateFlow(ConfigurationSetting.textAsrTimeout.value)
    private val _intentRecognitionTimeoutText = MutableStateFlow(ConfigurationSetting.intentRecognitionTimeout.value.toString())
    private val _intentRecognitionTimeout = MutableStateFlow(ConfigurationSetting.intentRecognitionTimeout.value)
    private val _recordingTimeoutText = MutableStateFlow(ConfigurationSetting.recordingTimeout.value.toString())
    private val _recordingTimeout = MutableStateFlow(ConfigurationSetting.recordingTimeout.value)

    //unsaved ui data
    val dialogManagementOption = _dialogManagementOption.readOnly
    val textAsrTimeoutText = _textAsrTimeoutText.readOnly
    val intentRecognitionTimeoutText = _intentRecognitionTimeoutText.readOnly
    val recordingTimeoutText = _recordingTimeoutText.readOnly

    fun updateTextAsrTimeout(connectionTimeout: String) {
        val text = connectionTimeout.replace("""[-,. ]""".toRegex(), "")
        _textAsrTimeoutText.value = text
        _textAsrTimeout.value = text.toLongOrNull() ?: 0L
    }

    fun updateIntentRecognitionTimeout(connectionTimeout: String) {
        val text = connectionTimeout.replace("""[-,. ]""".toRegex(), "")
        _intentRecognitionTimeoutText.value = text
        _intentRecognitionTimeout.value = text.toLongOrNull() ?: 0L
    }

    fun updateRecordingTimeout(connectionTimeout: String) {
        val text = connectionTimeout.replace("""[-,. ]""".toRegex(), "")
        _recordingTimeoutText.value = text
        _recordingTimeout.value = text.toLongOrNull() ?: 0L
    }

    private val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_dialogManagementOption, ConfigurationSetting.dialogManagementOption.data),
        combineStateNotEquals(_textAsrTimeout, ConfigurationSetting.textAsrTimeout.data),
        combineStateNotEquals(_intentRecognitionTimeout, ConfigurationSetting.intentRecognitionTimeout.data),
        combineStateNotEquals(_recordingTimeout, ConfigurationSetting.recordingTimeout.data)
    )

    override val configurationEditViewState = combineState(hasUnsavedChanges, _dialogManagementOption) { hasUnsavedChanges, dialogManagementOption ->
        IConfigurationViewState.IConfigurationEditViewState(
            hasUnsavedChanges = hasUnsavedChanges,
            isTestingEnabled = dialogManagementOption != DialogManagementOption.Disabled
        )
    }

    //all options
    val dialogManagementOptionList = DialogManagementOption::values

    //if local dialog management settings should be visible
    fun isLocalDialogManagementSettingsVisible(option: DialogManagementOption): Boolean {
        return option == DialogManagementOption.Local
    }

    //set new dialog management option
    fun selectDialogManagementOption(option: DialogManagementOption) {
        _dialogManagementOption.value = option
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSetting.dialogManagementOption.value = _dialogManagementOption.value
        ConfigurationSetting.textAsrTimeout.value = _textAsrTimeout.value
        ConfigurationSetting.intentRecognitionTimeout.value = _intentRecognitionTimeout.value
        ConfigurationSetting.recordingTimeout.value = _recordingTimeout.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _dialogManagementOption.value = ConfigurationSetting.dialogManagementOption.value
        _textAsrTimeout.value = ConfigurationSetting.textAsrTimeout.value
        _intentRecognitionTimeout.value = ConfigurationSetting.intentRecognitionTimeout.value
        _recordingTimeout.value = ConfigurationSetting.recordingTimeout.value
    }

    override fun initializeTestParams() {
        get<DialogManagerServiceParams> {
            parametersOf(
                DialogManagerServiceParams(
                    option = _dialogManagementOption.value,
                    asrTimeout = _textAsrTimeout.value,
                    intentRecognitionTimeout = _intentRecognitionTimeout.value,
                    recordingTimeout = _recordingTimeout.value,
                )
            )
        }
    }

}