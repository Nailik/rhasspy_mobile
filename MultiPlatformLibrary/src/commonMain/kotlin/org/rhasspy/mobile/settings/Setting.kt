package org.rhasspy.mobile.settings

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.get
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.encodeValue
import com.russhwolf.settings.set
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import org.rhasspy.mobile.data.DataEnum
import org.rhasspy.mobile.viewModels.GlobalData

abstract class Setting<T>(private val key: SettingsEnum, private val initial: T) {

    companion object {
        internal val viewScope = CoroutineScope(Dispatchers.Main)
    }

    /**
     * returns value as a mutable live data to observe it
     */
    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    internal val value = object : MutableLiveData<T>(readValue()) {
        override var value: T
            get():T = super.value
            set(newValue) {
                if (value != newValue) {
                    @Suppress("UNCHECKED_CAST")
                    when (initial) {
                        is String -> GlobalData.settings[key.name] = newValue as String
                        is Int -> GlobalData.settings[key.name] = newValue as Int
                        is Float -> GlobalData.settings[key.name] = newValue as Float
                        is Boolean -> GlobalData.settings[key.name] = newValue as Boolean
                        is DataEnum<*> -> GlobalData.settings[key.name] = (newValue as DataEnum<*>).name
                        is Set<*> -> GlobalData.settings.encodeValue(SetSerializer(String.serializer()), key.name, newValue as Set<String>)
                        else -> throw RuntimeException()
                    }
                    super.value = newValue
                }
            }
    }

    /**
     * reads current saved value
     */
    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    internal fun readValue(): T {
        @Suppress("UNCHECKED_CAST")
        return when (initial) {
            is String -> GlobalData.settings[key.name, initial]
            is Int -> GlobalData.settings[key.name, initial]
            is Float -> GlobalData.settings[key.name, initial]
            is Boolean -> GlobalData.settings[key.name, initial]
            is DataEnum<*> -> initial.findValue(GlobalData.settings[key.name, initial.name])
            is Set<*> -> GlobalData.settings.decodeValue(SetSerializer(String.serializer()), key.name, initial as Set<String>)
            else -> throw RuntimeException()
        } as T
    }

    /**
     * represents current saved data and when changed, updates data asynchronously
     */
    open var data: T
        get() = this.value.value
        set(newValue) {
            viewScope.launch {
                value.value = newValue
            }
        }
}