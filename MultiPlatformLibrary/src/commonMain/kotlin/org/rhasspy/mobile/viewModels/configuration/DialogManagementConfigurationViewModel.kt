package org.rhasspy.mobile.viewModels.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.data.DialogManagementOptions
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.dialogManager.DialogManagerLocalService
import org.rhasspy.mobile.services.dialogManager.DialogManagerServiceParams
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsService
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.viewModels.configuration.test.DialogManagementConfigurationTest

/**
 * ViewModel for Dialog Management Configuration
 *
 * Current Option
 * all Options as list
 */
class DialogManagementConfigurationViewModel : IConfigurationViewModel() {

    override val testRunner by inject<DialogManagementConfigurationTest>()

    //unsaved data
    private val _dialogManagementOption = MutableStateFlow(ConfigurationSettings.dialogManagementOption.value)

    //unsaved ui data
    val dialogManagementOption = _dialogManagementOption.readOnly

    override val isTestingEnabled = _dialogManagementOption.mapReadonlyState { it != DialogManagementOptions.Disabled }

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_dialogManagementOption, ConfigurationSettings.dialogManagementOption.data)
    )

    //all options
    val dialogManagementOptionsList = DialogManagementOptions::values

    //set new dialog management option
    fun selectDialogManagementOption(option: DialogManagementOptions) {
        _dialogManagementOption.value = option
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSettings.dialogManagementOption.value = _dialogManagementOption.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _dialogManagementOption.value = ConfigurationSettings.dialogManagementOption.value
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

    override fun runTest() = testRunner.startTest()

}