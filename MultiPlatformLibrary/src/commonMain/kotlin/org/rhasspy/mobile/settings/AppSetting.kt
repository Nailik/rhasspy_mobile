package org.rhasspy.mobile.settings

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.get
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.encodeValue
import com.russhwolf.settings.set
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import org.rhasspy.mobile.data.DataEnum
import org.rhasspy.mobile.viewModels.GlobalData


class AppSetting<T>(private val key: SettingsEnum, private val initial: T) {

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    val value = object : MutableLiveData<T>(readValue()) {
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

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    fun readValue(): T {
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

    var data: T
        get() = this.value.value
        set(newValue) {
            viewScope.launch {
                value.value = newValue
            }
        }

    companion object {
        private val viewScope = CoroutineScope(Dispatchers.Main)
    }

}