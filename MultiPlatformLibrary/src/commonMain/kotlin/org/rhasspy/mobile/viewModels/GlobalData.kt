package org.rhasspy.mobile.viewModels

import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import org.rhasspy.mobile.settings.Setting

object GlobalData {

    val settings: Settings = Settings()

    val allSettings = mutableListOf<Setting<*>>()

    val unsavedChanges = MutableLiveData(false)

    fun updateUnsavedChanges() {
        unsavedChanges.value = allSettings.any { it.unsavedChange.value }
    }

    fun saveAllChanges() {
        allSettings.forEach { it.save() }
        unsavedChanges.value = false
    }

    fun resetChanges() {
        allSettings.forEach { it.reset() }
        unsavedChanges.value = false
    }

}