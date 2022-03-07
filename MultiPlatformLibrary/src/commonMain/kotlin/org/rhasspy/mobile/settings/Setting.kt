package org.rhasspy.mobile.settings

import com.russhwolf.settings.get
import com.russhwolf.settings.set
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import org.rhasspy.mobile.viewModels.GlobalData

class Setting<T>(private val key: SettingsEnum, private val initial: T) {

    //unsaved
    val value: T
        get() {
            @Suppress("UNCHECKED_CAST")
            return when (initial) {
                is String -> GlobalData.settings[key.name, initial]
                is Int -> GlobalData.settings[key.name, initial]
                is Float -> GlobalData.settings[key.name, initial]
                is Boolean -> GlobalData.settings[key.name, initial]
                is Enum<*> -> GlobalData.settings[key.name, initial.name]
                else -> throw RuntimeException()
            } as T
        }


    //saved
    val unsaved = object : MutableLiveData<T>(value) {
        @Suppress("UNCHECKED_CAST")
        actual override var value: T
            get() = super.value
            set(newValue) {
                if (super.value != newValue) {
                    super.value = newValue
                    unsavedChange.value = true
                }
            }
    }

    val unsavedChange = MutableLiveData(false)

    fun save() {
        unsavedChange.value = false
        when (initial) {
            is String -> GlobalData.settings[key.name] = unsaved.value as String
            is Int -> GlobalData.settings[key.name] = unsaved.value as Int
            is Float -> GlobalData.settings[key.name] = unsaved.value as Float
            is Boolean -> GlobalData.settings[key.name] = unsaved.value as Boolean
            is Enum<*> -> GlobalData.settings[key.name] = (unsaved.value as Enum<*>).name
            else -> throw RuntimeException()
        }
    }

}