package org.rhasspy.mobile.viewModels

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.postValue
import org.rhasspy.mobile.settings.ConfigurationSetting

object GlobalData {
    private val logger = Logger.withTag("GlobalData")

    val settings: Settings = Settings()

    val allConfigurationSettings = mutableListOf<ConfigurationSetting<*>>()

    val unsavedChanges = MutableLiveData(false)

    fun updateUnsavedChanges() {
        logger.i { "updateUnsavedChanges" }

        unsavedChanges.value = allConfigurationSettings.any { it.isUnsaved }
    }

    fun saveAllChanges() {
        logger.i { "saveAllChanges" }

        allConfigurationSettings.forEach { it.save() }
        unsavedChanges.postValue(false)
    }

    fun resetChanges() {
        logger.i { "resetChanges" }

        allConfigurationSettings.forEach { it.reset() }
        unsavedChanges.postValue(false)
    }

}