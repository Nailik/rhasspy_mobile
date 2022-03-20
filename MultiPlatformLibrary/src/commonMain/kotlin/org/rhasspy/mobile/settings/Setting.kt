package org.rhasspy.mobile.settings

import com.russhwolf.settings.get
import com.russhwolf.settings.set
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.postValue
import org.rhasspy.mobile.data.DataEnum
import org.rhasspy.mobile.viewModels.GlobalData

class Setting<T>(private val key: SettingsEnum, private val initial: T) {

    init {
        GlobalData.allSettings.add(this)
    }

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

    var data: T = value
     private set

    val unsaved = object : MutableLiveData<T>(value) {
        @Suppress("UNCHECKED_CAST")
        override var value: T
            get() = super.value
            set(newValue) {
                if (super.value != newValue) {
                    super.value = newValue
                    if (data != newValue) {
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
        unsavedChange.postValue(false)
        data = unsaved.value
    }

    fun reset() {
        unsaved.value = value
    }

    var unsavedData: T
        get() = this.unsaved.value
        set(newValue) {
            this.unsaved.value = newValue
        }

}