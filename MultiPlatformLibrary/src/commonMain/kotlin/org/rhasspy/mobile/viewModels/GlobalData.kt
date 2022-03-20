package org.rhasspy.mobile.viewModels

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.postValue
import org.rhasspy.mobile.settings.Setting

object GlobalData {
    private val logger = Logger.withTag(this::class.simpleName!!)

    val settings: Settings = Settings()

    val allSettings = mutableListOf<Setting<*>>()

    val unsavedChanges = MutableLiveData(false)

    fun updateUnsavedChanges() {
        logger.i { "updateUnsavedChanges" }

        unsavedChanges.value = allSettings.any { it.unsavedChange.value }
    }

    fun saveAllChanges() {
        logger.i { "saveAllChanges" }

        allSettings.forEach { it.save() }
        unsavedChanges.postValue(false)
    }

    fun resetChanges() {
        logger.i { "resetChanges" }

        allSettings.forEach { it.reset() }
        unsavedChanges.postValue(false)
    }

}