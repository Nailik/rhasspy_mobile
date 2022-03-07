package org.rhasspy.mobile.settings

import com.russhwolf.settings.get
import com.russhwolf.settings.set
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import org.rhasspy.mobile.data.DataEnum
import org.rhasspy.mobile.viewModels.GlobalData

class Setting<T>(private val key: SettingsEnum, private val initial: T) {

    init {
        GlobalData.allSettings.add(this)
    }

    //unsaved
    val value: T
        get() {
            @Suppress("UNCHECKED_CAST")
            return when (initial) {
                is String -> GlobalData.settings[key.name, initial]
                is Int -> GlobalData.settings[key.name, initial]
                is Float -> GlobalData.settings[key.name, initial]
                is Boolean -> GlobalData.settings[key.name, initial]
                is DataEnum<*> -> initial.findValue(GlobalData.settings[key.name, initial.name])
                else -> throw RuntimeException()
            } as T
        }

    private var currentValue = value

    //saved
    val unsaved = object : MutableLiveData<T>(value) {
        @Suppress("UNCHECKED_CAST")
        override var value: T
            get() = super.value
            set(newValue) {
                if (super.value != newValue) {
                    super.value = newValue

                    if (currentValue != newValue) {
                        //new value
                        unsavedChange.value = true
                        GlobalData.unsavedChanges.value = true
                    } else {
                        //set value back to saved
                        unsavedChange.value = false
                        GlobalData.updateUnsavedChanges()
                    }

                }
            }
    }

    val unsavedChange = MutableLiveData(false)

    fun save() {
        when (initial) {
            is String -> GlobalData.settings[key.name] = unsaved.value as String
            is Int -> GlobalData.settings[key.name] = unsaved.value as Int
            is Float -> GlobalData.settings[key.name] = unsaved.value as Float
            is Boolean -> GlobalData.settings[key.name] = unsaved.value as Boolean
            is DataEnum<*> -> GlobalData.settings[key.name] = (unsaved.value as DataEnum<*>).name
            else -> throw RuntimeException()
        }
        unsavedChange.value = false
        currentValue = unsaved.value
    }

    fun reset() {
        unsaved.value = value
    }

}