package org.rhasspy.mobile.settings

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.postValue
import dev.icerock.moko.mvvm.livedata.readOnly
import org.rhasspy.mobile.viewModels.GlobalData

/**
 * configuration settings do not change there saved value when value is changed,
 * internally unsaved value will be changed and on save it's saved to the value
 */
class ConfigurationSetting<T>(key: SettingsEnum, initial: T) : Setting<T>(key, initial) {

    init {
        GlobalData.allConfigurationSettings.add(this)
    }

    //if there are unsaved changes in this value
    private val isUnsaved = MutableLiveData(false)
    val hasUnsavedChange = isUnsaved.readOnly()

    /**
     * holds the unsaved value
     * updated unsaved changes accordingly if the new value is different or the same as the current saved value
     */
    val unsaved = object : MutableLiveData<T>(data.value) {
        override var value: T
            get() = super.value
            set(newValue) {
                if (super.value != newValue) {
                    super.value = newValue
                    if (data != newValue) {
                        //new value
                        isUnsaved.value = true
                        GlobalData.unsavedChanges.value = true
                    } else {
                        //set value back to saved
                        isUnsaved.value = false
                        GlobalData.updateUnsavedChanges()
                    }
                }
            }
    }

    /**
     * saves the current unsaved value and resets unsaved changes and updates the value
     */
    fun save() {
        //update unsaved changes
        isUnsaved.postValue(false)
        //update saved value
        data.value = unsaved.value
    }

    /**
     * reset unsaved changes to the current value
     */
    fun reset() {
        unsaved.value = data.value
    }

}