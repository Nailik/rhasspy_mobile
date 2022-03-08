package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.services.ForegroundService
import org.rhasspy.mobile.settings.AppSettings

class MainViewModel : ViewModel() {

    init {
        AppSettings.languageOption.value.addObserver {
            StringDesc.localeType = StringDesc.LocaleType.Custom(it.code)
        }
    }

    fun saveAndApplyChanges() {
        GlobalData.saveAllChanges()
        ForegroundService.stopServices()
        ForegroundService.startServices()
    }

    fun resetChanges() {
        GlobalData.resetChanges()
    }
}