package org.rhasspy.mobile.settings

import com.russhwolf.settings.ExperimentalSettingsApi
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.postValue
import dev.icerock.moko.mvvm.livedata.readOnly
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
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
    private val unsaved = object : MutableLiveData<T>(readValue()) {
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
     * data used to get current saved value or to set value for unsaved changes
     */
    override var data: T
        get() = this.value.value
        set(newValue) {
            viewScope.launch {
                unsaved.value = newValue
            }
        }

    /**
     * data that's live updated to get current value readonly
     */
    var liveUnsavedData = unsaved.readOnly()

    /**
     * saves the current unsaved value and resets unsaved changes and updates the value
     */
    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    fun save() {
        //update unsaved changes
        isUnsaved.postValue(false)
        //update saved value
        value.postValue(unsaved.value)
    }

    /**
     * reset unsaved changes to the current value
     */
    fun reset() {
        unsaved.value = value.value
    }

}