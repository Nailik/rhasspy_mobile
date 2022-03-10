package org.rhasspy.mobile.viewModels

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.services.native.NativeIndication
import org.rhasspy.mobile.settings.AppSettings

class HomeScreenViewModel : ViewModel() {
    private val logger = Logger.withTag(this::class.simpleName!!)

    init {
        AppSettings.languageOption.value.addObserver {
            StringDesc.localeType = StringDesc.LocaleType.Custom(it.code)
        }
        AppSettings.isWakeWordLightIndication.value.addObserver {
            if (it) {
                NativeIndication.displayOverAppsPermission()
            }
        }
    }

    fun saveAndApplyChanges() {
        logger.i { "saveAndApplyChanges" }

        GlobalData.saveAllChanges()
        // ForegroundService.stopServices()
        //   ForegroundService.startServices()
    }

    fun resetChanges() {
        logger.i { "resetChanges" }

        GlobalData.resetChanges()
    }

}