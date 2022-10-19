package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.DialogManagementOptions
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.ConfigurationSettings

/**
 * ViewModel for Dialog Management Configuration
 *
 * Current Option
 * all Options as list
 */
class DialogManagementConfigurationViewModel : ViewModel() {

    //unsaved data
    private val _dialogManagementOption = MutableStateFlow(ConfigurationSettings.dialogueManagementOption.value)

    //unsaved ui data
    val dialogueManagementOption = _dialogManagementOption.readOnly

    //all options
    val dialogManagementOptionsList = DialogManagementOptions::values

    //set new dialog management option
    fun selectDialogManagementOption(option: DialogManagementOptions) {
        _dialogManagementOption.value = option
    }

    /**
     * save data configuration
     */
    fun save() {
        ConfigurationSettings.dialogueManagementOption.data.value = _dialogManagementOption.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}