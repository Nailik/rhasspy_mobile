package org.rhasspy.mobile.viewModels.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.settings.AppSettings

class ProblemHandlingSettingsViewModel : ViewModel() {

    //unsaved ui data
    val isForceCancelEnabled = AppSettings.isForceCancelEnabled.data

    //set new intent recognition option
    fun toggleForceCancelEnabled(enabled: Boolean) {
        AppSettings.isForceCancelEnabled.value = enabled
    }

}