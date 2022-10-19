package org.rhasspy.mobile.viewModels.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.AppSettings

class ProblemHandlingSettingsViewModel : ViewModel() {

    //unsaved data
    private val _isForceCancelEnabled = MutableStateFlow(AppSettings.isForceCancelEnabled.value)

    //unsaved ui data
    val isForceCancelEnabled = _isForceCancelEnabled.readOnly

    //set new intent recognition option
    fun toggleForceCancelEnabled(enabled: Boolean) {
        _isForceCancelEnabled.value = enabled
    }

    /**
     * save data configuration
     */
    fun save() {
        AppSettings.isForceCancelEnabled.data.value = _isForceCancelEnabled.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}