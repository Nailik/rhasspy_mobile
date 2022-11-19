package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.data.DialogManagementOptions
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.state.ServiceState
import org.rhasspy.mobile.settings.ConfigurationSettings

/**
 * ViewModel for Dialog Management Configuration
 *
 * Current Option
 * all Options as list
 */
class DialogManagementConfigurationViewModel : ViewModel(), IConfigurationViewModel {

    //unsaved data
    private val _dialogManagementOption = MutableStateFlow(ConfigurationSettings.dialogManagementOption.value)

    //unsaved ui data
    val dialogManagementOption = _dialogManagementOption.readOnly

    override val isTestingEnabled = _dialogManagementOption.mapReadonlyState { it != DialogManagementOptions.Disabled }
    override val testState: StateFlow<List<ServiceState>> = MutableStateFlow(listOf())

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
    override fun save() {
        ConfigurationSettings.dialogManagementOption.value = _dialogManagementOption.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _dialogManagementOption.value = ConfigurationSettings.dialogManagementOption.value
    }

    /**
     * test unsaved data configuration
     */
    override fun test() {
        //TODO only when enabled
        //?? maybe record button -> test flow
    }

    override fun stopTest() {

    }

}