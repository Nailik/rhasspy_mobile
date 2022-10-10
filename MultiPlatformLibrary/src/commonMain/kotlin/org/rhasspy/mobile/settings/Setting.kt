package org.rhasspy.mobile.settings

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.encodeValue
import com.russhwolf.settings.set
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import org.rhasspy.mobile.data.DataEnum

private val settings = Settings()

class Setting<T>(private val key: SettingsEnum, private val initial: T) {

    /**
     * data used to get current saved value or to set value for unsaved changes
     */
    val data = MutableStateFlow(readValue()).also { flow ->
        CoroutineScope(Dispatchers.Default).launch {
            flow.collect {
                saveValue(it)
            }
        }
    }

    val value: T get() = data.value

    /**
     * save current value
     */
    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    @Suppress("UNCHECKED_CAST")
    private fun saveValue(newValue: T) {
        when (initial) {
            is String -> settings[key.name] = newValue as String
            is Int -> settings[key.name] = newValue as Int
            is Float -> settings[key.name] = newValue as Float
            is Boolean -> settings[key.name] = newValue as Boolean
            is DataEnum<*> -> settings[key.name] = (newValue as DataEnum<*>).name
            is Set<*> -> settings.encodeValue(SetSerializer(String.serializer()), key.name, newValue as Set<String>)
            else -> throw RuntimeException()
        }
    }

    /**
     * reads current saved value
     */
    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    @Suppress("UNCHECKED_CAST")
    private fun readValue(): T {
        return when (initial) {
            is String -> settings[key.name, initial]
            is Int -> settings[key.name, initial]
            is Float -> settings[key.name, initial]
            is Boolean -> settings[key.name, initial]
            is DataEnum<*> -> initial.findValue(settings[key.name, initial.name])
            is Set<*> -> settings.decodeValue(SetSerializer(String.serializer()), key.name, initial as Set<String>)
            else -> throw RuntimeException()
        } as T
    }

}