package org.rhasspy.mobile.viewmodel.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.logic.combineAny
import org.rhasspy.mobile.logic.combineStateNotEquals
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.mapReadonlyState
import org.rhasspy.mobile.logic.readOnly
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.logic.settings.option.DialogManagementOption
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
    private val _dialogManagementOption =
        MutableStateFlow(ConfigurationSetting.dialogManagementOption.value)

    //unsaved ui data
    val dialogManagementOption = _dialogManagementOption.readOnly

    override val isTestingEnabled =
        _dialogManagementOption.mapReadonlyState { it != DialogManagementOption.Disabled }

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(
            _dialogManagementOption,
            ConfigurationSetting.dialogManagementOption.data
        )
    )

    //all options
    val dialogManagementOptionList = DialogManagementOption::values

    //set new dialog management option
    fun selectDialogManagementOption(option: DialogManagementOption) {
        _dialogManagementOption.value = option
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSetting.dialogManagementOption.value = _dialogManagementOption.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _dialogManagementOption.value = ConfigurationSetting.dialogManagementOption.value
    }

    override fun initializeTestParams() {
        get<DialogManagerServiceParams> {
            parametersOf(
                DialogManagerServiceParams(
                    option = _dialogManagementOption.value
                )
            )
        }
    }

}